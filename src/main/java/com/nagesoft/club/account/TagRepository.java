package com.nagesoft.club.account;

import ch.qos.logback.core.joran.action.IADataForComplexProperty;
import com.nagesoft.club.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Long, Tag> {
}
