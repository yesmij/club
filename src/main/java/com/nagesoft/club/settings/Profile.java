package com.nagesoft.club.settings;

import lombok.Data;

@Data
public class Profile {
    private String bio;
    private String url;
    private String occupation;
    private String location;

    public Profile(String bio, String url, String occupation, String location) {
        this.bio = bio;
        this.url = url;
        this.occupation = occupation;
        this.location = location;
    }
}
