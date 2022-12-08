package com.nagesoft.club.study;

import com.nagesoft.club.account.CurrentAccount;
import com.nagesoft.club.domain.Account;
import com.nagesoft.club.domain.Study;
import com.nagesoft.club.study.form.StudyDescriptionForm;
import com.nagesoft.club.study.form.StudyForm;
import com.nagesoft.club.study.validator.StudyFormValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Controller
public class StudyController {

    private final StudyService studyService;
    private final ModelMapper modelMapper;
    private final StudyRepository studyRepository;
    private final StudyFormValidator studyFormValidator;

    @InitBinder("studyForm")
    public void init(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(studyFormValidator);
    }

    @GetMapping("/new-study")
    public String newStudy(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new StudyForm());
        return "study/new-study";
    }

    @PostMapping("/new-study")
    public String newStudySave(@CurrentAccount Account account, @Valid StudyForm studyForm, Errors errors, Model model) {
        if(errors.hasErrors()) {
            model.addAttribute(account);
            return "study/new-study";
        }

        Study study = studyService.createStudy(modelMapper.map(studyForm, Study.class), account);
        return "redirect:/study/" + URLEncoder.encode(study.getPath(), StandardCharsets.UTF_8);
    }

    @GetMapping("/study/{path}")
    public String studyView(@CurrentAccount Account account, @PathVariable String path, Model model) {
        model.addAttribute("study", studyRepository.findByPath(path));
        model.addAttribute(account);
        return "study/view";
    }

    @GetMapping("/study/{path}/members")
    public String members(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Study study = studyRepository.findByPath(path);
        model.addAttribute("study", study);
        model.addAttribute("members", study.getMembers());
        model.addAttribute(account);
//        System.out.println("memberSet = " + memberSet.stream().map(Member::getName).collect(Collectors.toList()) );
        return "study/members";
    }

    @GetMapping("/study/{path}/settings/description")
    public String settingForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Study study = studyRepository.findByPath(path);
        if(!study.getManagers().contains(account)) {
            model.addAttribute("message", "매니저만 접근 가능합니다.");
            model.addAttribute(account);
            model.addAttribute(study);
            //return "study/settings/description";
            throw new AccessDeniedException("사용 권한이 없습니다."); // todo 서비스에 체크 기능 두고, 권한여부는 Account에 둔다!!
        }

        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute(new StudyDescriptionForm().builder().shortDescription(study.getShortDescription())
                .fullDescription(study.getFullDescription()).build());
        //model.addAttribute(modelMapper.map(study, StudyDescriptionForm.class));
        return "study/settings/description";
    }

    @PostMapping("/study/{path}/settings/description")
    public String settingDescription(@CurrentAccount Account account, @PathVariable String path, @Valid StudyDescriptionForm studyDescriptionForm,
                                     Errors errors, Model model, RedirectAttributes attributes) {
        if(errors.hasErrors()) {
            model.addAttribute(account);
            return "study/settings/description";
        }
        //Study study = studyService.updateDescription(modelMapper.map(studyDescriptionForm, Study.class));
        studyService.updateDescription(path, studyDescriptionForm);
        attributes.addFlashAttribute("message", "수정했습니다.");
        //System.out.println("Controller : study.Desc = " + study.getFullDescription());
        model.addAttribute(account);
        return "redirect:/study/" + URLEncoder.encode(path, StandardCharsets.UTF_8) + "/settings/description";
    }

}
