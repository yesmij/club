package com.nagesoft.club.modules.study.event;

import com.nagesoft.club.modules.study.Study;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class StudyCreatedEvent {
    private Study study;

    public StudyCreatedEvent(Study study) {
        this.study = study;
    }
}
