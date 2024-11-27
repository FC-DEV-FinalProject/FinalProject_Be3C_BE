package com.be3c.sysmetic.domain.member.controller;

import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.global.config.security.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class LoginControllerTest {

    /*
            1. 로그인 성공
            2. 로그인 실패
                2-1. 로그인 형식 불일치
                2-2. 가입하지 않은 이메일
                2-3. 비밀번호 불일치
        */

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RedisUtils redisUtils;

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {  // 전제: 가입완료된 회원
        memberRepository.deleteAll();

        Member member = Member.builder()
                .email("test@test.com")
                .password(bCryptPasswordEncoder.encode("Password1@"))
                // 초기값 설정
                .id(001L)
                .roleCode("USER")
                .name("테스트")
                .nickname("테스트")
                .birth(LocalDate.of(2000,1,1))
                .phoneNumber("01012341234")
                .usingStatusCode("사용")
                .totalFollow(0)
                .totalStrategyCount(0)
                .receiveInfoConsent("true")
                .infoConsentDate(LocalDateTime.now())
                .receiveMarketingConsent("true")
                .marketingConsentDate(LocalDateTime.now())
//                .createdBy(1L)
//                .createdDate(LocalDateTime.now())
//                .modifiedBy(1L)
//                .modifiedDate(LocalDateTime.now())
                .build();
        memberRepository.save(member);
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void successLoginTest() throws Exception {
        String email = "test@test.com";
        String password = "Password1@";
        String rememberMe = "true";

        String requestBody = String.format(
                "{\"email\":\"%s\", \"password\":\"%s\", \"rememberMe\":%b}",
                email, password, rememberMe
        );

        ResultActions resultActions = mockMvc.perform(post("/v1/auth/login")
                        .content(requestBody)   // JSON 데이터를 요청 본문에 추가
                        .contentType(MediaType.APPLICATION_JSON)// 요청 Content-Type을 JSON으로 설정
                        .accept(MediaType.APPLICATION_JSON))    // 응답의 Accept 타입 설정
                .andExpect(status().isOk()) // 응답 검증
                .andExpect(header().exists("Authorization"));

//        // resultActions 구조 확인하고 싶어서 작성한 내용들
//        MvcResult mvdResult = resultActions.andReturn();
//        System.out.println("Status = " + mvdResult.getResponse().getStatus());
//        System.out.println("Authorization Header = " + mvdResult.getResponse().getHeader("Authorization"));
//        System.out.println("Response Body = " + mvdResult.getResponse().getContentAsString());

        String bearerToken = resultActions.andReturn().getResponse().getHeader("Authorization");
        Assertions.assertNotNull(bearerToken, "Authorization header should not be null");

        String accessToken = bearerToken.substring(7).trim();

        String refreshToken = redisUtils.getRefreshToken(accessToken);
        Assertions.assertNotNull(refreshToken);
    }

    @Test
    @DisplayName("로그인 실패 테스트")
    void failLoginTest() throws Exception {
        String email = "test@test.com";
        String password = "Password1@";
        String rememberMe = "true";
        // 1. 로그인 형식 불일치
        // 1-1. 이메일 형식 불일치
        String invalidEmailRequestBody = String.format(
                "{\"email\":\"%s\", \"password\":\"%s\", \"rememberMe\":%b}",
                "formatMismatch", password, rememberMe
        );

        ResultActions resultActions = mockMvc.perform(post("/v1/auth/login")
                        .content(invalidEmailRequestBody)  // JSON 본문 데이터 전달
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // 응답 검증

        // 1-2. 비밀번호 형식 불일치
        String invalidPasswordRequestBody = String.format(
                "{\"email\":\"%s\", \"password\":\"%s\", \"rememberMe\":%b}",
                email, "formatMismatch", rememberMe
        );

        resultActions = mockMvc.perform(post("/v1/auth/login")
                        .content(invalidPasswordRequestBody)  // JSON 본문 데이터 전달
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // 응답 검증

        // 2. 가입하지 않은 이메일
        String unregisterEmailRequestBody = String.format(
                "{\"email\":\"%s\", \"password\":\"%s\", \"rememberMe\":%b}",
                "unregister@test.com", password, rememberMe
        );

        resultActions = mockMvc.perform(post("/v1/auth/login")
                        .content(unregisterEmailRequestBody)  // JSON 본문 데이터 전달
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // 응답 검증 - 가입되지 않은 이메일로 인한 실패

        // 3. 비밀번호 불일치
        String unMatchPasswordRequestBody = String.format(
                "{\"email\":\"%s\", \"password\":\"%s\", \"rememberMe\":%b}",
                email, "unMatch123@", rememberMe
        );

        resultActions = mockMvc.perform(post("/v1/auth/login")
                        .content(unMatchPasswordRequestBody)  // JSON 본문 데이터 전달
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // 응답 검증 - 비밀번호 불일치로 인한 실패
    }
}