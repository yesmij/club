package com.nagesoft.club.modules.account.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

//@NoArgsConstructor
@Data
public class Profile {
    @Length(max = 10)
    private String bio;
    @Length(max = 30)
    private String url;
    @Length(max = 20)
    private String occupation;
    private String location;
    private String profileImage;

//    public Profile(Account account) {
//        this.bio = account.getBio();
//        this.url = account.getUrl();
//        this.occupation = account.getOccupation();
//        this.location = account.getLocation();
//        this.profileImage = account.getProfileImage();
//    }
}
