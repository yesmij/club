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
        accounts.forEach(account -> {
            if(account.isStudyCreatedByEmail()) {
                sendStudyCreatedEmail(study, account);
            }

            if(account.isStudyCreatedByWeb()) {
                saveStudyCreatedEmail(study, account);
            }
        });
    }

    private void saveStudyCreatedEmail(Study study, Account account) {

        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("link", "/study/" + study.getEncodePath());
        context.setVariable("linkName", study.getTitle());
        context.setVariable("message", "새로운 스터디가 생겼습니다");
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .subject(study.getTitle() + "스터기 개설 알림")
                .to(account.getEmail())
                .message(message)
                .build();
        emailService.sendEmail(emailMessage);
    }

    private void sendStudyCreatedEmail(Study study, Account account) {
        Notification notification = new Notification();
        notification.setAccount(account);
        notification.setChecked(false);
        notification.setNotificationType(NotificationType.STUDY_CREATED);
        notification.setLink("/study/" + study.getEncodePath());
        notification.setMessage(study.getShortDescription());
        notification.setTitle(study.getTitle());
        notification.setCreatedDateTime(LocalDateTime.now());
        notificationRepository.save(notification);
    }

}
