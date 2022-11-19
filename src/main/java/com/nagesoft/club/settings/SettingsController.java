package com.nagesoft.club.settings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nagesoft.club.account.AccountRepository;
import com.nagesoft.club.account.AccountService;
import com.nagesoft.club.account.CurrentAccount;
import com.nagesoft.club.account.TagRepository;
import com.nagesoft.club.domain.Account;
import com.nagesoft.club.domain.Tag;
import com.nagesoft.club.settings.form.*;
import com.nagesoft.club.settings.validator.NicknameFormValidator;
import com.nagesoft.club.settings.validator.PasswordFormValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
public class SettingsController {

    private final AccountService accountService;
    private final ModelMapper modelMapper;
    private final NicknameFormValidator nicknameFormValidator;
    private final TagRepository tagRepository;
    private final AccountRepository accountRepository;
    private final ObjectMapper objectMapper;
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
    public String profileUpdate(@CurrentAccount Account account, Model model) {

//        Account byNickname = accountRepository.findByNickname(account.getNickname());
//        model.addAttribute(new Profile(account));
        model.addAttribute(modelMapper.map(account, Profile.class));
        model.addAttribute(account);

        return "settings/profile";
    }

    @PostMapping("/settings/profile")
    public String profileUpdateSave(@CurrentAccount Account account, @Valid @ModelAttribute Profile profile, Errors errors, Model model, RedirectAttributes redirectAttributes) {

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
    public String passwordForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute("passwordForm", new PasswordForm());
        return "settings/password";
    }

    @PostMapping("/settings/password")
    public String passwordUpdate(@CurrentAccount Account account, @ModelAttribute @Valid PasswordForm passwordForm
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
    public String notiForm(@CurrentAccount Account account, Model model) {
//        model.addAttribute(new NotificationForm(account));
        model.addAttribute(modelMapper.map(account, NotificationForm.class));
        model.addAttribute(account);
        return "settings/notifications";
    }

    @PostMapping("/settings/notifications")
    public String notiSave(@CurrentAccount Account account, Model model, @Valid NotificationForm notificationForm, RedirectAttributes attribute, Errors errors) {
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
    public String accountForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, NicknameForm.class));
        return "settings/account";
    }

    @PostMapping("/settings/account")
    public String accountSave(@CurrentAccount Account account, @Valid NicknameForm nicknameForm, Errors errors, RedirectAttributes attributes, Model model) {
        if(errors.hasErrors()) {
            model.addAttribute("error", "닉네임 정보를 제대로 입력해주세요");
            return "settings/account";
        }
        accountService.updateNickname(account, nicknameForm);
        attributes.addFlashAttribute("message", "닉네임을 변경했습니다.");
        return "redirect:/settings/account";
    }

    @GetMapping("/settings/tags")
    public String tagForm(@CurrentAccount Account account, Model model) throws JsonProcessingException {
        Set<Tag> tags = accountService.getTags(account);
        List<Tag> whitelistTags = accountService.getWhitelistTags();

        model.addAttribute("whitelist", objectMapper.writeValueAsString(whitelistTags));
        model.addAttribute("account", account);
        model.addAttribute("tags", tags.stream().map(Tag::getTitle).collect(Collectors.toList()));
        return "settings/tags";
    }

    @ResponseBody
    @PostMapping("/settings/tags/remove")
    public ResponseEntity tagRemove(@CurrentAccount Account account, @RequestBody TagForm tagForm) {
        Tag tag = tagRepository.findByTitle(tagForm.getTagTitle());
        if(tag == null) {
            return ResponseEntity.badRequest().build();
        }
        accountService.tagRemove(account, tag);
        return ResponseEntity.ok().build();
    }

    @ResponseBody
    @PostMapping("/settings/tags/add")
    public ResponseEntity tagSave(@CurrentAccount Account account, @RequestBody TagForm tagForm) {
        Tag tag = tagRepository.findByTitle(tagForm.getTagTitle());
        System.out.println("tag1 = " + tag);
        if(tag == null) {
            tag = tagRepository.save(Tag.builder().title(tagForm.getTagTitle()).build());
            System.out.println("tag2 = " + tag);
        }
        accountService.addTag(account, tag);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/settings/zones")
    public String updateZonesForm(@CurrentAccount Account account, Model model) throws JsonProcessingException {
        return "settings/zone";
    }

    @PostMapping("/settings/zones" + "/add")
    @ResponseBody
//    public ResponseEntity addZone(@CurrentAccount Account account, @RequestBody ZoneForm zoneForm) {
    public ResponseEntity addZone(@CurrentAccount Account account) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/settings/zones" + "/remove")
    @ResponseBody
//    public ResponseEntity removeZone(@CurrentAccount Account account, @RequestBody ZoneForm zoneForm) {
    public ResponseEntity removeZone(@CurrentAccount Account account) {

            return ResponseEntity.ok().build();
    }
}
