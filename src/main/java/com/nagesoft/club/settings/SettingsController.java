package com.nagesoft.club.settings;

import com.nagesoft.club.account.AccountRepository;
import com.nagesoft.club.account.CurrentUser;
import com.nagesoft.club.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class SettingsController {

    private final AccountRepository accountRepository;

    @GetMapping("/settings/profile")
    public String profileUpdate(@CurrentUser Account account, Model model) {

//        Account byNickname = accountRepository.findByNickname(account.getNickname());
        model.addAttribute(new Profile(account.getBio(), account.getUrl(), account.getOccupation(), account.getLocation()));
        model.addAttribute(account);

        return "settings/profile";
    }
}
