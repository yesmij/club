package com.nagesoft.club.study;

import com.nagesoft.club.account.CurrentAccount;
import com.nagesoft.club.config.AppProperties;
import com.nagesoft.club.domain.Account;
import com.nagesoft.club.domain.Study;
import com.nagesoft.club.study.form.StudyForm;
import com.nagesoft.club.study.validator.StudyFormValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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
    public String newStudySave(@CurrentAccount Account account, @Valid StudyForm studyForm, Errors errors) {
        if(errors.hasErrors()) {
            return "study/new-study";
        }

        Study study = studyService.cretateStudy(modelMapper.map(studyForm, Study.class), account);
        return "redirect:/study/" + URLEncoder.encode(study.getPath(), StandardCharsets.UTF_8);
    }

    @GetMapping("/study/{path}")
    public String studyView(@CurrentAccount Account account, @PathVariable String path, Model model) {
        model.addAttribute("study", studyRepository.findByPath(path));
        return "study/view";
    }


}
