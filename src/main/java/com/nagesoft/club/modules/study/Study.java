package com.nagesoft.club.modules.study;

import com.nagesoft.club.modules.account.Account;
import com.nagesoft.club.modules.account.CurrentAccount;
import com.nagesoft.club.modules.account.UserAccount;
import com.nagesoft.club.modules.tag.Tag;
import com.nagesoft.club.modules.zone.Zone;
import lombok.*;

import javax.persistence.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@NamedEntityGraph(name = "study.withMember", attributeNodes = {
        @NamedAttributeNode("members")
})
@NamedEntityGraph(name = "Study.withTagsAndManagers", attributeNodes = {
        @NamedAttributeNode("tags"),
        @NamedAttributeNode("managers")})
@NamedEntityGraph(name = "Study.withZonesAndManagers", attributeNodes = {
        @NamedAttributeNode("zones"),
        @NamedAttributeNode("managers")})
@NamedEntityGraph(name = "study.withManager", attributeNodes = {
        @NamedAttributeNode("managers")
})
@NamedEntityGraph(name = "study.withAll", attributeNodes = {
            @NamedAttributeNode("managers"),
            @NamedAttributeNode("members"),
            @NamedAttributeNode("zones"),
            @NamedAttributeNode("tags")} )
@NamedEntityGraph(name = "Study.withZonesAndTags", attributeNodes = {
        @NamedAttributeNode("zones"),
        @NamedAttributeNode("tags")} )
@Getter @Setter @EqualsAndHashCode(of = "id")
@Entity
@Builder @NoArgsConstructor @AllArgsConstructor
public class Study {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Account> managers = new HashSet<>();

    @ManyToMany
    private Set<Account> members = new HashSet<>();

    @Column(unique = true)
    private String path;

    private String title;

    private String shortDescription;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String fullDescription;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    private Set<Zone> zones = new HashSet<>();

    private LocalDateTime publishedDateTime;

    private LocalDateTime closedDateTime;

    private LocalDateTime recruitingUpdatedDateTime;

    private boolean recruiting;

    private boolean published;

    private boolean closed;

    private boolean useBanner;

    public boolean isJoinable(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        return this.recruiting == true && this.published == true
                && !this.members.contains(account) && !this.managers.contains(account);
    }

    public boolean isMember(UserAccount userAccount) {
        return this.members.contains(userAccount.getAccount());
    }

    public boolean isManager(UserAccount userAccount) {
        return this.managers.contains(userAccount.getAccount());
        //return true;
    }

    public void addManager(Account account) {
        this.managers.add(account);
    }

    public String getEncodePath() {
        return URLEncoder.encode(this.path, StandardCharsets.UTF_8);
    }

    public String getProfileImage() {
        //return this.useBanner ? this.image : "/images/default_banner.png";
        return profileImage != null ? this.profileImage : "/images/default_banner.png";
    }

    public void publish() {
        if(!this.closed && !this.published) {
            this.published = true;
            this.publishedDateTime = LocalDateTime.now();
        } else {
            throw new RuntimeException("???????????? ????????? ??? ?????? ???????????????. ???????????? ?????? ??????????????? ??????????????????.");
        }
    }

    public void close() {
        if(!this.closed && this.published) {
            this.closed = true;
            this.closedDateTime = LocalDateTime.now();
        } else {
            throw new RuntimeException("???????????? ????????? ??? ????????????. ???????????? ???????????? ???????????? ?????? ????????? ??????????????????.");
        }
    }


    public void startRecruit() {
        if(canUpdateRecruit()) {
            this.recruiting = true;
            this.recruitingUpdatedDateTime = LocalDateTime.now();
        } else {
            throw new RuntimeException("?????? ????????? ????????? ??? ????????????. ???????????? ??????????????? ??? ?????? ??? ?????? ???????????????.");
        }
    }

    public boolean canUpdateRecruit() {
        return this.published && this.recruitingUpdatedDateTime == null || this.recruitingUpdatedDateTime.isBefore(LocalDateTime.now().minusHours(1));
    }

    public void stopRecruit() {
        if(canUpdateRecruit()) {
            this.recruiting = false;
            this.recruitingUpdatedDateTime = LocalDateTime.now();
        } else {
            throw new RuntimeException("?????? ????????? ????????? ??? ????????????. ???????????? ??????????????? ??? ?????? ??? ?????? ???????????????.");
        }
    }

    public boolean isRemovable() {
        return !this.published;
    }

    public void addMember(Account account) {
        this.getMembers().add(account);
    }

    public boolean isMemberOf(Account account) {
        return this.members.contains(account);
    }

    public boolean isManagerOf(Account account) {
        return this.managers.contains(account);
    }

}
