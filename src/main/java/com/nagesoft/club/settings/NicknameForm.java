package com.nagesoft.club.settings;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class NicknameForm {
    @NotBlank
    @Pattern(regexp = "^[a-zㄱ-힣0-9-_]{3,20}$")
    private String nickname;
}
