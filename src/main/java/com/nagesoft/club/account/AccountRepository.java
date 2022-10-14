package com.nagesoft.club.account;

import com.nagesoft.club.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByNickname(String nickname);

    boolean existsByEmail(String email);
}
