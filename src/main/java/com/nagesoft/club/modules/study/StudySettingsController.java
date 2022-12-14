package com.nagesoft.club.modules.study;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nagesoft.club.modules.account.Account;
import com.nagesoft.club.modules.account.CurrentAccount;
import com.nagesoft.club.modules.tag.Tag;
import com.nagesoft.club.modules.zone.Zone;
import com.nagesoft.club.modules.tag.TagForm;
import com.nagesoft.club.modules.study.form.StudyDescriptionForm;
import com.nagesoft.club.modules.tag.TagRepository;
import com.nagesoft.club.modules.tag.TagService;
import com.nagesoft.club.modules.zone.ZoneForm;
import com.nagesoft.club.modules.zone.ZoneRepository;
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
    private final TagService tagService;
    private final ZoneRepository zoneRepository;

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
        attributes.addFlashAttribute("message", "??????????????????.");
        //System.out.println("Controller : study.Desc = " + study.getFullDescription());
//        model.addAttribute(account);
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
        attributes.addFlashAttribute("message", "????????? ??????????????????.");
        return "redirect:/study/" + study.getEncodePath() + "/settings/banner";
    }

    @PostMapping("/study/{path}/settings/banner/enable")
    public String settingEnableBanner(@CurrentAccount Account account, @PathVariable String path,
                                      RedirectAttributes attributes) {
        Study study = studyService.getStudyToUpdate(path, account);
        studyService.updateEnable(study, true);
        attributes.addFlashAttribute("message", "????????? ?????????????????????.");
        return "redirect:/study/" + study.getEncodePath() + "/settings/banner";
    }

    @PostMapping("/study/{path}/settings/banner/disable")
    public String settingDisableBanner(@CurrentAccount Account account, @PathVariable String path,
                                       RedirectAttributes attributes) {
        Study study = studyService.getStudyToUpdate(path, account);
        studyService.updateEnable(study, false);
        attributes.addFlashAttribute("message", "????????? ????????????????????????.");
        return "redirect:/study/" + study.getEncodePath() + "/settings/banner";
    }

    @GetMapping("/study/{path}/settings/tags")
    public String tagsForm(@CurrentAccount Account account, @PathVariable String path, Model model) throws JsonProcessingException {
        Study study = studyService.getWithTagsAndManagerByStudy(path, account);

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
        Study study = studyService.getWithTagsAndManagerByStudy(path, account);
        //Tag tag = Tag.builder().title(tagTitle).build();

        Tag tag = tagService.findnCreateTag(tagForm.getTagTitle());

        studyService.addTagToStudy(study, tag);

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
        Study study = studyService.getWithTagsAndManagerByStudy(path, account);
        Tag tag = tagRepository.findByTitle(tagForm.getTagTitle());
        if(tag == null) {
            return ResponseEntity.badRequest().build();
        }
        studyService.removeTagToStudy(study, tag);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/study/{path}/settings/zones")
    public String zoneForm(@CurrentAccount Account account, @PathVariable String path, Model model) throws JsonProcessingException {
        Study study = studyService.getWithZonesAndManagerByStudy(path, account);
        List<Zone> whitelist = zoneRepository.findAll();
        Set<Zone> zones = study.getZones();

        model.addAttribute("whitelist", objectMapper.writeValueAsString(whitelist));
        System.out.println("zones = " + zones);
        System.out.println("whitelist = " + whitelist);

        model.addAttribute("zones", zones.stream().map(Zone::toString).collect(Collectors.toList()));
        model.addAttribute(account);
        model.addAttribute(study);

        return "study/settings/zones";
    }

    @ResponseBody
    @PostMapping("/study/{path}/settings/zones/add")
    public ResponseEntity addZoneToStudy(@CurrentAccount Account account, @PathVariable String path, @RequestBody ZoneForm zoneForm) {
        Study study = studyService.getWithZonesAndManagerByStudy(path, account);
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        studyService.addZoneToStudy(study, zone);

        return ResponseEntity.ok().build();
    }

    @ResponseBody
    @PostMapping("/study/{path}/settings/zones/remove")
    public ResponseEntity removeZoneToStudy(@CurrentAccount Account account, @PathVariable String path, @RequestBody ZoneForm zoneForm) {
        Study study = studyService.getWithZonesAndManagerByStudy(path, account);
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        studyService.removeZoneToStudy(study, zone);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/study/{path}/settings/study")
    public String studySetting(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Study study = studyService.getWithManagerByStudy(path, account);
        model.addAttribute(study);
        model.addAttribute(account);

        return "study/settings/study";
    }

    @PostMapping("/study/{path}/settings/study/publish")
    public String studyPublish(@CurrentAccount Account account, @PathVariable String path, RedirectAttributes attributes, Model model) {
        Study study = studyService.getWithManagerByStudy(path, account);
        studyService.publishStudy(study);
        attributes.addFlashAttribute("message", "???????????? ??????????????? ??????????????????.");
        return "redirect:/study/" + study.getEncodePath() + "/settings/study";
    }

    @PostMapping("/study/{path}/settings/study/close")
    public String studyClose(@CurrentAccount Account account, @PathVariable String path, RedirectAttributes attributes) {
        Study study = studyService.getWithManagerByStudy(path, account);
        studyService.closeStudy(study);
        attributes.addFlashAttribute("message", "???????????? ???????????????.");
        return "redirect:/study/" + study.getEncodePath() + "/settings/study";
    }

    @PostMapping("/study/{path}/settings/recruit/start")
    public String studyRecruitStart(@CurrentAccount Account account, @PathVariable String path, RedirectAttributes attributes) {
        Study study = studyService.getWithManagerByStudy(path, account);
        if(!study.canUpdateRecruit()) {
            attributes.addFlashAttribute("message", "1????????????????????????????????????????");
            return "redirect:/study/" + study.getEncodePath() + "/settings/study";
        }
        studyService.startRecruitStudy(study);
        attributes.addFlashAttribute("message", "???????????????????????????????????????????????????");
        return "redirect:/study/" + study.getEncodePath() + "/settings/study";
    }

    @PostMapping("/study/{path}/settings/recruit/stop")
    public String studyRecruitStop(@CurrentAccount Account account, @PathVariable String path, RedirectAttributes attributes) {
        Study study = studyService.getWithManagerByStudy(path, account);
        if(!study.canUpdateRecruit()) {
            attributes.addFlashAttribute("message", "1????????????????????????????????????????");
            return "redirect:/study/" + study.getEncodePath() + "/settings/study";
        }
        studyService.stopRecruitStudy(study);
        attributes.addFlashAttribute("message", "??????????????????????????????????????????????????????");
        return "redirect:/study/" + study.getEncodePath() + "/settings/study";
    }

    @PostMapping("/study/{path}/settings/study/path")
    public String studyPath(@CurrentAccount Account account, @PathVariable String path, RedirectAttributes attributes,
                            @RequestParam String newPath) {
        Study study = studyService.getWithManagerByStudy(path, account);
        if(!studyService.isValidatePath(newPath)) {
            attributes.addFlashAttribute("studyPathError", "PATH??????????????????????");
            return "redirect:/study/" + study.getEncodePath() + "/settings/study";
        }

        Study newStudy = studyService.updatePath(study, newPath);
        attributes.addFlashAttribute("message", "PATH????????????????????????????????????");
        return "redirect:/study/" + newStudy.getEncodePath() + "/settings/study";
    }

    @PostMapping("/study/{path}/settings/study/title")
    public String studyTitle(@CurrentAccount Account account, @PathVariable String path, RedirectAttributes attributes,
                             @RequestParam String newTitle) {
        Study study = studyService.getWithManagerByStudy(path, account);
        if(!studyService.isValidateTitle(newTitle)) {
            attributes.addFlashAttribute("studyTitleError", "??????????????????????????????????");
            return "redirect:/study/" + study.getEncodePath() + "/settings/study";
        }
        studyService.updateTitle(study, newTitle);
        attributes.addFlashAttribute("message", "????????????????????????????????????????????????");
        return "redirect:/study/" + study.getEncodePath() + "/settings/study";
    }

    @PostMapping("/study/{path}/settings/study/remove")
    public String studyRemove(@CurrentAccount Account account, @PathVariable String path, RedirectAttributes attributes) {
        Study study = studyService.getWithManagerByStudy(path, account);

        studyService.removeStudy(study);
        return "redirect:/";
    }

}
