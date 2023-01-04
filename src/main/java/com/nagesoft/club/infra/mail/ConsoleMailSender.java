package com.nagesoft.club.infra.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("local")
@Component
public class ConsoleMailSender implements EmailService {
//public class ConsoleMailSender implements JavaMailSender {

    @Override
    public void sendEmail(EmailMessage emailMessage) {
        log.info("sent email: {}", emailMessage.getMessage());
    }

//    @Override
//    public MimeMessage createMimeMessage() {
//        return null;
//    }
//
//    @Override
//    public MimeMessage createMimeMessage(InputStream contentStream) throws MailException {
//        return null;
//    }
//
//    @Override
//    public void send(MimeMessage mimeMessage) throws MailException {
//    }
//
//    @Override
//    public void send(MimeMessage... mimeMessages) throws MailException {
//    }
//
//    @Override
//    public void send(MimeMessagePreparator mimeMessagePreparator) throws MailException {
//    }
//
//    @Override
//    public void send(MimeMessagePreparator... mimeMessagePreparators) throws MailException {
//    }
//
//    @Override
//    public void send(SimpleMailMessage simpleMessage) throws MailException {
//        log.info(simpleMessage.getText());
//    }
//
//    @Override
//    public void send(SimpleMailMessage... simpleMessages) throws MailException {
//    }
}
