package com.nagesoft.club.modules.event;

import com.nagesoft.club.modules.study.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStudy(Study study);

    List<Event> findByStudyOrderByStartDateTime(Study study);
}
