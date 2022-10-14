package com.nagesoft.club.account;

import com.nagesoft.club.domain.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class AccountController {

    private AccountService accountService;
    private SignUpFormValidator signUpFormValidator;


    public AccountController(AccountService accountService, SignUpFormValidator signUpFormValidator) {
        this.accountService = accountService;
        this.signUpFormValidator = signUpFormValidator;
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
        account.setPassword(signUpForm.getPassword());
        account.setEmailChecked(false);
        accountService.save(account);

        // 메일 발송

        return "redirect:/";
    }
}
