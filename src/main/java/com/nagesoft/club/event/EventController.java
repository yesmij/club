package com.nagesoft.club.event;

import com.nagesoft.club.account.CurrentAccount;
import com.nagesoft.club.domain.Account;
import com.nagesoft.club.domain.Event;
import com.nagesoft.club.domain.Study;
import com.nagesoft.club.event.form.EventForm;
import com.nagesoft.club.event.validator.EventValdator;
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
import java.util.List;
import java.util.Optional;

@RequestMapping("/study/{path}")
@RequiredArgsConstructor
@Controller
public class EventController {

    private final StudyService studyService;
    private final ModelMapper modelMapper;
    private final EventValdator eventValdator;
    private final EventService eventService;
    private final EventRepository eventRepository;

    @InitBinder("eventForm")
    public void init(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(eventValdator);
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
        Study study = studyService.getWithManagerByStudy(path, account);
        if(errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(study);
            return "event/event-form";
        }

        Event event = eventService.createEvent(modelMapper.map(eventForm, Event.class), account, study);
        attributes.addFlashAttribute("message", "이벤트 등록을 완료했습니다.");

        return "redirect:/study/" + study.getEncodePath() + "/events/" + event.getId();
    }

    @GetMapping("/events/{eventId}")
    public String eventView(@CurrentAccount Account account, @PathVariable String path, @PathVariable Long eventId, Model model) {
//        Study study = studyService.getWithMemberByStudy(path, account);
        Study study = studyService.getStudy(path);
        Event event = eventRepository.findById(eventId).orElseThrow();
        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute(event);
        return "event/view";
    }


    @GetMapping("/events/{id}/edit")
    public String eventEditFormOfStudy(@CurrentAccount Account account, @PathVariable String path,
                                       @PathVariable Long id, Model model) {
        Study study = studyService.getStudyToUpdate(path, account);
//        Optional<Event> event = eventRepository.findById(id);
        Event event = eventRepository.findById(id).orElseThrow();
        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute(event);
        model.addAttribute(modelMapper.map(event, EventForm.class));

        return "event/update-form";
    }

    @PostMapping("/events/{id}/edit")
    public String eventEditOfStudy(@CurrentAccount Account account, @PathVariable String path,
                                   @PathVariable Long id, @Valid EventForm eventForm, Errors errors, Model model) {
        Study study = studyService.getStudyToUpdate(path, account);
        Event event = eventRepository.findById(id).orElseThrow();
        eventValdator.isLimitOfEnrollments(eventForm, event, errors);
        
        if(errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(study);
            model.addAttribute(event);
            return "event/update-form";
        }
        
        eventForm.setEventType(event.getEventType());
        eventService.updateEvent(event, eventForm);

        return "redirect:/study/" + study.getEncodePath() + "/events/" + event.getId();

    }

    @DeleteMapping("/events/{id}")
    public String deleteEventOfStudy(@CurrentAccount Account account, @PathVariable String path,
                                     @PathVariable Long id) {
        Study study = studyService.getStudyToUpdate(path, account);
        Event event = eventRepository.findById(id).orElseThrow();
        eventService.deleteEvent(event);

        return "redirect:/study/" + study.getEncodePath() + "/events";
    }

    @PostMapping("/events/{id}/enroll")
    public String eventEnroll(@CurrentAccount Account account, @PathVariable String path, @PathVariable Long id,
                              Model model, RedirectAttributes attributes) {
        Study study = studyService.getStudy(path);
        Event event = eventRepository.findById(id).orElseThrow();
        eventService.enrollEvent(event, account);

        return "redirect:/study/" + study.getEncodePath() + "/events/" + event.getId();
    }
}
