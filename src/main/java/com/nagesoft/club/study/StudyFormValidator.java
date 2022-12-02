package com.nagesoft.club.study;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@RequiredArgsConstructor
@Component
public class StudyFormValidator implements Validator {

    private final StudyRepository studyRepository;

    @Override
    public boolean supports(Class<?> aClass) {
//        return StudyForm.class.isAssignableFrom(aClass);
        return aClass.isAssignableFrom(StudyForm.class);
    }

    @Override
    public void validate(Object o, Errors errors) {
        StudyForm studyForm = (StudyForm) o;
        Boolean existPath = studyRepository.existsByPath(studyForm.getPath());
        if (existPath) {
//            errors.rejectValue(studyForm.getPath(), "wrong.path", new Object[]{studyForm.getPath()}, "이미 사용중인 Path입니다.");
            errors.rejectValue("path", "wrong.path", "이미 사용중인 Path입니다.");
        }

    }
}
