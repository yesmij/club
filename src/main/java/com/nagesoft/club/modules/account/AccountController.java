package com.nagesoft.club.modules.account;

import com.nagesoft.club.modules.account.form.SignUpForm;
import com.nagesoft.club.modules.account.validator.SignUpFormValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Slf4j
@Controller
public class AccountController {

    private AccountService accountService;
    private SignUpFormValidator signUpFormValidator;
    private AccountRepository accountRepository;

    @InitBinder("signUpForm")
    public void init(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    public AccountController(AccountService accountService, SignUpFormValidator signUpFormValidator, AccountRepository accountRepository) {
        this.accountService = accountService;
        this.signUpFormValidator = signUpFormValidator;
        this.accountRepository = accountRepository;
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
        Account account = accountService.accountCreationProcess(signUpForm);

        // 자동 로그인 처리
        accountService.login(account);

        return "redirect:/";
    }

    @GetMapping("/check-email-token")
    public String checkMail(@RequestParam(required = false) String emailToken, @RequestParam String email, Model model) {
        System.out.println("email = " + email);
        if(email == null || emailToken == null) {
            model.addAttribute("error", "token or email error");
            model.addAttribute("email", email);
            return "account/checked-email-token";
        }

        // 이메일 통한 회원 검사
        Account account = accountRepository.findByEmail(email);
        if(account == null) {
            model.addAttribute("error", "email error");
            return "account/checked-email-token";
        }
        // 이메일 토큰 검사
        if(!emailToken.equals(account.getEmailCheckToken())) {
            model.addAttribute("error", "token error");
            return "account/checked-email-token";
        }

        //account.completeSignUp();  // todo (확인) 트랜잭션 대상인지 확인 필요!!
        accountService.completeSignUp(account);

        model.addAttribute("numberOfCount", accountRepository.count());
        model.addAttribute("nickname", account.getNickname());

        // 자동 로그인 처리
        accountService.login(account);
        return "account/checked-email-token";
    }

    @RequestMapping("/check-email")
    public String checkEmail(@CurrentAccount Account account, Model model) {
        if(account != null) {
            model.addAttribute(account);
        }
        return "account/check-email";

    }


    @GetMapping("/resend-confirm-email")
    public String resend(@CurrentAccount Account account, Model model) {
        if(account == null ) {
            model.addAttribute("error", "token or email error");
            return "account/checked-email-token";
        }

        // 인증 이메일 다시 전송 가능한지 체크
//        if(LocalDateTime.now().isBefore(account.getEmailSendAt().plusHours(1))) {
        if(LocalDateTime.now().isBefore(account.getEmailSendAt().plusSeconds(10))) {
            model.addAttribute("error", "메일 발송 후 1시간이 지나지 않았습니다. 다시 확인해주세요.");
            return "account/check-email";
        } else {
            accountService.accountConfirmMail(account);
        }

        // 보낼 수 있으면 전송 , 첫 페이지 링크

        // 보낼 수 없으면, 에러 메시지를 담아서, 이메일 보낸 페이지로 전송

        return "redirect:/";
    }

    @GetMapping("/profile/{nickname}")
    public String profileView(@PathVariable String nickname, @CurrentAccount Account account, Model model) {

        // nickname 사용자 확인 -> not IllegalArgumentException
        Account byNickname = accountRepository.findByNickname(nickname);
        if(byNickname == null) {
            throw new IllegalArgumentException(nickname + "사용자가 없습니다.");
        }

//        if(byNickname.equals(account)) {
            model.addAttribute("isOwner", byNickname.equals(account));
//        }
        model.addAttribute(byNickname);

        // nickname 사용자와 현재 유저가 맞는지 확인 -> isOwner & nickname

        return "account/profile";
    }
}
