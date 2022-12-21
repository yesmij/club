package com.nagesoft.club.event;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDateTime;

@Component
public class EventValdator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(EventForm.class);
//        return EventForm.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        EventForm eventForm = (EventForm) o;

        if(isValidEnrollmentDateTime(eventForm)) {
            errors.rejectValue("endEnrollmentDateTime", "wrong.dateTime", "등록마감을 제대로 입력!!");
        }

        if(isValidEndDateTime(eventForm)) {
            errors.rejectValue("endDateTime", "wrong.datetime", "End Time이 시작 Time보다 빠를수는 없습니다.");
        }

        if(isValidStartDateTime(eventForm)) {
            errors.rejectValue("endEnrollmentDateTime", "StartDateTime.time.wrong.value", "Start Time이 등록마감보다 빠를수는 없습니다.");
        }
    }
    // NG
    // 현재시간 > 등록마감
    // 모임종료 > 모임시작
    // 등록시작 > 등록마감
    // --> 현재시간 > 등록마감 > 모임시작 > 모임마감



    private boolean isValidStartDateTime(EventForm eventForm) {
        return eventForm.getEndEnrollmentDateTime().isAfter(eventForm.getStartDateTime());
    }

    private boolean isValidEnrollmentDateTime(EventForm eventForm) {
        return eventForm.getEndEnrollmentDateTime().isBefore(LocalDateTime.now());
    }

    private boolean isValidEndDateTime(EventForm eventForm) {
        return eventForm.getEndDateTime().isBefore(eventForm.getStartDateTime());
    }
}
