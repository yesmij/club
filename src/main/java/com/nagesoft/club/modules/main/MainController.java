package com.nagesoft.club.modules.main;

import com.nagesoft.club.modules.account.Account;
import com.nagesoft.club.modules.account.AccountRepository;
import com.nagesoft.club.modules.account.AccountService;
import com.nagesoft.club.modules.account.CurrentAccount;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@Controller
public class MainController {

    private final AccountRepository accountRepository;
    private final AccountService accountService;

    @GetMapping("/")
    public String main(@CurrentAccount Account account, Model model) {
        if(account != null) {
            model.addAttribute(account);
        }
        return "index";
    }

    @GetMapping("/login")
    public String loginForm(@CurrentAccount Account account, Model model) {
        if(account != null) {
            model.addAttribute(account);
            return "index";
        }
        return "login";
    }

    @PostMapping("/login")
    public String loginPro(@RequestParam String username, @RequestParam String password, Model model) {

        Account account = (Account) accountService.loadUserByUsername(username);
        System.out.println("account = " + account);

        return "index";
    }


    @GetMapping("/email-login")
    public String emailLoginForm() {
        return "account/email-login";
    }

    @GetMapping("/nologin")
    public String noLogin() {
        return "nologin";
    }

    @PostMapping("/email-login")
    public String emailLoginSend(@RequestParam String email, RedirectAttributes attributes, Model model) {
        if(Strings.isEmpty(email) || !accountRepository.existsByEmail(email)) {
            model.addAttribute("error", "해당 사용자가 없스무니다!");
            model.addAttribute("email");
            return "account/email-login";
        }
        accountService.emailLoginSend(email);
        attributes.addFlashAttribute("message", "메일 전송을 했습니다. 로그인해주세요.");
        return "redirect:/email-login";
    }

    @GetMapping("/login-by-email")
    public String emailLoginProcess(@RequestParam String emailToken, @RequestParam String email, Model model) {
        if(email == null || emailToken == null) {
            model.addAttribute("error", "email or token error");
            return "account/logged-in-by-email";
        }

        Account accountByEmail = accountRepository.findByEmail(email);
        if(accountByEmail == null || !emailToken.equals(accountByEmail.getEmailCheckToken()) ) {
            model.addAttribute("error", "email or token error");
            return "account/logged-in-by-email";
        }

        accountService.login(accountByEmail);
        return "account/logged-in-by-email";
    }
}
