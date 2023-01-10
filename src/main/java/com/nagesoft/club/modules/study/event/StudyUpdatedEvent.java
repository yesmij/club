package com.nagesoft.club.modules.study.event;

import com.nagesoft.club.modules.study.Study;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class StudyUpdatedEvent {
    private final Study study;
    private final String message;

    public StudyUpdatedEvent(Study study, String message) {
        this.study = study;
        this.message = message;
    }
}
