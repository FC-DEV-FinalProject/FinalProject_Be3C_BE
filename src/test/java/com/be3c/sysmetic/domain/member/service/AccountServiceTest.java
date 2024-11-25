package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootTest
@Transactional
class AccountServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AccountService accountService;

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
    void findEmailTest() {
        // 1. 성공
        String name = "테스트";
        String phoneNumber = "01012341234";
        Assertions.assertEquals("test1@test.com",accountService.findEmail(name, phoneNumber));

        // 2. 실패 - 존재하지 않는 회원정보
        Assertions.assertThrows(NullPointerException.class, () -> accountService.findEmail("존재하지않는회원", phoneNumber));
    }

    @Test
    @DisplayName("이메일 확인 테스트")
    void isPresentEmailTest() {
        // 1. 성공 - true
        Assertions.assertTrue(accountService.isPresentEmail("test1@test.com"));
        // 2. 실패 - false
        Assertions.assertFalse(accountService.isPresentEmail("wrong@test.com"));
    }

    @Test
    @DisplayName("비밀번호 일치 여부 확인 테스트")
    void isPasswordMatchTest() {
        String password = "123456";
        String rewritePassword = "123456";
        String worngPassword = "asdf1234";
        // 1. 성공
        Assertions.assertTrue(accountService.isPasswordMatch(password, rewritePassword));
        // 2. 실패
        Assertions.assertFalse(accountService.isPasswordMatch(password, worngPassword));
    }

    @Test
    @DisplayName("비밀번호 재설정 테스트")
    void resetPasswordTest() {
        // 1. 성공
        String email = "test1@test.com";
        String password = "RePassword12@";
        Assertions.assertTrue(accountService.resetPassword(email, password));
        // 2. 실패 - 존재하지 않는 이메일
        email = "wrong@test.com";
        Assertions.assertFalse(accountService.resetPassword(email, password));
    }


}