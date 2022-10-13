package com.nagesoft.club.account;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class SignUpForm {

    @NotBlank
    @Min(3) @Max(10)
    private String nickname;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Min(8)
    private String password;

}
