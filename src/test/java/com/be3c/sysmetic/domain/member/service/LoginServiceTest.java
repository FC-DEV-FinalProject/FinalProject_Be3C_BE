package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.exception.MemberBadRequestException;
import com.be3c.sysmetic.domain.member.exception.MemberExceptionMessage;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.global.config.security.JwtTokenProvider;
import com.be3c.sysmetic.global.util.file.service.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class LoginServiceImplTest {

    @InjectMocks
    private LoginServiceImpl loginService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private FileService fileService;

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 이메일_조회_성공_테스트() {
        // Given
        String email = "test@test.com";
        when(memberRepository.existsByEmail(email)).thenReturn(true);

        // When
        String result = loginService.findEmail(email);

        // Then
        assertThat(result).isEqualTo(email);
        verify(memberRepository, times(1)).existsByEmail(email);
    }

    @Test
    void 이메일_조회_실패_테스트() {
        // Given
        String email = "notfound@test.com";
        when(memberRepository.existsByEmail(email)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> loginService.findEmail(email))
                .isInstanceOf(MemberBadRequestException.class)
                .hasMessage(MemberExceptionMessage.EX_1.getMessage());
        verify(memberRepository, times(1)).existsByEmail(email);
    }

    @Test
    void 비밀번호_검증_성공_테스트() {
        // Given
        String email = "test@test.com";
        String password = "Password123!";
        String hashedPassword = bCryptPasswordEncoder.encode(password);

        Member member = Member.builder()
                .email(email)
                .password(hashedPassword)
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));

        // When
        boolean result = loginService.validatePassword(email, password);

        // Then
        assertThat(result).isTrue();
        verify(memberRepository, times(1)).findByEmail(email);
    }

    @Test
    void 비밀번호_검증_실패_테스트() {
        // Given
        String email = "test@test.com";
        String password = "WrongPassword!";
        String hashedPassword = bCryptPasswordEncoder.encode("Password123!");

        Member member = Member.builder()
                .email(email)
                .password(hashedPassword)
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));

        // When & Then
        assertThatThrownBy(() -> loginService.validatePassword(email, password))
                .isInstanceOf(MemberBadRequestException.class)
                .hasMessage(MemberExceptionMessage.EX_1.getMessage());
        verify(memberRepository, times(1)).findByEmail(email);
    }

    @Test
    void 토큰_생성_테스트_rememberMe_선택() {
        // Given
        String email = "test@test.com";
        boolean rememberMe = true;

        Member member = Member.builder()
                .id(1L)
                .email(email)
                .roleCode("USER")
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(jwtTokenProvider.generateAccessToken(anyLong(), anyString(), anyString())).thenReturn("accessToken");
        when(jwtTokenProvider.generateMonthRefreshToken(anyLong(), anyString(), anyString())).thenReturn("refreshToken");

        // When
        Map<String, String> tokens = loginService.generateTokenBasedOnRememberMe(email, rememberMe);

        // Then
        assertThat(tokens).containsKeys("accessToken", "refreshToken");
        assertThat(tokens.get("accessToken")).isEqualTo("accessToken");
        assertThat(tokens.get("refreshToken")).isEqualTo("refreshToken");

        verify(memberRepository, times(1)).findByEmail(email);
        verify(jwtTokenProvider, times(1)).generateAccessToken(1L, email, "USER");
        verify(jwtTokenProvider, times(1)).generateMonthRefreshToken(1L, email, "USER");
    }

    @Test
    void 토큰_생성_테스트_rememberMe_미선택() {
        // Given
        String email = "test@test.com";
        boolean rememberMe = false;

        Member member = Member.builder()
                .id(1L)
                .email(email)
                .roleCode("USER")
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(jwtTokenProvider.generateAccessToken(anyLong(), anyString(), anyString())).thenReturn("accessToken");
        when(jwtTokenProvider.generateHourRefreshToken(anyLong(), anyString(), anyString())).thenReturn("refreshToken");

        // When
        Map<String, String> tokens = loginService.generateTokenBasedOnRememberMe(email, rememberMe);

        // Then
        assertThat(tokens).containsKeys("accessToken", "refreshToken");
        assertThat(tokens.get("accessToken")).isEqualTo("accessToken");
        assertThat(tokens.get("refreshToken")).isEqualTo("refreshToken");

        verify(memberRepository, times(1)).findByEmail(email);
        verify(jwtTokenProvider, times(1)).generateAccessToken(1L, email, "USER");
        verify(jwtTokenProvider, times(1)).generateHourRefreshToken(1L, email, "USER");
    }
}
