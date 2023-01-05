package com.nagesoft.club.modules.notification;

import com.nagesoft.club.modules.account.Account;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter @Setter
@EqualsAndHashCode(of = "id")
@Entity
public class Notification {

    @Id @GeneratedValue
    private Long id;

    private String title;

    private String link;

    private String message;

    private boolean checked;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    private LocalDateTime createdLocalDateTime;

    @Enumerated(value = EnumType.STRING)
    private NotificationType notificationType;
}

