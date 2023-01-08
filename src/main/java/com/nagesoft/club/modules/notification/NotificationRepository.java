package com.nagesoft.club.modules.notification;

import com.nagesoft.club.modules.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Long countByAccountAndChecked(Account account, boolean checked);
}
