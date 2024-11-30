package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.dto.RegisterRequestDto;
import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.entity.MemberRole;
import com.be3c.sysmetic.domain.member.exception.MemberBadRequestException;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class RegisterServiceImplTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RegisterService registerService;

    @BeforeEach
    void setUp() {
        // 데이터 초기화
        memberRepository.deleteAll();
    }

    @Test
    void 회원가입_성공_테스트() {
        // Given
        RegisterRequestDto dto = new RegisterRequestDto();
        dto.setRoleCode(MemberRole.USER);
        dto.setEmail("test@test.com");
        dto.setPassword("Password123!");
        dto.setRewritePassword("Password123!");
        dto.setName("테스트");
        dto.setNickname("테스트닉네임");
        dto.setBirth("2000-01-01");
        dto.setPhoneNumber("01012345678");
        dto.setReceiveInfoConsent(true);
        dto.setInfoConsentDate(LocalDateTime.now().toString());
        dto.setReceiveMarketingConsent(true);
        dto.setMarketingConsentDate(LocalDateTime.now().toString());

        // When
        boolean result = registerService.registerMember(dto, null);

        // Then
        assertTrue(result);

        // 추가 확인: 저장된 데이터 검증
        Member savedMember = memberRepository.findByEmail(dto.getEmail()).orElse(null);
        assertNotNull(savedMember);
        assertEquals("test@test.com", savedMember.getEmail());
        assertEquals("테스트닉네임", savedMember.getNickname());
    }

    @Test
    void 회원가입_중복_이메일_테스트() {
        // Given
        memberRepository.save(Member.builder()
                        .roleCode("USER")
                        .email("test1@test.com")
                        .password("Asdf1234!")
                        .name("테스트")
                        .nickname("테스트닉네임")
                        .birth(LocalDate.of(2000,1,1))
                        .phoneNumber("01012345678")
                        .receiveInfoConsent("true")
                        .infoConsentDate(LocalDateTime.now())
                        .receiveMarketingConsent("true")
                        .marketingConsentDate(LocalDateTime.now())
                .build());

        RegisterRequestDto dto = new RegisterRequestDto();
        dto.setRoleCode(MemberRole.USER);
        dto.setEmail("test1@test.com"); // 중복 이메일
        dto.setPassword("Password123!");
        dto.setRewritePassword("Password123!");
        dto.setName("테스트");
        dto.setNickname("새닉네임");
        dto.setBirth("2000-01-01");
        dto.setPhoneNumber("01012345678");
        dto.setReceiveInfoConsent(true);
        dto.setInfoConsentDate(LocalDateTime.now().toString());
        dto.setReceiveMarketingConsent(true);
        dto.setMarketingConsentDate(LocalDateTime.now().toString());

        // When & Then
        MemberBadRequestException exception = assertThrows(MemberBadRequestException.class,
                () -> registerService.registerMember(dto, null));
        assertEquals("이미 사용 중인 이메일입니다. 다른 이메일을 입력해 주세요.", exception.getMessage());
    }

    @Test
    void 회원가입_중복_닉네임_테스트() {
        // Given
        memberRepository.save(Member.builder()
                .roleCode("USER")
                .email("test1@test.com")
                .password("Asdf1234!")
                .name("테스트")
                .nickname("테스트닉네임") // 중복 닉네임
                .birth(LocalDate.of(2000,1,1))
                .phoneNumber("01012345678")
                .receiveInfoConsent("true")
                .infoConsentDate(LocalDateTime.now())
                .receiveMarketingConsent("true")
                .marketingConsentDate(LocalDateTime.now())
                .build());

        RegisterRequestDto dto = new RegisterRequestDto();
        dto.setRoleCode(MemberRole.USER);
        dto.setEmail("new@test.com");
        dto.setPassword("Password123!");
        dto.setRewritePassword("Password123!");
        dto.setName("테스트");
        dto.setNickname("테스트닉네임"); // 중복 닉네임
        dto.setBirth("2000-01-01");
        dto.setPhoneNumber("01012345678");
        dto.setReceiveInfoConsent(true);
        dto.setInfoConsentDate(LocalDateTime.now().toString());
        dto.setReceiveMarketingConsent(true);
        dto.setMarketingConsentDate(LocalDateTime.now().toString());

        // When & Then
        MemberBadRequestException exception = assertThrows(MemberBadRequestException.class,
                () -> registerService.registerMember(dto, null));
        assertEquals("이미 사용 중인 닉네임입니다. 다른 닉네임을 입력해 주세요.", exception.getMessage());
    }

    @Test
    void 회원가입_잘못된_날짜_형식_테스트() {
        // Given
        RegisterRequestDto dto = new RegisterRequestDto();
        dto.setRoleCode(MemberRole.USER);
        dto.setEmail("test1@test.com");
        dto.setPassword("Password123!");
        dto.setRewritePassword("Password123!");
        dto.setName("테스트");
        dto.setNickname("테스트닉네임");
        dto.setBirth("invalid-date"); // 잘못된 날짜 형식
        dto.setPhoneNumber("01012345678");
        dto.setReceiveInfoConsent(true);
        dto.setInfoConsentDate(LocalDateTime.now().toString());
        dto.setReceiveMarketingConsent(true);
        dto.setMarketingConsentDate(LocalDateTime.now().toString());

        // When & Then
        MemberBadRequestException exception = assertThrows(MemberBadRequestException.class,
                () -> registerService.registerMember(dto, null));
        assertEquals("잘못된 날짜 형식으로 인해 회원 저장 실패", exception.getMessage());
    }
}
