package com.nagesoft.club.modules.event.event;

import com.nagesoft.club.modules.event.Enrollment;
import com.nagesoft.club.modules.event.Event;
import com.nagesoft.club.modules.study.Study;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class EnrollmentEvent {
    private final Enrollment enrollment;
    private final String message;
}
