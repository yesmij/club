package com.nagesoft.club.study;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nagesoft.club.account.CurrentAccount;
import com.nagesoft.club.domain.Account;
import com.nagesoft.club.domain.Study;
import com.nagesoft.club.domain.Tag;
import com.nagesoft.club.settings.form.TagForm;
import com.nagesoft.club.study.form.StudyDescriptionForm;
import com.nagesoft.club.tag.TagRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
public class StudySettingsController {

    private final StudyService studyService;
    private final StudyRepository studyRepository;
    private final ModelMapper modelMapper;
    private final TagRepository tagRepository;
    private final ObjectMapper objectMapper;

    @GetMapping("/study/{path}/settings/description")
    public String settingForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyToUpdate(path, account);

        model.addAttribute(account);
        model.addAttribute(study);
       // model.addAttribute(new StudyDescriptionForm().builder().shortDescription(study.getShortDescription()).fullDescription(study.getFullDescription()).build());
        model.addAttribute(modelMapper.map(study, StudyDescriptionForm.class));
        return "study/settings/description";
    }

    @PostMapping("/study/{path}/settings/description")
    public String settingDescription(@CurrentAccount Account account, @PathVariable String path, @Valid StudyDescriptionForm studyDescriptionForm,
                                     Errors errors, Model model, RedirectAttributes attributes) {

        Study study = studyService.getStudyToUpdate(path, account);
        if(errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(study);
            return "study/settings/description";
        }

        studyService.updateDescription(study, studyDescriptionForm);
        //studyService.updateDescription(path, studyDescriptionForm);
        attributes.addFlashAttribute("message", "수정했습니다.");
        //System.out.println("Controller : study.Desc = " + study.getFullDescription());
        model.addAttribute(account);
        return "redirect:/study/" + study.getEncodePath() + "/settings/description";
    }

    @GetMapping("/study/{path}/settings/banner")
    public String settingBanner(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyToUpdate(path, account);
        model.addAttribute(study);
        model.addAttribute(account);
        return "study/settings/banner";
    }

    @PostMapping("/study/{path}/settings/banner")
    public String settingBannerSave(@CurrentAccount Account account, @PathVariable String path,
                                    RedirectAttributes attributes, @RequestParam String image) {
        Study study = studyService.getStudyToUpdate(path, account);
        studyService.updateBanner(study, image);
        attributes.addFlashAttribute("message", "배너는 수정했습니다.");
        return "redirect:/study/" + study.getEncodePath() + "/settings/banner";
    }

    @PostMapping("/study/{path}/settings/banner/enable")
    public String settingEnableBanner(@CurrentAccount Account account, @PathVariable String path,
                                      RedirectAttributes attributes) {
        Study study = studyService.getStudyToUpdate(path, account);
        studyService.updateEnable(study, true);
        attributes.addFlashAttribute("message", "배너를 활성화했습니다.");
        return "redirect:/study/" + study.getEncodePath() + "/settings/banner";
    }

    @PostMapping("/study/{path}/settings/banner/disable")
    public String settingDisableBanner(@CurrentAccount Account account, @PathVariable String path,
                                      RedirectAttributes attributes) {
        Study study = studyService.getStudyToUpdate(path, account);
        studyService.updateEnable(study, false);
        attributes.addFlashAttribute("message", "배너를 비활성화했습니다.");
        return "redirect:/study/" + study.getEncodePath() + "/settings/banner";
    }

    @GetMapping("/study/{path}/settings/tags")
    public String tagsForm(@CurrentAccount Account account, @PathVariable String path, Model model) throws JsonProcessingException {
        Study study = studyService.getStudyToUpdate(path, account);

        List<Tag> tagWhitelist = studyService.getTagWhitelist();
        model.addAttribute("whitelist", objectMapper.writeValueAsString(tagWhitelist));

        Set<Tag> tags = studyService.getStudyTags(study);
        model.addAttribute("tags", tags.stream().map(Tag::getTitle).collect(Collectors.toList()));
        model.addAttribute(account);
        model.addAttribute(study);

        return "study/settings/tags";
    }

    @ResponseBody
    @PostMapping("/study/{path}/settings/tags/add")
    public ResponseEntity addTagToStudy(@CurrentAccount Account account, @PathVariable String path, @RequestBody TagForm tagForm) {
        Study study = studyService.getStudyToUpdate(path, account);
        //Tag tag = Tag.builder().title(tagTitle).build();

        studyService.addTagToStudy(study, tagForm.getTagTitle());

        // todo study refresh??  & Query Tunning
//        List<Tag> tagWhitelist = studyService.getTagWhitelist();
//        model.addAttribute("whitelist", objectMapper.writeValueAsString(tagWhitelist));
//
//        Set<Tag> tags = studyService.getStudyTags(study);
//        model.addAttribute("tags", tags.stream().map(Tag::getTitle).collect(Collectors.toList()));

        return ResponseEntity.ok().build();
    }

    @ResponseBody
    @PostMapping("/study/{path}/settings/tags/remove")
    public ResponseEntity removeTagToStudy(@CurrentAccount Account account, @PathVariable String path, @RequestBody TagForm tagForm) {
        Study study = studyService.getStudyToUpdate(path, account);
        studyService.removeTagToStudy(study, tagForm.getTagTitle());

        return ResponseEntity.ok().build();
    }
}
