package com.nagesoft.club.event;

import com.nagesoft.club.account.AccountRepository;
import com.nagesoft.club.account.AccountService;
import com.nagesoft.club.account.SignUpForm;
import com.nagesoft.club.domain.Account;
import com.nagesoft.club.domain.Study;
import com.nagesoft.club.study.StudyRepository;
import com.nagesoft.club.study.StudyService;
import com.nagesoft.club.study.form.StudyForm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class EventControllerTest {

    @Autowired EventService eventService;
    @Autowired MockMvc mockMvc;
    @Autowired AccountService accountService;
    @Autowired AccountRepository accountRepository;
    @Autowired StudyService studyService;
    @Autowired StudyRepository studyRepository;
    @Autowired ModelMapper modelMapper;

    @BeforeEach
    void before() {
        SignUpForm signUpForm = SignUpForm.builder()
                .nickname("santiago")
                .email("yesmij@gmail.com")
                .password("55555555").build();
        accountService.accountCreationProcess(signUpForm);

        StudyForm studyForm = StudyForm.builder()
                .path("motor")
                .title("motor")
                .shortDescription("motor")
                .fullDescription("motor").build();
        Account santiago = accountRepository.findByNickname("santiago");
        studyService.createStudy(modelMapper.map(studyForm, Study.class), santiago);
    }

    @AfterEach
    void after() {
        accountRepository.deleteAll();
        studyRepository.deleteAll();
    }

}