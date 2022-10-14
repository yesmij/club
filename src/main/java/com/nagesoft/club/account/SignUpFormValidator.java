package com.nagesoft.club.account;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class SignUpFormValidator implements Validator {

    private final AccountRepository accountRepository;

    public SignUpFormValidator(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(SignUpForm.class);
    }

    @Override
    public void validate(Object o, Errors errors) {
        SignUpForm signUpForm = (SignUpForm) errors;
        if(accountRepository.existsByNickname(signUpForm.getNickname())) {
            errors.rejectValue("nickname", "invalid nickname", new Object[]{signUpForm.getNickname()}, "닉네임이 이미 있습니다");
        }

        if(accountRepository.existsByEmail(signUpForm.getEmail())) {
            errors.rejectValue("email", "invalid email", new Object[]{signUpForm.getEmail()}, "이미 존재하는 이메일입니다.");
        }
    }
}
