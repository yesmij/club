package com.nagesoft.club.study;

import com.nagesoft.club.modules.account.Account;
import com.nagesoft.club.modules.account.AccountRepository;
import com.nagesoft.club.modules.account.AccountService;
import com.nagesoft.club.modules.account.CurrentAccount;
import com.nagesoft.club.modules.account.form.SignUpForm;
import com.nagesoft.club.modules.study.Study;
import com.nagesoft.club.modules.study.StudyRepository;
import com.nagesoft.club.modules.study.StudyService;
import com.nagesoft.club.modules.study.form.StudyForm;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class StudyControllerTest {

    @Autowired
    StudyRepository studyRepository;
    @Autowired AccountService accountService;
    @Autowired AccountRepository accountRepository;
    @Autowired
    StudyService studyService;
    @Autowired ModelMapper modelMapper;
    @Autowired MockMvc mockMvc;

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

    @DisplayName("스터디 조회")
    @WithUserDetails(value = "santiago", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void studyView() throws Exception {
        mockMvc.perform(get("/study/{path}", "motor"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("study"))
                .andExpect(view().name("study/view"));
    }


    @DisplayName("스터디 등록")//
    @WithUserDetails(value = "santiago", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void studyCreate() {
        StudyForm studyForm = StudyForm.builder()
                .path("motor")
                .title("motor")
                .shortDescription("motor")
                .fullDescription("motor").build();
        Account santiago = accountRepository.findByNickname("santiago");
        studyService.createStudy(modelMapper.map(studyForm, Study.class), santiago);
        Study savedStudy = studyRepository.findByPath("motor");

        Assertions.assertThat(savedStudy.getPath()).isEqualTo("motor");
        assertNotNull(savedStudy.getManagers().contains(santiago));
        assertTrue(savedStudy.getManagers().contains(santiago));
    }

    @DisplayName("스터디 등록 실패")
    @WithUserDetails(value = "santiago", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void studyCreateFail() throws Exception {
        mockMvc.perform(post("/new-study")
                .param("path", "motor")
                .param("title", "motormotormotormotormotormotormotormotormotormotormotormotormotormotormotor")
                .param("shortDescription", "motor")
                .param("fullDescription", "motor")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("study/new-study"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("studyForm"))
                .andExpect(model().attributeExists("account"));
    }


    // 화면 뷰
    @DisplayName("스터디 등록 폼")
    @WithUserDetails(value = "santiago", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void studyForm() throws Exception {
        mockMvc.perform(get("/new-study"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("studyForm"))
                .andExpect(model().attributeExists("account"))
                .andExpect(view().name("study/new-study"));
    }

    // 멤버 뷰

}