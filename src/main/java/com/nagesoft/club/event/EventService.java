package com.nagesoft.club.event;

import com.nagesoft.club.domain.Account;
import com.nagesoft.club.domain.Event;
import com.nagesoft.club.domain.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

//    public List<Event> currentEvents() {
//        List<Event> events = eventRepository.findAll()
//                .stream().map(event -> {event.getEndDateTime().isBefore(LocalDateTime.now())} )
//                .collect(Collectors.toList());
//
//        // 모든 이벤트에서 현재 진행중인 이벤트 : 현재시간 < 끝시간
//        return null;
//    }
}
