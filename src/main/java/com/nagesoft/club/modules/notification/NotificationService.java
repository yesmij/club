package com.nagesoft.club.modules.notification;

import com.nagesoft.club.modules.account.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void toChecked(List<Notification> notifications) {
        notifications.forEach(notification -> notification.setChecked(true));
        notificationRepository.saveAll(notifications);
    }

    public void removeNotification(Account account) {
        notificationRepository.deleteByAccountAndChecked(account, true);
    }
}
