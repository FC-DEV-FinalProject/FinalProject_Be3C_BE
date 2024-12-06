package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.global.config.security.JwtTokenProvider;
import com.be3c.sysmetic.global.config.security.RedisUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RedisUtils redisUtils;

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();


    @Test
    @DisplayName("로그인 성공 테스트")
    void testGetUsernameFromToken() throws Exception {

        String email = "testGetUsernameFromToken@test.com";
        String password = "Password1@";
        String rememberMe = "true";

        Member member = Member.builder()
                .email(email)
                .password(bCryptPasswordEncoder.encode(password))
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
                .build();
        memberRepository.save(member);



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

        String bearerToken = resultActions.andReturn().getResponse().getHeader("Authorization");
        Assertions.assertNotNull(bearerToken, "Authorization header should not be null");

        String accessToken = bearerToken.substring(7).trim();

        String refreshToken = redisUtils.getRefreshToken(accessToken);
        Assertions.assertNotNull(refreshToken);


        String emailProvided = jwtTokenProvider.getUsernameFromToken(accessToken);
        Assertions.assertEquals(emailProvided, email);

    }

}
