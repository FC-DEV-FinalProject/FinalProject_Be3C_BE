package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

@SpringBootTest
@AutoConfigureMockMvc
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
                .birth(LocalDateTime.parse("2000-01-01T12:00:00"))
                .phoneNumber("01012341234")
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
        Assertions.assertNull(accountService.findEmail("존재하지않는회원", phoneNumber));

//        [null일 때 예외를 발생시키도록 수정한다면, 아래 코드로 테스트 진행]
//        Assertions.assertThrows(NullPointerException.class, () -> {
//            accountService.findEmail("존재하지않는회원", phoneNumber);
//        });
    }


}