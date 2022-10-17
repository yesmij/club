package com.nagesoft.club.account;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private AccountRepository accountRepository;

    @MockBean JavaMailSender javaMailSender;

    @DisplayName("회원가입 - View")
    @Test
    void signUpTest() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andDo(print());
    }

    @DisplayName("회원가입 - 실패")
    @Test
    void signUpForm_fail() throws Exception {
        mockMvc.perform(post("/sign-up")
                        .param("nickname", "santiago")
                        .param("email", "yesmij")
                        .param("password", "11")
                        .with(csrf()))

                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"));
    }

    @DisplayName("회원가입 - 성공")
    @Test
    void signUpForm_success() throws Exception {
        mockMvc.perform(post("/sign-up")
                        .param("nickname", "santiago")
                        .param("email", "yesmij@naver.com")
                        .param("password", "1111")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));

        Assertions.assertThat(accountRepository.existsByEmail("yesmij@naver.com")).isTrue();
        then(javaMailSender).should().send(any(SimpleMailMessage.class));
    }

}