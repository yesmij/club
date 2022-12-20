package com.nagesoft.club.event;

import com.nagesoft.club.account.CurrentAccount;
import com.nagesoft.club.domain.Account;
import com.nagesoft.club.domain.Event;
import com.nagesoft.club.domain.Study;
import com.nagesoft.club.study.StudyService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@RequestMapping("/study/{path}")
@RequiredArgsConstructor
@Controller
public class EventController {

    private final StudyService studyService;
    private final ModelMapper modelMapper;
    private final EventFormValidator eventFormValidator;

    @InitBinder("eventForm")
    public void init(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(eventFormValidator);
    }

    @GetMapping("/new-event")
    public String eventForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Study study = studyService.getWithMemberByStudy(path, account);
        model.addAttribute(study);
        model.addAttribute(account);
        model.addAttribute(new EventForm());
        return "event/event-form";
    }

    @PostMapping("/new-event")
    public String eventSave(@CurrentAccount Account account, @PathVariable String path, @Valid EventForm eventForm,
                            Errors errors, Model model, RedirectAttributes attributes) {
        Study study = studyService.getWithMemberByStudy(path, account);
        if(errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(study);
            return "event/event-form";
        }

        studyService.createEvent(modelMapper.map(eventForm, Event.class), account, study);
        attributes.addFlashAttribute("message", "이벤트 등록을 완료했습니다.");

        return "redirect:/study/" + study.getEncodePath() + "/new-event";
    }
}
