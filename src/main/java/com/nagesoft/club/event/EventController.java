package com.nagesoft.club.event;

import com.nagesoft.club.account.CurrentAccount;
import com.nagesoft.club.domain.Account;
import com.nagesoft.club.domain.Study;
import com.nagesoft.club.study.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/study/{path}/event")
@RequiredArgsConstructor
@Controller
public class EventController {

    private final StudyService studyService;

    @GetMapping("/eventForm")
    public String eventForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Study study = studyService.getWithMemberByStudy(path, account);
        model.addAttribute(study);
        model.addAttribute(account);
        model.addAttribute(new EventForm());

        return "event/event-form";
    }
}
