package com.nagesoft.club.modules.account;

import com.nagesoft.club.modules.account.form.NicknameForm;
import com.nagesoft.club.modules.account.form.NotificationForm;
import com.nagesoft.club.modules.account.form.Profile;
import com.nagesoft.club.modules.account.form.SignUpForm;
import com.nagesoft.club.infra.config.AppProperties;
import com.nagesoft.club.modules.tag.Tag;
import com.nagesoft.club.modules.zone.Zone;
import com.nagesoft.club.infra.mail.EmailMessage;
import com.nagesoft.club.infra.mail.EmailService;
import com.nagesoft.club.modules.tag.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    //private final JavaMailSender mailSender;
    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final TagRepository tagRepository;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;

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

        Context context = new Context();
        context.setVariable("link", "/check-email-token?emailToken=" + newAccount.getEmailCheckToken() +
                "&email=" + newAccount.getEmail());
        context.setVariable("nickname", newAccount.getNickname());
        context.setVariable("linkName", "이메일 인증하기");
        context.setVariable("message", "스터디올래 서비스를 사용하려면 링크를 클릭하세요.");
        context.setVariable("host", appProperties.getHost());

        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(newAccount.getEmail())
                .subject("스터디올래, 회원 가입 인증")
                .message(message)
                .build();

//        SimpleMailMessage mailMessage = new SimpleMailMessage();
//        mailMessage.setTo(newAccount.getEmail());
//        mailMessage.setSubject("会員　登録　メール　確認");
//        mailMessage.setText("/check-email-token?emailToken=" + newAccount.getEmailCheckToken() +
//                "&email=" + newAccount.getEmail());
//        newAccount.setEmailSendAt(LocalDateTime.now());
        //mailSender.send(mailMessage);
        emailService.sendEmail(emailMessage);
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

        Account byEmail = accountRepository.findByEmail(email);

        Context context = new Context();
        context.setVariable("link", "/check-email-token?emailToken=" + byEmail.getEmailCheckToken() +
                "&email=" + byEmail.getEmail());
        context.setVariable("nickname", byEmail.getNickname());
        context.setVariable("linkName", "이메일 인증하기");
        context.setVariable("message", "스터디올래 서비스를 사용하려면 링크를 클릭하세요.");
        context.setVariable("host", appProperties.getHost());

        String message = templateEngine.process("mail/simple-link", context);


        EmailMessage emailMessage = EmailMessage.builder()
                .to(byEmail.getEmail())
                .subject("스터디올래, 회원 가입 인증")
                .message(message)
                .build();

//        Account emailAccount = accountRepository.findByEmail(email);
//        SimpleMailMessage mailMessage = new SimpleMailMessage();
//        mailMessage.setTo(email);
//        mailMessage.setSubject("LogIn By Email");
//        mailMessage.setText("/login-by-email?emailToken=" + emailAccount.getEmailCheckToken() +
//                "&email=" + email);
//        emailAccount.setEmailSendAt(LocalDateTime.now());
        //mailSender.send(mailMessage);
        emailService.sendEmail(emailMessage);
    }

    public void addTag(Account account, Tag tag) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getTagSet().add(tag));
//        byId.getTagSet().add(tag);
//        accountRepository.save(byId);
    }

    public void tagRemove(Account account, Tag tag) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getTagSet().remove(tag));
//        byId.getTagSet().remove(tag);
    }

    public Set<Tag> getTags(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        return byId.orElseThrow().getTagSet();
//        return byId.getTagSet();
    }

    public List<Tag> getWhitelistTags() {
        return tagRepository.findAll();
    }

    public Set<Zone> getZones(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        return byId.orElseThrow().getZones();
    }

    public void addZone(Account account, Zone zone) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getZones().add(zone));
    }

    public void removeZone(Account account, Zone zone) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getZones().remove(zone));
//        byId.getZones().remove(zone);
//        accountRepository.save(byId);
    }
}
