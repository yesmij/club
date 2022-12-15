package com.nagesoft.club.study;

import com.nagesoft.club.domain.Study;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


@Transactional(readOnly=true)
public interface StudyRepository extends JpaRepository<Study, Long> {
    Boolean existsByPath(String path);

    @EntityGraph(value = "study.withAll", type = EntityGraph.EntityGraphType.LOAD)
    Study findByPath(String path);

    @EntityGraph(value = "study.withManager", type = EntityGraph.EntityGraphType.FETCH)
    Study findStudyWithManagerByPath(String path);

    @EntityGraph(value = "Study.withTagsAndManagers", type = EntityGraph.EntityGraphType.FETCH)
    Study findStudyWithTagsAndManagersByPath(String path);

    @EntityGraph(value = "Study.withZonesAndManagers", type = EntityGraph.EntityGraphType.FETCH)
    Study findStudyWithZonesAndManagersByPath(String path);

//    Set<Member> findMembersById(Long id);
}
