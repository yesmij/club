package com.nagesoft.club.study;

import com.nagesoft.club.domain.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface StudyRepository extends JpaRepository<Study, Long> {
    Boolean existsByPath(String path);

    Study findByPath(String path);
}
