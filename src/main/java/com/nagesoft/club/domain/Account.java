package com.nagesoft.club.domain;

import lombok.*;
import org.hibernate.annotations.Fetch;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter @Setter
@Entity @EqualsAndHashCode(of = "id")
@AllArgsConstructor @NoArgsConstructor
public class Account {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String emailCheckToken;
    private boolean emailChecked;

    private String password;
    @Column(unique = true)
    private String nickname;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String profileImg;

    private String bio;
    private String url;
    private String occupation;
    private String location;

    private LocalDateTime joinedAt;

    private boolean studyCreatedByEmail;
    private boolean studyCreatedByWeb;
    private boolean studyEnrollmentResultByEmail;
    private boolean studyEnrollmentResultByWeb;
    private boolean studyUpdatedByEmai;
    private boolean studyUpdatedByWeb;
}
