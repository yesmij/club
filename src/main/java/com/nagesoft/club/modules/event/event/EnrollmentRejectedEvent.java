package com.nagesoft.club.modules.event.event;

import com.nagesoft.club.modules.event.Enrollment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


public class EnrollmentRejectedEvent extends EnrollmentEvent {


    public EnrollmentRejectedEvent(Enrollment enrollment) {
        super(enrollment, "모임 참가 신청을 거절했습니다.");
    }
}
