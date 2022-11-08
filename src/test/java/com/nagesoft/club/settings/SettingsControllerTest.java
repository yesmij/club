package com.nagesoft.club.settings;

import com.nagesoft.club.account.AccountRepository;
import com.nagesoft.club.account.AccountService;
import com.nagesoft.club.account.SignUpForm;
import com.nagesoft.club.domain.Account;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class SettingsControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired AccountService accountService;
    @Autowired AccountRepository accountRepository;

    @BeforeEach
    void before() {
        SignUpForm signUpForm = SignUpForm.builder()
                .nickname("santiago")
                .email("yesmij@naver.com")
                .password("1111").build();
        accountService.accountCreationProcess(signUpForm);
    }

    @WithUserDetails(value = "santiago", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("업데이트 성공")
    @Test
    void profileUpdate_OK() throws Exception {

        Account account = accountRepository.findByNickname("santiago");
        mockMvc.perform(post("/settings/profile")
                .param("bio", "M")
                .param("occupation", "sw")
                .param("location", "hanam")
                .param("url", "https://www")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"));

        Account updateAccount = accountRepository.findByNickname("santiago");

        Assertions.assertThat(account).isEqualTo(updateAccount);
        Assertions.assertThat("M").isEqualTo(updateAccount.getBio());
    }


    @WithUserDetails(value = "santiago", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("업데이트 실패")
    @Test
    void profileUpdate_Fail() throws Exception {

        Account account = accountRepository.findByNickname("santiago");
        mockMvc.perform(post("/settings/profile")
                        .param("bio", "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM")
                        .param("occupation", "sw")
                        .param("location", "hanam")
                        .param("url", "https://www")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors());

        Account updateAccount = accountRepository.findByNickname("santiago");

//        Assertions.assertThat(account).isNotEqualTo(updateAccount);
//        Assertions.assertThat("null").isNotEqualTo(updateAccount.getBio());
        assertNull(updateAccount.getBio());
        assertNull(updateAccount.getOccupation());
    }

    @WithUserDetails(value = "santiago", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("패스워드 수정폼")
    @Test
    void passwordForm() throws Exception {
        mockMvc.perform(get("/settings/password"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/password"));
    }

    @WithUserDetails(value = "santiago", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("패스워드 수정 성공")
    @Test
    void passwordChange_OK() throws Exception {
        mockMvc.perform(post("/settings/password")
                .param("newPassword", "2222")
                .param("newPasswordConfirm", "2222")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"));
    }


    @WithUserDetails(value = "santiago", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("패스워드 수정 실패")
    @Test
    void passwordChange_Fail() throws Exception {
        mockMvc.perform(post("/settings/password")
                        .param("newPassword", "22222222")
                        .param("newPasswordConfirm", "111111111")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("passwordForm"))
                //.andExpect(model().attributeExists("account"))  // todo 에러 확인 필요
                .andExpect(model().hasErrors());
    }

    @WithUserDetails(value = "santiago", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("알람 변경 폼")
    @Test
    void notification_form() throws Exception {
        mockMvc.perform(get("/settings/notifications")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("notificationForm"))
                .andExpect(view().name("settings/notifications"));
    }

    @WithUserDetails(value = "santiago", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("알람 변경 성공")
    @Test
    void notification_ok() throws Exception {
        mockMvc.perform(post("/settings/notifications")
                .param("studyCreatedByEmail", "true")
                .param("studyCreatedByWeb", "true")
                .param("studyEnrollmentResultByEmail", "false")
                .param("studyEnrollmentResultByWeb", "false")
                .param("studyUpdatedByEmail", "true")
                .param("studyUpdatedByWeb", "false")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"));
                //.andExpect(model().attributeExists());
    }

    @WithUserDetails(value = "santiago", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("알람 변경 fail")
    @Test
    void notification_fail() throws Exception {
        mockMvc.perform(post("/settings/notifications")
                        .param("studyCreatedByEmail", "1234")
                        .param("studyCreatedByWeb", "true")
                        .param("studyEnrollmentResultByEmail", "false")
                        .param("studyEnrollmentResultByWeb", "false")
                        .param("studyUpdatedByEmail", "true")
                        .param("studyUpdatedByWeb", "false")
                        .with(csrf()))
                //.andExpect(status().isOk())
                .andExpect(model().hasErrors());
        //.andExpect(model().attributeExists());
    }

}