package com.nagesoft.club.event;

import com.nagesoft.club.modules.account.Account;
import com.nagesoft.club.modules.account.AccountRepository;
import com.nagesoft.club.modules.account.AccountService;
import com.nagesoft.club.modules.account.CurrentAccount;
import com.nagesoft.club.modules.account.form.SignUpForm;
import com.nagesoft.club.modules.event.EventService;
import com.nagesoft.club.modules.study.Study;
import com.nagesoft.club.modules.study.StudyRepository;
import com.nagesoft.club.modules.study.StudyService;
import com.nagesoft.club.modules.study.form.StudyForm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class EventControllerTest {

    @Autowired
    EventService eventService;
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