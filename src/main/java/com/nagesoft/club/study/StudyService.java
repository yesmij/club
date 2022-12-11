package com.nagesoft.club.study;

import com.nagesoft.club.account.AccountRepository;
import com.nagesoft.club.domain.Account;
import com.nagesoft.club.domain.Study;
import com.nagesoft.club.domain.Tag;
import com.nagesoft.club.study.form.StudyDescriptionForm;
import com.nagesoft.club.tag.TagRepository;
import com.nagesoft.club.zone.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Transactional
@RequiredArgsConstructor
@Service
public class StudyService {
    private final StudyRepository studyRepository;
    private final ModelMapper modelMapper;
    private final TagRepository tagRepository;

    public Study createStudy(Study study, Account account) {
        Study savedStudy = studyRepository.save(study);
//        Account accountGetOne =  accountRepository.getOne(account.getId());
        //savedStudy.getManagers().add(account);  // todo Account로 변경!!
        savedStudy.addManager(account);
        return savedStudy;
    }

//    public Study updateDescription(Study study) {
    public void updateDescription(Study study, StudyDescriptionForm studyDescriptionForm) {
////        System.out.println("Service : study.Desc = " + study.getFullDescription());
//        study.setShortDescription(studyDescriptionForm.getShortDescription());
//        study.setFullDescription(studyDescriptionForm.getFullDescription());
        modelMapper.map(studyDescriptionForm, study);
    }

    public Study getStudyToUpdate(String path, Account account) {
        //Optional<Account> byId = accountRepository.findById(account.getId());
        Study study = getStudy(path);
        if(!account.isManagerOf(study)) {
            throw new AccessDeniedException("ㅅㅏ용할 수 없습니다.");
        }
        return study;
    }

    private Study getStudy(String path) {
        Study study = studyRepository.findByPath(path);
        if(study == null) {
            throw new IllegalArgumentException("해당 스터디가 아닙니다. path = " + path );
        }
        return study;
    }

    public void updateBanner(Study study, String image) {
        study.setImage(image);
    }

    public void updateEnable(Study study, boolean enableFlag) {
        study.setUseBanner(enableFlag);
    }

    public List<Tag> getTagWhitelist() {
        return tagRepository.findAll();
    }

    public Set<Tag> getStudyTags(Study study) {
        return study.getTags();
    }

    public void addTagToStudy(Study study, Tag tag) {
        study.getTags().add(tag);
    }

    public void removeTagToStudy(Study study, Tag tag) {
        study.getTags().remove(tag);
    }

//    public void getStudyTags(Study study) {
//        tagRepository.
//    }
}
