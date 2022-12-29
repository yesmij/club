package com.nagesoft.club.event;

import com.nagesoft.club.domain.*;
import com.nagesoft.club.event.form.EventForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class EventService {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EnrollmentRepository enrollmentRepository;


    public Event createEvent(Event event, Account account, Study study) {
        event.setCreatedBy(account);
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

    public void deleteEvent(Event event) {
        eventRepository.delete(event);
    }

    public void enrollEvent(Event event, Account account) {

        // 가입이력 있는지 확인
        if(!enrollmentRepository.existsByAccountAndEvent(account, event)) {

            Enrollment enrollment = new Enrollment();

            enrollment.setAccepted(event.isAbleToAcceptWaitingEnrollment());
            enrollment.setAccount(account);
            enrollment.setEvent(event);
            enrollment.setEnrolledAt(LocalDateTime.now());
            enrollment.setEvent(event);
            event.addEnrollment(enrollment);
            enrollmentRepository.save(enrollment);
//        event.getEnrollments().add(enrollment);
        }
    }

}
