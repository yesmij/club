package com.nagesoft.club.account;

import com.nagesoft.club.domain.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final JavaMailSender mailSender;

    private final PasswordEncoder passwordEncoder;

    @Transactional
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
        account.setEmailChecked(false);
        Account newAccount = accountRepository.save(account);
        log.info("new Account = {}", newAccount.getEmail());
        return newAccount;
    }

    public void accountConfirmMail(Account newAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("会員　登録　メール　確認");
        mailMessage.setText("/check-email?emailToken=" + newAccount.getEmailCheckToken() +
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
}
