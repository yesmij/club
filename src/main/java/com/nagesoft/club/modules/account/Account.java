package com.nagesoft.club.modules.account;

import com.nagesoft.club.modules.study.Study;
import com.nagesoft.club.modules.tag.Tag;
import com.nagesoft.club.modules.zone.Zone;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Builder
@Getter
@Setter
@Entity
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    //    @Autowired PasswordEncoder passwordEncoder;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String emailCheckToken;
    private boolean emailVerified;
    private LocalDateTime emailSendAt;

    @Lob
    private String password;
    @Column(unique = true)
    private String nickname;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    @ManyToMany
    private Set<Tag> tagSet;

    @ManyToMany
    private Set<Zone> zones = new HashSet<>();

    private String bio;
    private String url;
    private String occupation;
    private String location;

    private LocalDateTime joinedAt;

    private boolean studyCreatedByEmail;
    private boolean studyCreatedByWeb;
    private boolean studyEnrollmentResultByEmail;
    private boolean studyEnrollmentResultByWeb;
    private boolean studyUpdatedByEmail;
    private boolean studyUpdatedByWeb;

    public void createEmailToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
    }

    public void completeSignUp() {
        this.emailVerified = true;
        this.joinedAt = LocalDateTime.now();
    }


    //    public boolean isManagerOf(Study study) {
    //        return study.getManagers().contains(this);
    //    }  // todo 오류1 : manager
    //
    //    public boolean isMemberOf(Study study) {
    //        return this.members
    //        // return study.getMembers().contains(this); //todo 오류2 : member
    //    }

    //    public void passwordEncode(String rawPassword) {
    //        this.password = passwordEncoder.encode(rawPassword);
    //    }
}