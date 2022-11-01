package com.nagesoft.club.settings;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.ManyToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Size;

@Data
public class Profile {
    @Length(max = 10)
    private String bio;
    @Length(max = 30)
    private String url;
    @Length(max = 20)
    private String occupation;
    private String location;

    public Profile(String bio, String url, String occupation, String location) {
        this.bio = bio;
        this.url = url;
        this.occupation = occupation;
        this.location = location;
    }
}
