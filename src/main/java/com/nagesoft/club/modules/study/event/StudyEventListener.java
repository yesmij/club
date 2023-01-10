package com.nagesoft.club.modules.study.event;

import com.nagesoft.club.infra.config.AppProperties;
import com.nagesoft.club.infra.mail.EmailMessage;
import com.nagesoft.club.infra.mail.HtmlEmailService;
import com.nagesoft.club.modules.account.Account;
import com.nagesoft.club.modules.account.AccountPredicates;
import com.nagesoft.club.modules.account.AccountRepository;
import com.nagesoft.club.modules.notification.Notification;
import com.nagesoft.club.modules.notification.NotificationRepository;
import com.nagesoft.club.modules.notification.NotificationType;
import com.nagesoft.club.modules.study.Study;
import com.nagesoft.club.modules.study.StudyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Async
@Component
public class StudyEventListener {

    private final StudyRepository studyRepository;
    private final AccountRepository accountRepository;
    private final AppProperties appProperties;
    private final HtmlEmailService emailService;
    private final TemplateEngine templateEngine;
    private final NotificationRepository notificationRepository;

    @EventListener
    public void handleStudyCreatedEvent(StudyCreatedEvent studyCreatedEvent) {
        // 스터디에 있는 Zone과 Tag 정보들을 가지고 와서,
        // 해당 사용자의 Zone과 Tag를 가지고 있는 사용자 정보를 가져온다.
        // 가지고 온 사용자중에서 이메일 발송인지, 웹 알림인지에 따라서 알림 처리!!
        Study study = studyRepository.findStudyWithZonesAndTagsById(studyCreatedEvent.getStudy().getId());
        Iterable<Account> accounts = accountRepository.findAll(AccountPredicates.findByTagsAndZones(study.getTags(), study.getZones()));
        String studyMessage = "스터티 개설 안내";

        accounts.forEach(account -> {
            if(account.isStudyCreatedByEmail()) {
                String mailTitle = study.getTitle() + " 개설되었습니다.";
                sendStudyNotification(study, account, studyMessage, mailTitle);
            }

            if(account.isStudyCreatedByWeb()) {
                saveStudyNotification(study, account, studyMessage, NotificationType.STUDY_CREATED);
            }
        });
    }

    @EventListener
    public void handleStudyUpdatedEvent(StudyUpdatedEvent studyUpdatedEvent) {
        // 스터디에 가입되어 있는 매니저와 멤버들 조회
        Study study = studyRepository.findStudyWithManagersAndMemebersById(studyUpdatedEvent.getStudy().getId());
        Set<Account> accounts = new HashSet<>();
        accounts.addAll(study.getManagers());
        accounts.addAll(study.getMembers());

        String studyMessage = studyUpdatedEvent.getMessage();
        accounts.forEach(account -> {
            if(account.isStudyUpdatedByEmail()) {
                String mailTitle = study.getTitle() + " 소식 변경";
                sendStudyNotification(study, account, studyMessage, mailTitle);
            }

            if(account.isStudyUpdatedByWeb()) {
                saveStudyNotification(study, account, studyMessage, NotificationType.STUDY_UPDATED);
            }
        });
    }


    private void sendStudyNotification(Study study, Account account, String studyMessage, String mailTitle) {

        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("link", "/study/" + study.getEncodePath());
        context.setVariable("linkName", study.getTitle());
        context.setVariable("message", studyMessage);
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .subject(mailTitle)
                .to(account.getEmail())
                .message(message)
                .build();
        emailService.sendEmail(emailMessage);
    }

    private void saveStudyNotification(Study study, Account account, String studyMessage, NotificationType notificationType) {
        Notification notification = new Notification();
        notification.setAccount(account);
        notification.setChecked(false);
        notification.setNotificationType(notificationType);
        notification.setLink("/study/" + study.getEncodePath());
        notification.setMessage(studyMessage);
        notification.setTitle(study.getTitle());
        notification.setCreatedDateTime(LocalDateTime.now());
        notificationRepository.save(notification);
    }

}
