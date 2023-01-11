package com.nagesoft.club.modules.event.event;

import com.nagesoft.club.infra.config.AppProperties;
import com.nagesoft.club.infra.mail.EmailMessage;
import com.nagesoft.club.infra.mail.EmailService;
import com.nagesoft.club.modules.account.Account;
import com.nagesoft.club.modules.event.Event;
import com.nagesoft.club.modules.notification.Notification;
import com.nagesoft.club.modules.notification.NotificationRepository;
import com.nagesoft.club.modules.notification.NotificationType;
import com.nagesoft.club.modules.study.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;

@Async
@Transactional
@RequiredArgsConstructor
@Component
public class EnrollmentEventListener {

    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;
    private final EmailService emailService;
    private final NotificationRepository notificationRepository;

    @EventListener
    public void handleEnrollmentEvent(EnrollmentEvent enrollmentEvent) {
        // 대상자 추출 -> 스터티 괌심
        Account account = enrollmentEvent.getEnrollment().getAccount();
        Event event = enrollmentEvent.getEnrollment().getEvent();
        Study study = event.getStudy();

        String mailTitle = event.getTitle() + " 모임 참가 신청 결과입니다";

        // 메일 발송 or 웹 저장 + 이벤트 메시지 (생성, 변경, 삭제)
        if(account.isStudyEnrollmentResultByEmail()) {
            sendEnrollmentNotification(study, event, account, enrollmentEvent.getMessage(), mailTitle);
        }

        if(account.isStudyEnrollmentResultByWeb()) {
            saveEnrollmentNotification(study, event, account, enrollmentEvent.getMessage(), NotificationType.EVENT_ENROLLMENT);
        }

    }

    private void sendEnrollmentNotification(Study study, Event event, Account account, String eventMessage, String mailTitle) {

        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("link", "/study/" + study.getEncodePath() + "/events/" + event.getId());
        context.setVariable("linkName", study.getTitle());
        context.setVariable("message", eventMessage);
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .subject(mailTitle)
                .to(account.getEmail())
                .message(message)
                .build();
        emailService.sendEmail(emailMessage);
    }

    private void saveEnrollmentNotification(Study study, Event event, Account account, String eventMessage, NotificationType notificationType) {
        Notification notification = new Notification();
        notification.setAccount(account);
        notification.setChecked(false);
        notification.setNotificationType(notificationType);
        notification.setLink("/study/" + study.getEncodePath() + "/events/" + event.getId());
        notification.setMessage(eventMessage);
        notification.setTitle(study.getTitle() + " / " + event.getTitle());
        notification.setCreatedDateTime(LocalDateTime.now());
        notificationRepository.save(notification);
    }

}
