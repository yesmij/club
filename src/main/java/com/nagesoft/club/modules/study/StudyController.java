package com.nagesoft.club.modules.study;

import com.nagesoft.club.modules.account.Account;
import com.nagesoft.club.modules.account.CurrentAccount;
import com.nagesoft.club.modules.event.Event;
import com.nagesoft.club.modules.event.EventRepository;
import com.nagesoft.club.modules.study.validator.StudyFormValidator;
import com.nagesoft.club.modules.study.form.StudyForm;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class StudyController {

    private final StudyService studyService;
    private final ModelMapper modelMapper;
    private final StudyRepository studyRepository;
    private final StudyFormValidator studyFormValidator;
    private final EventRepository eventRepository;

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

    @GetMapping("/study/{path}/join")
    public String joinStudy(@CurrentAccount Account account, @PathVariable String path) {
        Study study = studyRepository.findByPath(path);
        studyService.joinStudy(study, account);
        return "redirect:/study/" + study.getEncodePath() + "/members";
    }

    @GetMapping("/study/{path}/leave")
    public String leaveStudy(@CurrentAccount Account account, @PathVariable String path) {
        Study study = studyRepository.findByPath(path);
        studyService.leaveStudy(study, account);
        return "redirect:/study/" + study.getEncodePath() + "/members";
    }

    @GetMapping("/study/{path}/events")
    public String eventsOfStudy(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Study study = studyRepository.findByPath(path);
        List<Event> events = eventRepository.findByStudyOrderByStartDateTime(study);

        List<Event> oldEvents = new ArrayList<>();
        List<Event> newEvents = new ArrayList<>();
        events.forEach( a -> {
            if (a.getEndDateTime().isBefore(LocalDateTime.now())) {
                oldEvents.add(a);
            } else {
                newEvents.add(a);
            }
        });

        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute(events);
        model.addAttribute("newEvents", newEvents);
        model.addAttribute("oldEvents", oldEvents);

        return "study/events";
    }

}
