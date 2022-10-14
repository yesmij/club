package com.nagesoft.club.account;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired MockMvc mockMvc;

    @Test
    void signUpTest() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andDo(print());
    }

    @Test
    void signUpSave() {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setEmail("yesmij@gmial.com");
        signUpForm.setNickname("santiago");
        signUpForm.setPassword("1234");
//        mockMvc.perform(post("/sign-up").pa
//                .andExpect(status().isOk())
//                .a
    }

}