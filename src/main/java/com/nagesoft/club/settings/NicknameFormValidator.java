package com.nagesoft.club.settings;

import com.nagesoft.club.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.validation.Valid;
import javax.validation.Validation;

@RequiredArgsConstructor
@Component
public class NicknameFormValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return NicknameForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        NicknameForm nicknameForm = (NicknameForm) target;
        if(null != accountRepository.findByNickname(nicknameForm.getNickname())) {
            errors.rejectValue("nickname", "wrong.value", "이미 사용중인 니쿠네무데스카라");
        }
    }
}
