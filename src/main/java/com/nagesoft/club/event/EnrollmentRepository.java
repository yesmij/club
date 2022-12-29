package com.nagesoft.club.event;

import com.nagesoft.club.domain.Account;
import com.nagesoft.club.domain.Enrollment;
import com.nagesoft.club.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByAccountAndEvent(Account account, Event event);
}
