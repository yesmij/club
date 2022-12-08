package com.nagesoft.club.domain;

import com.nagesoft.club.account.UserAccount;
import lombok.*;

import javax.persistence.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@NamedEntityGraph(name = "study.withAll", attributeNodes = {
            @NamedAttributeNode("managers"),
            @NamedAttributeNode("members"),
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
    private String image;

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
}
