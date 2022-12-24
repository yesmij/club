package com.nagesoft.club.study;

import com.nagesoft.club.domain.*;
import com.nagesoft.club.event.EventRepository;
import com.nagesoft.club.study.form.StudyDescriptionForm;
import com.nagesoft.club.study.form.StudyForm;
import com.nagesoft.club.tag.TagRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Transactional
@RequiredArgsConstructor
@Service
public class StudyService {
    private final StudyRepository studyRepository;
    private final ModelMapper modelMapper;
    private final TagRepository tagRepository;
    private final EventRepository eventRepository;

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
        Study study = studyRepository.findByPath(path);
        checkIfExistingStudy(path, study);
        checkManagerOf(account, study);
        return study;
    }

    private void checkManagerOf(Account account, Study study) {
        if(!study.isManagerOf(account)) {
            throw new AccessDeniedException("ㅅㅏ용할 수 없습니다.");
        }
    }
    private void checkIfExistingStudy(String path, Study study) {
        if(study == null) {
            throw new IllegalArgumentException("해당 스터디가 아닙니다. path = " + path);
        }
    }
    public Study getWithManagerByStudy(String path, Account account) {
        Study study = studyRepository.findStudyWithManagerByPath(path);
        checkIfExistingStudy(path, study);
        checkManagerOf(account, study);
        return study;
    }

    public Study getWithMemberByStudy(String path, Account account) {
        Study study = studyRepository.findStudyWithMemberByPath(path);
        checkIfExistingStudy(path, study);
        //checkMemberOf(account, study);
        return study;
    }

    private void checkMemberOf(Account account, Study study) {
        if(!study.isMemberOf(account)) {
            throw new AccessDeniedException("멤버가 아닙니다.");
        }
    }

    public Study getWithTagsAndManagerByStudy(String path, Account account) {
        Study study = studyRepository.findStudyWithTagsAndManagersByPath(path);
        checkIfExistingStudy(path, study);
        checkManagerOf(account, study);
        return study;
    }

    public Study getWithZonesAndManagerByStudy(String path, Account account) {
        Study study = studyRepository.findStudyWithZonesAndManagersByPath(path);
        checkIfExistingStudy(path, study);
        checkManagerOf(account, study);
        return study;
    }

    public void updateBanner(Study study, String image) {
        study.setProfileImage(image);
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

    public void addZoneToStudy(Study study, Zone zone) {
        study.getZones().add(zone);
    }

    public void removeZoneToStudy(Study study, Zone zone) {
        study.getZones().remove(zone);
    }

    public void publishStudy(Study study) {
        study.publish();
    }

    public void closeStudy(Study study) {
        study.close();
    }

    public void startRecruitStudy(Study study) {
        study.startRecruit();
    }

    public void stopRecruitStudy(Study study) {
        study.stopRecruit();
    }

    public void updateTitle(Study study, String newTitle) {
        study.setTitle(newTitle);
    }

    public Study updatePath(Study study, String newPath) {
        study.setPath(newPath);
        return studyRepository.findByPath(newPath);
    }

    public boolean isValidatePath(String newPath) {
        if(!newPath.matches(StudyForm.VALID_PATH_PATTERN)) {
            return false;
        }
        return !studyRepository.existsByPath(newPath);
    }

    public boolean isValidateTitle(String newTitle) {
        if(newTitle.length() >= 50) {
            return false;
        }
        return newTitle.length() <= 50;
    }

    public boolean isValidateRemove(Study study) {
        return !study.isPublished() && study.isClosed();
    }

    public void removeStudy(Study study) {
        if(study.isRemovable()) {
            studyRepository.delete(study);
        } else {
            throw new IllegalArgumentException("削除は不可ですね！！");
        }
    }

    public void joinStudy(Study study, Account account) {
        if(study.getMembers().contains(account) && study.getManagers().contains(account)) {
            throw new RuntimeException("이미 가입한 회원입니다. 또는 매니저");
        }
        study.addMember(account);
        //study.getMembers().add(account);
    }

    public void leaveStudy(Study study, Account account) {
        if(!study.getMembers().contains(account) && study.getManagers().contains(account)) {
            throw new RuntimeException("가입한 회원이 아닙니다. 또는 매니저");
        }
        study.getMembers().remove(account);
    }

    public Study getStudy(String path) {
        Study study = studyRepository.findByPath(path);
        checkIfExistingStudy(path, study);
        return study;
    }

    public List<Event> getEvents(Study study) {
        return eventRepository.findByStudy(study);
    }

//    public void getStudyTags(Study study) {
//        tagRepository.
//    }
}
