package com.be3c.sysmetic.domain.member.controller;

import com.be3c.sysmetic.domain.member.dto.RegisterResponseDto;
import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.global.config.security.RedisUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class RegisterControllerTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private MockMvc mockMvc;

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private JacksonTester<RegisterResponseDto> json;
    @Autowired
    private DefaultErrorAttributes errorAttributes;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();

        Member member = Member.builder()
                .email("test@test.com")
                .password(bCryptPasswordEncoder.encode("Password1@"))
                // 초기값 설정
                .roleCode("RC001")
                .name("테스트")
                .nickname("테스트")
                .birth(LocalDateTime.of(2000, 1, 1, 0, 0, 0))
                .phoneNumber("01012341234")
                .receiveInfoConsent("ture")
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
    @DisplayName("이메일 중복체크 테스트")
    void checkDuplicateEmailTest() throws Exception {
        // 1. 성공 - 중복되지 않은 이메일 입력
        mockMvc.perform(get("/auth/check-duplicate-email")
                .param("email", "notDubpl@test.com"))
                .andExpect(status().isOk());

        // 2. 실패 - 잘못된 형식
        ResultActions resultActions = mockMvc.perform(get("/auth/check-duplicate-email")
                .param("email", "test.test.com"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("유효한 이메일 형식이 아닙니다."));

        System.out.println("[resultActions] = " + resultActions.andReturn().getResponse().getContentAsString());

        // 3. 실패 - 중복된 이메일
        mockMvc.perform(get("/auth/check-duplicate-email")
                .param("email", "test@test.com"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("이미 사용 중인 이메일입니다. 다른 이메일을 입력해 주세요."));
    }

    @Test
    @DisplayName("이메일 인증코드 발송 및 저장 테스트")
    void sendVerificationCodeTest() {
        // 1. 성공
        // 유효한 이메일 입력


        // 2. 실패 - 잘못된 형식

    }

    @Test
    @DisplayName("이메일 인증코드 확인 테스트")
    void verifyCodeTest() throws Exception {

        // 임시 인증코드 Redis에 저장
        String email = "test@test.com";
        String authCode = "authCode";
        redisUtils.saveEmailAuthCodeWithExpireTime(email, authCode, 60 * 60 * 1000L );

        // 1. 성공 - 올바른 인증코드 입력
        String requestBody = String.format(
                "{\"email\":\"%s\", \"authCode\":\"%s\"}",
                email, authCode
        );
        mockMvc.perform(post("/auth/verify-code")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        // 인증 성공 후 인증코드 내역 삭제됐는지 확인
        mockMvc.perform(post("/auth/verify-code")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("인증코드가 일치하지 않습니다. 올바른 인증코드로 입력해 주세요."));

        // 2. 실패 - 잘못된 이메일 형식
        requestBody = String.format(
                "{\"email\":\"%s\", \"authCode\":\"%s\"}",
                "test.test.com", authCode
        );
        mockMvc.perform(post("/auth/verify-code")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("유효한 이메일 형식이 아닙니다."));

        // 3. 실패 - 인증코드 불일치
        requestBody = String.format(
                "{\"email\":\"%s\", \"authCode\":\"%s\"}",
                email, "wrongCode"
        );
        mockMvc.perform(post("/auth/verify-code")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("인증코드가 일치하지 않습니다. 올바른 인증코드로 입력해 주세요."));

        // 4. 실패 - 인증코드 만료
        // 만료될 인증코드 생성
        email = "invalid@test.com";
        redisUtils.saveEmailAuthCodeWithExpireTime(email, authCode, 1L);
        Thread.sleep(1000); // 1초 기다리기

        requestBody = String.format(
                "{\"email\":\"%s\", \"authCode\":\"%s\"}",
                email, authCode
        );
        mockMvc.perform(post("/auth/verify-code")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("인증코드가 일치하지 않습니다. 올바른 인증코드로 입력해 주세요."));
    }

    @Test
    @DisplayName("닉네임 중복체크 테스트")
    void checkDuplicateNicknameTest() throws Exception {
        String unDuplNickname = "겹치지않는닉네임";
        String invalidNicknmae = "특수문자불가능12@#";

        // 1. 성공 - 중복되지 않은 닉네임 입력
        mockMvc.perform(get("/auth/check-nickname")
                .param("nickname", unDuplNickname))
                .andExpect(status().isOk());

        // 2. 실패 - 잘못된 형식
        mockMvc.perform(get("/auth/check-nickname")
                .param("nickname", invalidNicknmae))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("닉네임은 한글 또는 숫자로 3자 이상 10자 이내로 입력해야 합니다."));

        // 3. 실패 - 중복된 닉네임
        mockMvc.perform(get("/auth/check-nickname")
                        .param("nickname", "테스트"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("이미 사용 중인 닉네임입니다. 다른 닉네임을 입력해 주세요."));
    }

    @Test
    @DisplayName("회원가입 테스트")
    void registerTest() throws Exception {

        RegisterResponseDto registerResponseDto1 = RegisterResponseDto.builder()
                .roleCode("RC001")
                .email("test2@test.com")    //
                .password("Password12@")
                .rewritePassword("Password12@")
                .name("테스트")
                .nickname("테스트1")        //
                .birth("2000-01-01T12:00:00")
                .phoneNumber("01012341234")
                .receiveInfoConsent(true)
                .infoConsentDate("2020-01-01T12:00:00")
                .receiveMarketingConsent(true)
                .marketingConsentDate("2020-01-01T12:00:00")
                .build();

        // 1. 성공
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.write(registerResponseDto1).getJson()))
                .andExpect(status().isOk());

        // 2. 실패 - 비밀번호 불일치
        registerResponseDto1.setEmail("test3@test.com");
        registerResponseDto1.setRewritePassword("wrongPwd12@");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.write(registerResponseDto1).getJson()))
                        .andExpect(status().isBadRequest());

        // 3. 실패 - 중복된 이메일 또는 닉네임
//        RegisterResponseDto registerResponseDto2 = RegisterResponseDto.builder()
//                .roleCode("RC001")
//                .email("test@test.com")
//                .password("Password12@")
//                .rewritePassword("Password12@")
//                .name("테스트")
//                .nickname("테스트2")
//                .birth("2010-01-01T12:00:00")
//                .phoneNumber("01012341234")
//                .receiveInfoConsent(true)
//                .infoConsentDate("2020-01-01T12:00:00")
//                .receiveMarketingConsent(true)
//                .marketingConsentDate("2020-01-01T12:00:00")
//                .build();
//
//        mockMvc.perform(post("/auth/register")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(new ObjectMapper().writeValueAsString(registerResponseDto2)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.message").value("회원가입에 실패했습니다. 관리자에게 문의해 주세요."));

        // 4. 실패 - 내부 서버 오류

        // 0. 실패 - 잘못된 형식
        /*
            Postman으로 테스트 진행 (JSON 데이터)
            {
                "profileImage":null,
                "roleCode":"RC001",
                "email":"test@test.com",
                "password":"Password12@",
                "rewritePassword":"Password12@",
                "name":"일이삼사오육칠팔구십",
                "nickname":"닉네임",
                "birth":"2010-11-18T12:00:00",
                "phoneNumber":"01012341234",
                "receiveInfoConsent":true,
                "infoConsentDate":"2020-01-01T12:00:00",
                "receiveMarketingConsent":true,
                "marketingConsentDate":"2020-01-01T12:00:00"
            }
         */

    }
}