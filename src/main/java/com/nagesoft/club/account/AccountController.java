package com.nagesoft.club.account;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {

    @GetMapping("/sign-up")
    public String singUp() {
        return "account/sign-up";
    }
}
