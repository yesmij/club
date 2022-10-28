package com.nagesoft.club.main;

import com.nagesoft.club.account.AccountRepository;
import com.nagesoft.club.account.AccountService;
import com.nagesoft.club.account.SignUpForm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class MainControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired AccountService accountService;
    @Autowired AccountRepository accountRepository;

    @BeforeEach
    void before() {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setEmail("yesmij@gmail.com");
        signUpForm.setNickname("santiago");
        signUpForm.setPassword("11111111");
        accountService.accountCreationProcess(signUpForm);
    }

    @AfterEach
    void after() {
        accountRepository.deleteAll();
    }

    @DisplayName("이메일 로그인 테스트")
    @Test
    void loginByEmail() throws Exception {

        mockMvc.perform(post("/login")
                .param("username", "yesmij@gmail.com")
                .param("password", "11111111")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("santiago"));
    }

    @DisplayName("닉네임 로그인 테스트")
    @Test
    void loginByNickname() throws Exception {

        mockMvc.perform(post("/login")
                        .param("username", "santiago")
                        .param("password", "11111111")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("santiago"));
    }

    @DisplayName("로그인 실패 테스트")
    @Test
    void loginFailure() throws Exception {

        mockMvc.perform(post("/login")
                        .param("username", "santiago")
                        .param("password", "11221111")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @DisplayName("로그아웃 테스트")
    @Test
    void logout() throws Exception {

        mockMvc.perform(post("/logout")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(unauthenticated());
    }

}