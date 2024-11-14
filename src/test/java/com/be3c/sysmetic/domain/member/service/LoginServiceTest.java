package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.global.config.security.JwtTokenProvider;
import com.be3c.sysmetic.global.config.security.RedisUtils;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class LoginServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private LoginService loginService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    public void setUp() {
        memberRepository.deleteAll();

        Member member = Member.builder()
                .email("test@test.com")
                .password(bCryptPasswordEncoder.encode("encodedPassword"))
                // 초기값 설정
                .id(001L)
                .roleCode("USER")
                .name("테스트")
                .nickname("테스트")
                .phoneNumber("01012341234")
                .usingStatusCode("사용")
                .totalFollow(0)
                .receiveInfoConsent("Y")
                .infoConsentDate(LocalDateTime.now())
                .receiveMarketingConsent("Y")
                .marketingConsentDate(LocalDateTime.now())
                .createdBy(1L)
                .createdDate(LocalDateTime.now())
                .modifiedBy(1L)
                .modifiedDate(LocalDateTime.now())
                .build();
        memberRepository.save(member);
    }


    @Test
    @DisplayName("이메일 조회 테스트")
    public void findEmailTest() {
        /*
            1. 이메일 찾기 성공 -> 이메일 반환
            2. 이메일 찾기 실패 -> UsernameNotFoundException 예외 발생
         */
        // 1. 이메일 찾기 성공
        String inputEmail = "test@test.com";
        String findEmail = loginService.findEmail(inputEmail);
        Assertions.assertEquals(inputEmail, findEmail);

        // 2. 이메일 찾기 실패
        String wrongEmail = "wrong@test.com";
        Assertions.assertThrows(UsernameNotFoundException.class, () -> loginService.findEmail(wrongEmail));
    }

    @Test
    @DisplayName("비밀번호 비교 테스트")
    public void validatePasswordTest() {
        /*
            1. 비밀번호 비교 성공 -> true 반환
            2. 비밀번호 비교 실패 -> false 반환
         */
        String email = "test@test.com";
        String rightPassword = "encodedPassword";
        String wrongPassword = "wrongPassword";
        // 1. 비밀번호 비교 성공
        Assertions.assertTrue(loginService.validatePassword(email, rightPassword));
        // 2. 비밀번호 비교 실패
        Assertions.assertFalse(loginService.validatePassword(email, wrongPassword));
    }

    @Test
    @DisplayName("로그인유지 선택에 따른 토큰 생성 메서드")
    public void generateTokenBasedOnRememberMeTest() {
        /*
            1. 로그인 유지 선택O -> 30일 짜리 refresh 토큰 생성 확인
            2. 로그인 유지 선택X -> 1시간 짜리 refresh 토큰 생성 확인
         */
        String email = "test@test.com";
        // 1. 로그인 유지 선택O -> 30일 짜리 refresh 토큰 생성 확인
        Map<String, String> tokenMap = loginService.generateTokenBasedOnRememberMe(email,"Y");

        String refreshToken = tokenMap.get("refreshToken");
        Claims claims = jwtTokenProvider.parseTokenClaims(refreshToken);

        long expirationTimeMills = claims.getExpiration().getTime();
        long currentTimeMills = new Date().getTime();
        long timeDifference = expirationTimeMills - currentTimeMills;

        Assertions.assertTrue(Math.abs(timeDifference - (2592000000L)) < 1000, "The difference should be close to 30 days in milliseconds");

        // 2. 로그인 유지 선택X -> 1시간 짜리 refresh 토큰 생성 확인
        tokenMap = loginService.generateTokenBasedOnRememberMe(email, "N");

        refreshToken = tokenMap.get("refreshToken");
        claims = jwtTokenProvider.parseTokenClaims(refreshToken);

        expirationTimeMills = claims.getExpiration().getTime();
        currentTimeMills = new Date().getTime();
        timeDifference = expirationTimeMills - currentTimeMills;

        Assertions.assertTrue(Math.abs(timeDifference - (3600000L)) < 1000, "The difference should be close to 30 days in milliseconds");
    }
}
