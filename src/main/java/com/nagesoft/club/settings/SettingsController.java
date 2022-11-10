package com.nagesoft.club.settings;

import com.nagesoft.club.account.AccountService;
import com.nagesoft.club.account.CurrentUser;
import com.nagesoft.club.domain.Account;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;
    private final NicknameFormValidator nicknameFormValidator;
//    private final PasswordFormValidator passwordFormValidator;

    @InitBinder("passwordForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(new PasswordFormValidator());
    }

    @InitBinder("nicknameForm")
    public void initBinderForNickname(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(nicknameFormValidator);
    }

    @GetMapping("/settings/profile")
    public String profileUpdate(@CurrentUser Account account, Model model) {

//        Account byNickname = accountRepository.findByNickname(account.getNickname());
//        model.addAttribute(new Profile(account));
        model.addAttribute(modelMapper.map(account, Profile.class));
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

    @GetMapping("/settings/notifications")
    public String notiForm(@CurrentUser Account account, Model model) {
//        model.addAttribute(new NotificationForm(account));
        model.addAttribute(modelMapper.map(account, NotificationForm.class));
        model.addAttribute(account);
        return "settings/notifications";
    }

    @PostMapping("/settings/notifications")
    public String notiSave(@CurrentUser Account account, Model model, @Valid NotificationForm notificationForm, RedirectAttributes attribute, Errors errors) {
        if(errors.hasErrors()) {
            model.addAttribute(account);
            return "settings/notifications";
        }

        accountService.updateNotifications(account, notificationForm);
        model.addAttribute(account);
        attribute.addFlashAttribute("message", "notification을 수정했습니다.");

        return "redirect:/settings/notifications";
    }

    @GetMapping("/settings/account")
    public String accountForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, NicknameForm.class));
        return "settings/account";
    }

    @PostMapping("/settings/account")
    public String accountSave(@CurrentUser Account account, @Valid NicknameForm nicknameForm, Errors errors, RedirectAttributes attributes, Model model) {
        if(errors.hasErrors()) {
            model.addAttribute("error", "닉네임 정보를 제대로 입력해주세요");
            return "settings/account";
        }
        accountService.updateNickname(account, nicknameForm);
        attributes.addFlashAttribute("message", "닉네임을 변경했습니다.");
        return "redirect:/settings/account";
    }

}
