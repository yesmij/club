package com.nagesoft.club.settings;

import com.nagesoft.club.account.AccountService;
import com.nagesoft.club.account.CurrentUser;
import com.nagesoft.club.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@RequiredArgsConstructor
@Controller
public class SettingsController {

    private final AccountService accountService;

    @InitBinder("passwordForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(new PasswordFormValidator());
    }

    @GetMapping("/settings/profile")
    public String profileUpdate(@CurrentUser Account account, Model model) {

//        Account byNickname = accountRepository.findByNickname(account.getNickname());
        model.addAttribute(new Profile(account));
        model.addAttribute(account);

        return "settings/profile";
    }

    @PostMapping("/settings/profile")
    public String profileUpdateSave(@CurrentUser Account account, @Valid @ModelAttribute Profile profile, Errors errors, Model model, RedirectAttributes redirectAttributes) {

        if(errors.hasErrors()) {
            model.addAttribute(profile);
            model.addAttribute(account);
            return "settings/profile";
        }

        accountService.updateProfile(account, profile);
//        model.addAttribute(profile);
//        model.addAttribute(account);
        redirectAttributes.addFlashAttribute("message", "정보를 수정했습니다.");

//        return "settings/profile";
        return "redirect:/settings/profile";
    }

    @GetMapping("/settings/password")
    public String passwordForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute("passwordForm", new PasswordForm());
        return "settings/password";
    }

    @PostMapping("/settings/password")
    public String passwordUpdate(@CurrentUser Account account, @ModelAttribute @Valid PasswordForm passwordForm
                , Errors errors, RedirectAttributes attributes, Model model) {
        if(errors.hasErrors()){
            model.addAttribute("account");
            return "settings/password";
        }

        accountService.updatePassword(account, passwordForm.getNewPassword());
        attributes.addFlashAttribute("message", "패스워드를 변경했습니다");
        return "redirect:/settings/password";
    }
}
