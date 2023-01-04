package com.nagesoft.club.infra.mail;

public interface EmailService {

    void sendEmail(EmailMessage emailMessage);
}