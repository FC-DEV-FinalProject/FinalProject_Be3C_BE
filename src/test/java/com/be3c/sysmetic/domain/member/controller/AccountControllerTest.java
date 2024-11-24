package com.be3c.sysmetic.domain.member.controller;

import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.domain.member.service.AccountService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MockMvc mockMvc;

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        // 초기화
        memberRepository.deleteAll();

        // 테스트 데이터 삽입
        Member member1 = Member.builder()
                .roleCode("RC001")
                .email("test1@test.com")
                .password(bCryptPasswordEncoder.encode("Password1!"))
                .name("테스트")
                .nickname("테스트닉네임")
                .birth(LocalDate.of(2000,1,1))
                .phoneNumber("01012341234")
                .totalStrategyCount(0)
                .receiveInfoConsent("true")
                .infoConsentDate(LocalDateTime.now())
                .receiveMarketingConsent("true")
                .marketingConsentDate(LocalDateTime.now())
                .build();
        memberRepository.save(member1);
    }

    @AfterEach
    void tearDown() {
        // 초기화
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("이메일 찾기 테스트")
    void findEmailTest() throws Exception {
        String name = "테스트";
        String phoneNumber = "01012341234";
        String requestBody = String.format(
                "{\"name\":\"%s\", \"phoneNumber\":\"%s\"}", name, phoneNumber
        );

        // 1. 성공
        mockMvc.perform(post("/auth/find-email")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 2. 실패 - 형식 불일치 (postman으로 테스트 완료)
        // 이름
        name = "qnfdlfcl";
        phoneNumber = "01012341234";
        requestBody = String.format(
                "{\"name\":\"%s\", \"phoneNumber\":\"%s\"}", name, phoneNumber
        );
        mockMvc.perform(post("/auth/find-email")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // 휴대폰 번호
        name = "테스트";
        phoneNumber = "123";
        requestBody = String.format(
                "{\"name\":\"%s\", \"phoneNumber\":\"%s\"}", name, phoneNumber
        );
        mockMvc.perform(post("/auth/find-email")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // 3. 실패 - 존재하지 않는 회원정보
        name = "존재하지않는회원";
        phoneNumber = "01012341234";
        requestBody = String.format(
                "{\"name\":\"%s\", \"phoneNumber\":\"%s\"}", name, phoneNumber
        );
        mockMvc.perform(post("/auth/find-email")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("일치하는 회원 정보를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("이메일 확인 및 인증코드 발송 테스트")
    void checkEmailAndSendCodeTest() throws Exception {
        // 1. 성공
        mockMvc.perform(get("/auth/reset-password")
                        .param("email", "test1@test.com"))
                .andExpect(status().isOk());

        // 2. 실패 - 이메일 형식 불일치
        mockMvc.perform(get("/auth/reset-password")
                        .param("email", "invalid-email"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("유효한 이메일 형식이 아닙니다."));

        // 3. 실패 - 존재하지 않는 이메일
        mockMvc.perform(get("/auth/reset-password")
                        .param("email", "nonexist@example.com"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("잘못된 형식 또는 누락된 데이터가 있습니다."));
    }

    @Test
    @DisplayName("비밀번호 재설정 테스트")
    void resetPasswordTest() throws Exception {
        String email = "test1@test.com";
        String password = "Resetpwd123!";
        String rewritePassword = "Resetpwd123!";

        // 1. 성공
        String requestBody = String.format(
                "{\"email\":\"%s\", \"password\":\"%s\", \"rewritePassword\":\"%s\"}"
                , email, password, rewritePassword
        );

        mockMvc.perform(post("/auth/reset-password")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 2. 실패 - 비밀번호 형식 불일치
        String wrongPassword = "wrongPassword";

        requestBody = String.format(
                "{\"email\":\"%s\", \"password\":\"%s\", \"rewritePassword\":\"%s\"}"
                , email, wrongPassword, rewritePassword
        );

        mockMvc.perform(post("/auth/reset-password")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("비밀번호는 영문자(대, 소문자), 숫자, 특수문자를 포함하여 6~20자로 입력해야 합니다."));

        // 3. 실패 - 비밀번호 불일치
        String unMatchedPassword = "unMatch123!";

        requestBody = String.format(
                "{\"email\":\"%s\", \"password\":\"%s\", \"rewritePassword\":\"%s\"}"
                , email, unMatchedPassword, rewritePassword
        );

        mockMvc.perform(post("/auth/reset-password")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("비밀번호 불일치"));

        // 4. 실패 - 존재하지 않는 이메일
        String nonExistEmail = "nonExist@test.com";

        requestBody = String.format(
                "{\"email\":\"%s\", \"password\":\"%s\", \"rewritePassword\":\"%s\"}"
                , nonExistEmail, password, rewritePassword
        );

        mockMvc.perform(post("/auth/reset-password")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("서버 오류가 발생했습니다."));
    }
}