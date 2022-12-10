package com.nagesoft.club.study;

import com.nagesoft.club.account.CurrentAccount;
import com.nagesoft.club.domain.Account;
import com.nagesoft.club.domain.Study;
import com.nagesoft.club.study.form.StudyDescriptionForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Controller
public class StudySettingsController {

    private final StudyService studyService;
    private final StudyRepository studyRepository;
    private final ModelMapper modelMapper;

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
}
