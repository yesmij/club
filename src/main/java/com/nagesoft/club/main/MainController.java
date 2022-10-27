package com.nagesoft.club.main;

import com.nagesoft.club.account.AccountRepository;
import com.nagesoft.club.account.AccountService;
import com.nagesoft.club.account.CurrentUser;
import com.nagesoft.club.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
public class MainController {

    private final AccountRepository accountRepository;
    private final AccountService accountService;

    @GetMapping("/")
    public String main(@CurrentUser Account account, Model model) {
        if(account != null) {
            model.addAttribute(account);
        }
        return "index";
    }

    @GetMapping("/login")
    public String loginForm(@CurrentUser Account account, Model model) {
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
}
