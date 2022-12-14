package com.nagesoft.club.modules.study;

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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class StudySettingsControllerTest {

    @Autowired AccountRepository accountRepository;
    @Autowired AccountService accountService;
    @Autowired
    StudyService studyService;
    @Autowired
    StudyRepository studyRepository;
    @Autowired ModelMapper modelMapper;
    @Autowired MockMvc mockMvc;

    @BeforeEach
    void before() {
        SignUpForm manager = SignUpForm.builder()
                .nickname("santiago")
                .email("yesmij@gmail.com")
                .password("55555555").build();
        accountService.accountCreationProcess(manager);

        SignUpForm member = SignUpForm.builder()
                .nickname("santiago22")
                .email("yesmij@naver.com")
                .password("55555555").build();
        accountService.accountCreationProcess(member);

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

    @WithUserDetails(value = "santiago", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("?????? ?????? ????????? ??????")
    @Test
    void descriptionForm_OK() throws Exception {
        mockMvc.perform(get("/study/motor/settings/description"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(view().name("study/settings/description"));
    }

    @WithUserDetails(value = "santiago22", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("?????? ?????? ????????? ??????")
    @Test
    void descriptionForm_Fail() throws Exception {
        mockMvc.perform(get("/study/motor/settings/description"))
                .andExpect(status().isForbidden());
    }

    @WithUserDetails(value = "santiago", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("?????? ?????? ?????? ??????")
    @Test
    void descriptionUpdate_Ok() throws Exception {

        mockMvc.perform(post("/study/motor/settings/description")
                        .param("shortDescription", "short description")
                        .param("fullDescription", "full description")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/study/motor/settings/description"));

        Study updatedStudy = studyRepository.findByPath("motor");
        Assertions.assertThat("short description").isEqualTo(updatedStudy.getShortDescription());
        Assertions.assertThat("full description").isEqualTo(updatedStudy.getFullDescription());
    }

    @WithUserDetails(value = "santiago", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("?????? ?????? ?????? ??????")
    @Test
    void descriptionUpdate_Fail() throws Exception {

        mockMvc.perform(post("/study/motor/settings/description")
                        .param("shortDescription", "short descriptionshort descriptionshort descriptionshort descriptionshort descriptionshort description")
                        .param("fullDescription", "full description")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("study"));

        Study updatedStudy = studyRepository.findByPath("motor");
        Assertions.assertThat("short description").isNotEqualTo(updatedStudy.getShortDescription());
        Assertions.assertThat("full description").isNotEqualTo(updatedStudy.getFullDescription());
    }
}