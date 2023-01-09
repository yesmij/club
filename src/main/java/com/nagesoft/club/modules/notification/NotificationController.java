package com.nagesoft.club.modules.notification;

import com.nagesoft.club.modules.account.Account;
import com.nagesoft.club.modules.account.CurrentAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    @GetMapping("/notifications")
    public String notificationView(@CurrentAccount Account account, Model model) {
        // 날짜별로 가져오기
        List<Notification> notifications = notificationRepository.findByAccountAndCheckedOrderByCreatedDateTimeDesc(account, false);

        // 읽은 것들에 대한 카운트
        long numberOfChecked = notificationRepository.countByAccountAndChecked(account, true);

        notificationService.toChecked(notifications);
        model.addAttribute("isNew", true);

        // 담기 (스터디, 이벤트 등록, 관심스터디) && 체크한것, 체크안한것, 전체 노티목록 담기
        putModelAndView(model, notifications, numberOfChecked, notifications.size());

        return "notification/list";
    }

    private void putModelAndView(Model model, List<Notification> notifications, long numberOfChecked, long numberOfNotChecked) {
        List<Notification> newStudyNotifications = new ArrayList<>();
        List<Notification> eventEnrollmentNotifications = new ArrayList<>();
        List<Notification> watchingStudyNotifications = new ArrayList<>();
        for (Notification notification : notifications) {
            if(notification.getNotificationType().equals(NotificationType.STUDY_CREATED)) {
                newStudyNotifications.add(notification);
            } else if (notification.getNotificationType().equals(NotificationType.EVENT_ENROLLMENT)) {
                eventEnrollmentNotifications.add(notification);
            } else if (notification.getNotificationType().equals(NotificationType.STUDY_UPDATED)) {
                watchingStudyNotifications.add(notification);
            }
        }


        model.addAttribute("numberOfNotChecked", numberOfNotChecked);
        model.addAttribute("numberOfChecked", numberOfChecked);
        model.addAttribute("notifications", notifications);
        model.addAttribute("newStudyNotifications", newStudyNotifications);
        model.addAttribute("eventEnrollmentNotifications", eventEnrollmentNotifications);
        model.addAttribute("watchingStudyNotifications", watchingStudyNotifications);
    }

    @GetMapping("/notifications/old")
    public String notificationNotcheckedView(@CurrentAccount Account account, Model model) {
        // 날짜별로 가져오기
        List<Notification> notifications = notificationRepository.findByAccountAndCheckedOrderByCreatedDateTimeDesc(account, true);

        // 읽은 것들에 대한 카운트
        long numberOfNotChecked = notificationRepository.countByAccountAndChecked(account, false);
        model.addAttribute("isNew", false);

        // 담기 (스터디, 이벤트 등록, 관심스터디) && 체크한것, 체크안한것, 전체 노티목록 담기
        putModelAndView(model, notifications, notifications.size(), numberOfNotChecked);

        return "notification/list";
    }

    // 삭제
    @DeleteMapping("/notifications")
    public String removeNotification(@CurrentAccount Account account) {
        notificationService.removeNotification(account);
        return  "redirect:/notifications";
    }
}
