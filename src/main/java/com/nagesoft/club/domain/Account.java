package com.nagesoft.club.domain;

import lombok.*;
import org.hibernate.annotations.Fetch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
@Entity @EqualsAndHashCode(of = "id")
@AllArgsConstructor @NoArgsConstructor
public class Account {

//    @Autowired PasswordEncoder passwordEncoder;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String emailCheckToken;
    private boolean emailChecked;

    @Lob
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

    public void createEmailToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
    }

//    public void passwordEncode(String rawPassword) {
//        this.password = passwordEncoder.encode(rawPassword);
//    }
}
