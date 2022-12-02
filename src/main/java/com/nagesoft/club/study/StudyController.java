package com.nagesoft.club.study;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nagesoft.club.account.CurrentAccount;
import com.nagesoft.club.config.AppProperties;
import com.nagesoft.club.domain.Account;
import com.nagesoft.club.domain.Study;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
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
    private final AppProperties appProperties;
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

    @PostMapping("new-study")
    public String newStudySave(@CurrentAccount Account account, @Valid StudyForm studyForm, Errors errors) {
        if(errors.hasErrors()) {
            return "study/new-study";
        }

        Study study = studyService.cretateStudy(modelMapper.map(studyForm, Study.class), account);
        return "redirect:/" + URLEncoder.encode(study.getPath(), StandardCharsets.UTF_8);
    }


}
