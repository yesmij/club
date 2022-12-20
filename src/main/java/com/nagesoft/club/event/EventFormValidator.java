package com.nagesoft.club.event;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class EventFormValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(EventForm.class);
    }

    @Override
    public void validate(Object o, Errors errors) {
        EventForm eventForm = (EventForm) o;
        if(eventForm.getEndDateTime().isBefore(eventForm.getStartDateTime())) {
            errors.rejectValue(String.valueOf(eventForm.getEndDateTime()), "EndDateTime.time.wrong.value", "End Time이 시작 Time보다 빠를수는 없습니다.");
        }

        if(eventForm.getEndEnrollmentDateTime().isAfter(eventForm.getStartDateTime())) {
            errors.rejectValue(String.valueOf(eventForm.getStartDateTime()), "StartDateTime.time.wrong.value", "Start Time이 등록마감보다 빠를수는 없습니다.");
        }
    }


    // NG
    // 모임종료 > 모임시작
    // 등록시작 > 등록마감
    // 등록마감 > 모임시작 > 모임마감

//    private EventType eventType;
//
//    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) // 등록 마감
//    private LocalDateTime enrollmentDateTime;
//
//    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)  // 모임 시작
//    private LocalDateTime startDateTime;
//
//    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) // 모임 종료
//    private LocalDateTime endDateTime;
//
//    @Min(2)
//    private Integer limitOfEnrollments = 2;
}
