package com.nagesoft.club.event;

import com.nagesoft.club.domain.Account;
import com.nagesoft.club.domain.Event;
import com.nagesoft.club.domain.Study;
import com.nagesoft.club.event.form.EventForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class EventService {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;


    public Event createEvent(Event event, Account account, Study study) {
        event.setCreateBy(account);
        event.setCreateDateTime(LocalDateTime.now());
        event.setStudy(study);
        return eventRepository.save(event);
    }

    public Optional<Event> getEvent(Long eventId) {
        return eventRepository.findById(eventId);
    }

    public void updateEvent(Event event, EventForm eventForm) {
        modelMapper.map(eventForm, event);
//        modelMapper.map(eventForm, event);
        // TODO 모집 인원을 늘린 선착순 모임의 경우에, 자동으로 추가 인원의 참가 신청을 확정 상태로 변경해야 한다. (나중에 할 일)
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
