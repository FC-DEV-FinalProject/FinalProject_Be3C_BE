package com.be3c.sysmetic.domain.member.controller;

import com.be3c.sysmetic.domain.member.dto.FindEmailRequestDto;
import com.be3c.sysmetic.domain.member.dto.ResetPasswordRequestDto;
import com.be3c.sysmetic.domain.member.exception.MemberBadRequestException;
import com.be3c.sysmetic.domain.member.service.AccountService;
import com.be3c.sysmetic.domain.member.service.RegisterService;
import com.be3c.sysmetic.global.common.response.APIResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.matchers.Null;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AccountController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;

    @MockBean
    private RegisterService registerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("이메일 찾기 - 성공")
    void findEmail_ShouldReturnEmail_WhenValidRequest() throws Exception {
        // Given
        FindEmailRequestDto requestDto = new FindEmailRequestDto("테스트이름", "01012345678");
        String expectedEmail = "john@example.com";

        when(accountService.findEmail(requestDto.getName(), requestDto.getPhoneNumber()))
                .thenReturn(expectedEmail);

        // When & Then
        mockMvc.perform(post("/v1/auth/find-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(expectedEmail)));

        verify(accountService, times(1)).findEmail(requestDto.getName(), requestDto.getPhoneNumber());
    }

    @Test
    @DisplayName("이메일 확인 및 인증코드 발송 - 성공")
    void checkEmailAndSendCode_ShouldSendVerificationCode_WhenEmailIsValid() throws Exception {
        // Given
        String email = "john@example.com";

        when(accountService.isPresentEmail(email)).thenReturn(true);
        when(registerService.sendVerifyEmailCode(email)).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/v1/auth/reset-password")
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(accountService, times(1)).isPresentEmail(email);
        verify(registerService, times(1)).sendVerifyEmailCode(email);
    }

    @Test
    @DisplayName("이메일 확인 및 인증코드 발송 - 실패 (이메일 형식 오류)")
    void checkEmailAndSendCode_ShouldFail_WhenEmailIsInvalid() throws Exception {
        // Given
        String invalidEmail = "not-an-email";

        // When & Then
        mockMvc.perform(get("/v1/auth/reset-password")
                        .param("email", invalidEmail))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("유효한 이메일 형식이 아닙니다."));
    }

    @Test
    @DisplayName("비밀번호 재설정 - 성공")
    void resetPassword_ShouldResetPassword_WhenValidRequest() throws Exception {
        // Given
        ResetPasswordRequestDto requestDto = new ResetPasswordRequestDto("john@example.com", "newPassword123!", "newPassword123!");

        when(accountService.isPasswordMatch(requestDto.getPassword(), requestDto.getRewritePassword())).thenReturn(true);
        when(accountService.resetPassword(requestDto.getEmail(), requestDto.getPassword(), requestDto.getRewritePassword())).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/v1/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        verify(accountService, times(1)).isPasswordMatch(requestDto.getPassword(), requestDto.getRewritePassword());
        verify(accountService, times(1)).resetPassword(requestDto.getEmail(), requestDto.getPassword(), requestDto.getRewritePassword());
    }

    @Test
    @DisplayName("비밀번호 재설정 - 실패 (비밀번호 불일치)")
    void resetPassword_ShouldFail_WhenPasswordsDoNotMatch() throws Exception {
        // Given
        ResetPasswordRequestDto requestDto = new ResetPasswordRequestDto("test@example.com", "Password123!", "differentPwd12!");

        doThrow(new MemberBadRequestException("비밀번호가 일치하지 않습니다."))
                .when(accountService).isPasswordMatch(requestDto.getPassword(), requestDto.getRewritePassword());

        // When & Then
        mockMvc.perform(post("/v1/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("비밀번호가 일치하지 않습니다."));

        verify(accountService, times(1)).isPasswordMatch(requestDto.getPassword(), requestDto.getRewritePassword());
    }
}
