package com.nagesoft.club.event;

import com.nagesoft.club.domain.Account;
import com.nagesoft.club.domain.Event;
import com.nagesoft.club.domain.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class EventService {

    private final EventRepository eventRepository;


    public Event createEvent(Event event, Account account, Study study) {
        event.setCreateBy(account);
        event.setCreateDateTime(LocalDateTime.now());
        event.setStudy(study);
        return eventRepository.save(event);
    }

    public Optional<Event> getEvent(Long eventId) {
        return eventRepository.findById(eventId);
    }
}
