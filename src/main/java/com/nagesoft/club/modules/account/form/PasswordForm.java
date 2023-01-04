package com.nagesoft.club.modules.account.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class PasswordForm {
    @Length(min = 4, max = 50)
    private String newPassword;
    private String newPasswordConfirm;
}
