package com.nagesoft.club.study;

import com.nagesoft.club.account.AccountRepository;
import com.nagesoft.club.domain.Account;
import com.nagesoft.club.domain.Study;
import com.nagesoft.club.study.form.StudyDescriptionForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class StudyService {
    private final StudyRepository studyRepository;
    private final AccountRepository accountRepository;

    public Study createStudy(Study study, Account account) {
        Study savedStudy = studyRepository.save(study);
//        Account accountGetOne =  accountRepository.getOne(account.getId());
        //savedStudy.getManagers().add(account);  // todo Account로 변경!!
        savedStudy.addManager(account);
        return savedStudy;
    }

//    public Study updateDescription(Study study) {
    public void updateDescription(String path, StudyDescriptionForm studyDescriptionForm) {
//        System.out.println("Service : study.Desc = " + study.getFullDescription());
        Study study = studyRepository.findByPath(path);
        study.setShortDescription(studyDescriptionForm.getShortDescription());
        study.setFullDescription(studyDescriptionForm.getFullDescription());
    }
}
