package com.nagesoft.club.main;

import com.nagesoft.club.account.CurrentUser;
import com.nagesoft.club.domain.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String main(@CurrentUser Account account, Model model) {
        if(account != null) {
            model.addAttribute(account);
        }

        return "index";
    }
}
