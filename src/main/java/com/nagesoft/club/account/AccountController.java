package com.nagesoft.club.account;

import com.nagesoft.club.ConsoleMailSender;
import com.nagesoft.club.domain.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Slf4j
@Controller
public class AccountController {

    private AccountService accountService;
    private SignUpFormValidator signUpFormValidator;
    private JavaMailSender mailSender;

    @InitBinder("signUpForm")
    public void init(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }


    public AccountController(AccountService accountService, SignUpFormValidator signUpFormValidator, JavaMailSender mailSender) {
        this.accountService = accountService;
        this.signUpFormValidator = signUpFormValidator;
        this.mailSender = mailSender;
    }


    @GetMapping("/sign-up")
    public String signUpForm(Model model) {
        model.addAttribute("signUpForm", new SignUpForm());
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpSave(@Valid @ModelAttribute SignUpForm signUpForm, Errors error) {
        if(error.hasErrors()) {
            return "account/sign-up";
        }

        // 메일 중복 & 닉네임 중복 검증

       // 회원 가입
        Account account = new Account();
        account.setNickname(signUpForm.getNickname());
        account.setEmail(signUpForm.getEmail());
        account.setPassword(signUpForm.getPassword());  // todo password Encoding
        account.setEmailChecked(false);
        Account newAccount = accountService.save(account);
        log.info("new Account = {}", newAccount.getEmail());

        // 메일 발송
        newAccount.createEmailToken();                  // todo [search] DB에 저장은 언제?
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("会員　登録　メール　確認");
        mailMessage.setText("/check-email-token?emailToken=" + newAccount.getEmailCheckToken() +
                "&email=" + newAccount.getEmail());
        mailSender.send(mailMessage);

        return "redirect:/";
    }
}
