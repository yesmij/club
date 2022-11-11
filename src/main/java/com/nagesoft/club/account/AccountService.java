package com.nagesoft.club.account;

import com.nagesoft.club.domain.Account;
import com.nagesoft.club.settings.NicknameForm;
import com.nagesoft.club.settings.NotificationForm;
import com.nagesoft.club.settings.Profile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final JavaMailSender mailSender;

    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public Account accountCreationProcess(SignUpForm signUpForm) {
        Account newAccount = createAccount(signUpForm);

        // 메일 발송
        newAccount.createEmailToken();                  // transaction으로 DB저장 (detached vs active)
        accountConfirmMail(newAccount);

        return newAccount;
    }

    private Account createAccount(SignUpForm signUpForm) {
        Account account = new Account();
        //account.passwordEncode(signUpForm.getPassword());
        account.setNickname(signUpForm.getNickname());
        account.setEmail(signUpForm.getEmail());
        account.setPassword(passwordEncoder.encode(signUpForm.getPassword()));  // todo (확인) 인코딩을 여기서 해야 하는지?
        account.setEmailVerified(false);
        Account newAccount = accountRepository.save(account);
        log.info("new Account = {}", newAccount.getEmail());
        return newAccount;
    }

    public void accountConfirmMail(Account newAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("会員　登録　メール　確認");
        mailMessage.setText("/check-email-token?emailToken=" + newAccount.getEmailCheckToken() +
                "&email=" + newAccount.getEmail());
        newAccount.setEmailSendAt(LocalDateTime.now());
        mailSender.send(mailMessage);
    }

    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
//                account.getNickname(),
                new UserAccount(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(emailOrNickname);
        if(account == null) {
            account = accountRepository.findByNickname(emailOrNickname);
        }

        if(account == null) {
            new UsernameNotFoundException(emailOrNickname);
        }
        return new UserAccount(account);
    }

    public void completeSignUp(Account account) {
        account.completeSignUp();
    }

    public void updateProfile(Account account, Profile profile) {
        modelMapper.map(profile, account);
//        account.setBio(profile.getBio());
//        account.setLocation(profile.getLocation());
//        account.setOccupation(profile.getOccupation());
//        account.setUrl(profile.getUrl());
//        account.setProfileImage(profile.getProfileImage());
        accountRepository.save(account);
    }

    public void updatePassword(Account account, String changedPassword) {
        account.setPassword(passwordEncoder.encode(changedPassword));
        accountRepository.save(account);
    }

    public void updateNotifications(Account account, NotificationForm notificationForm) {
        modelMapper.map(notificationForm, account);
//        account.setStudyCreatedByEmail(notificationForm.isStudyCreatedByEmail());
//        account.setStudyCreatedByWeb(notificationForm.isStudyCreatedByWeb());
//        account.setStudyEnrollmentResultByEmail(notificationForm.isStudyEnrollmentResultByEmail());
//        account.setStudyEnrollmentResultByWeb(notificationForm.isStudyEnrollmentResultByWeb());
//        account.setStudyUpdatedByEmail(notificationForm.isStudyUpdatedByEmail());
//        account.setStudyUpdatedByWeb(notificationForm.isStudyUpdatedByWeb());
        accountRepository.save(account);

    }

    public void updateNickname(Account account, NicknameForm nicknameForm) {
        modelMapper.map(nicknameForm, account);
        accountRepository.save(account);
        login(account);
    }

    public void emailLoginSend(String email) {

        Account emailAccount = accountRepository.findByEmail(email);
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("LogIn By Email");
        mailMessage.setText("/login-by-email?emailToken=" + emailAccount.getEmailCheckToken() +
                "&email=" + email);
        emailAccount.setEmailSendAt(LocalDateTime.now());
        mailSender.send(mailMessage);
    }
}
