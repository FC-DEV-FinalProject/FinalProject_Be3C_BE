package com.be3c.sysmetic.domain.member.controller;

import com.be3c.sysmetic.domain.member.dto.EmailResponseDto;
import com.be3c.sysmetic.domain.member.dto.RegisterRequestDto;
import com.be3c.sysmetic.domain.member.entity.MemberRole;
import com.be3c.sysmetic.domain.member.service.RegisterService;
import com.be3c.sysmetic.domain.member.validation.RegisterValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.Errors;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegisterController.class)
@Slf4j
@AutoConfigureMockMvc(addFilters = false) // Spring Security 필터 비활성화
class RegisterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegisterService registerService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RegisterValidator registerValidator;

    @Test
    void 이메일_중복_확인_API_테스트() throws Exception {
        // Mocking
        Mockito.when(registerService.checkEmailDuplication("test@test.com")).thenReturn(true); // 반환 값 설정

        // Test
        mockMvc.perform(get("/v1/auth/check-duplicate-email")
                        .param("email", "test@test.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void 이메일_인증코드_전송_API_테스트() throws Exception {
        // Mocking
        Mockito.when(registerService.sendVerifyEmailCode("test@test.com")).thenReturn(true); // 이 메서드는 void 메서드임

        // Test
        mockMvc.perform(get("/v1/auth/email-code")
                        .param("email", "test@test.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void 이메일_인증코드_확인_API_테스트() throws Exception {
        // Given
        EmailResponseDto emailResponseDto = new EmailResponseDto();
        emailResponseDto.setEmail("test@test.com");
        emailResponseDto.setAuthCode("123456");

        // Mocking
        Mockito.when(registerService.checkVerifyEmailCode("test@test.com", "123456")).thenReturn(true);

        // Test
        mockMvc.perform(post("/v1/auth/email-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailResponseDto)))
                .andExpect(status().isOk());
    }

    @Test
    void 닉네임_중복_확인_API_테스트() throws Exception {
        // Mocking
        Mockito.when(registerService.checkNicknameDuplication("닉네임테스트")).thenReturn(true); // 반환 값 설정

        // Test
        mockMvc.perform(get("/v1/auth/check-nickname")
                        .param("nickname", "닉네임테스트")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void 회원가입_API_테스트() throws Exception {
        // Given
        RegisterRequestDto registerRequestDto = RegisterRequestDto.builder()
                .roleCode(MemberRole.USER)
                .email("test@test.com")
                .password("Password123!")
                .rewritePassword("Password123!")
                .name("테스트")
                .nickname("테스트닉네임1")
                .birth("2000-01-01")
                .phoneNumber("01012345678")
                .receiveInfoConsent(true)
                .infoConsentDate("2023-01-01T00:00:00")
                .receiveMarketingConsent(true)
                .marketingConsentDate("2023-01-01T00:00:00")
                .build();

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "profile-image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "Test Image".getBytes()
        );

        MockMultipartFile dto = new MockMultipartFile(
                "registerResponseDto", // 컨트롤러에서 @RequestPart로 매핑되는 이름
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(registerRequestDto)
        );

        // Mocking
        Mockito.doReturn(true).when(registerService).registerMember(any(RegisterRequestDto.class), any());

        // Test
        MvcResult result = mockMvc.perform(multipart("/v1/auth/register")
                        .file(dto)
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andReturn();

        // 에러 메시지 확인
        String responseBody = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        log.warn("Response: {}", responseBody);
    }
}
