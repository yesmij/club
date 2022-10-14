package com.nagesoft.club.account;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;

@Data
public class SignUpForm {

    @NotBlank
    @Length(min = 3, max = 10)
    @Pattern(regexp = "^[a-zㄱ-힣0-9-_]{3,20}$")
    private String nickname;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Length(min = 3, max = 50)
    private String password;

}
