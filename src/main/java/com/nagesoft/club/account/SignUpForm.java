package com.nagesoft.club.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;

@Builder
@NoArgsConstructor @AllArgsConstructor
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
