package com.nagesoft.club.study;

import com.nagesoft.club.domain.Study;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NamedEntityGraph;
import java.lang.reflect.Member;
import java.util.Set;


@Transactional(readOnly=true)
public interface StudyRepository extends JpaRepository<Study, Long> {
    Boolean existsByPath(String path);

    @EntityGraph(value = "study.withAll", type = EntityGraph.EntityGraphType.LOAD)
    Study findByPath(String path);


//    Set<Member> findMembersById(Long id);
}
