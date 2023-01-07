package com.nagesoft.club.modules.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.QuerydslJpaPredicateExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface AccountRepository extends JpaRepository<Account, Long>, QuerydslPredicateExecutor<Account> {
    boolean existsByNickname(String nickname);

    boolean existsByEmail(String email);

    Account findByEmail(String email);

    Account findByNickname(String username);
}
