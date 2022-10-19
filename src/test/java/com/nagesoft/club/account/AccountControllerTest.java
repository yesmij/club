package com.nagesoft.club.account;

import com.nagesoft.club.domain.Account;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private AccountRepository accountRepository;
    @Autowired private AccountService accountService ;

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
        String email = "yesmij@naver.com";
        mockMvc.perform(post("/sign-up")
                        .param("nickname", "santiago")
                        .param("email", email)
                        .param("password", "1111")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));

        Assertions.assertThat(accountRepository.existsByEmail(email)).isTrue();
        then(javaMailSender).should().send(any(SimpleMailMessage.class));
        
        //password encoding
        Account account = accountRepository.findByEmail(email);
        assertNotNull(account);
        log.info(account.getPassword());
        assertNotEquals("1111", account.getPassword());
    }

    @DisplayName("이메일 체크 확인 - 오류")
    @Test
    void checkEmail_fail() throws Exception {
        mockMvc.perform(get("/check-email-token")
                .param("emailToken", "aaaaa")
                .param("email", "aaaa"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("account/checked-email-token"));
    }

    @DisplayName("이메일 체크 확인 - 성공")
    @Test
    void checkEmail_success() throws Exception {
        // 회원가입 -> 토큰 생성 -> 이메일,토큰 파라미터 -> 비교
        SignUpForm signUpForm = SignUpForm.builder()
                .nickname("santiago")
                .email("yesmij@naver.com")
                .password("1111").build();
        accountService.accountCreationProcess(signUpForm);
        Account savedAccount = accountRepository.findByEmail("yesmij@naver.com");

                mockMvc.perform(get("/check-email-token")
                        .param("emailToken", savedAccount.getEmailCheckToken())
                        .param("email", savedAccount.getEmail()))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(model().attributeExists("numberOfCount"));
    }

}