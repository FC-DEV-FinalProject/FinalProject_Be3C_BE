//package com.be3c.sysmetic.domain.member.service;
//
//import com.be3c.sysmetic.domain.member.dto.RegisterResponseDto;
//import com.be3c.sysmetic.domain.member.entity.Member;
//import com.be3c.sysmetic.domain.member.repository.MemberRepository;
//import com.be3c.sysmetic.global.config.security.RedisUtils;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//@SpringBootTest
//class RegisterServiceTest {
//
//    @Autowired
//    RegisterService registerService;
//
//    @Autowired
//    MemberRepository memberRepository;
//
//    @Autowired
//    RedisUtils redisUtils;
//
//    private RegisterResponseDto registerRequestDto;
//    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
//
//    @BeforeEach
//    public void setUp() {
//        memberRepository.deleteAll();
//
//        Member member = Member.builder()
//                .roleCode("RC001")
//                .email("test@test.com")
//                .password(bCryptPasswordEncoder.encode("Password1!"))
//                .name("테스트")
//                .nickname("테스트닉네임")
//                .birth(LocalDateTime.parse("2020-01-01T12:00:00"))
//                .phoneNumber("01012341234")
//                .receiveInfoConsent("true")
//                .infoConsentDate(LocalDateTime.now())
//                .receiveMarketingConsent("true")
//                .marketingConsentDate(LocalDateTime.now())
//                .build();
//
//        memberRepository.save(member);
//    }
//
//
//    @Test
//    @DisplayName("이메일 중복확인 메서드 테스트")
//    public void checkEmailDuplicationTest() {
//        Boolean result = null;
//        // 1. 중복된 경우
//        result = registerService.checkEmailDuplication("test@test.com");
//        Assertions.assertTrue(result);
//
//        // 2. 중복되지 않은 경우
//        result = registerService.checkEmailDuplication("wrong@email.com");
//        Assertions.assertFalse(result);
//    }
//
//    @Test
//    @DisplayName("이메일 인증코드 발송 및 Redis 저장 테스트")
//    public void sendVerifyEmailCodeTest() {
//        // 이메일 인증코드 발송 테스트
//
//        // 작성 필요
//
//        // Redis 저장 테스트
//        // 1. Redis 저장 성공
//        registerService.sendVerifyEmailCode("test");
//        Assertions.assertTrue("auth".equals(redisUtils.getEmailAuthCode("test")));
//
//        // 2. Redis 저장 실패
//        // (고민중) Redis Test에서 진행할지 여기에서도 진행할지
//    }
//
//    @Test
//    @DisplayName("이메일 인증코드 확인 메서드 테스트")
//    public void checkVerifyEmailCodeTest() {
//        String email = "test@test.com";
//        String authCode = "auth";
//
//        registerService.sendVerifyEmailCode(email);
//
//        // 1. 틀린 인증코드를 입력한 경우 (실패)
//        Assertions.assertFalse(registerService.checkVerifyEmailCode(email,"wrongCode"));
//        // 인증 실패한 경우, 인증 내역 유지되는지 확인
//        String auth = redisUtils.getEmailAuthCode(email);
//        Assertions.assertEquals(authCode, auth);
//
//        // 2. 올바른 인증코드를 입력한 경우 (성공)
//        Assertions.assertTrue(registerService.checkVerifyEmailCode(email, authCode));
//        // 인증 성공한 경우, 인증 내역 삭제되는지 확인
//        auth = redisUtils.getEmailAuthCode(email);
//        Assertions.assertNull(auth);
//    }
//
//    @Test
//    @DisplayName("닉네임 중복확인 테스트")
//    public void checkNicknameDuplicationTest() {
//        String duplicateNickname = "테스트닉네임";
//        String nonDuplicateNickname = "중복없는닉네임";
//
//        // 1. 닉네임 중복되는 경우
//        Boolean result = registerService.checkNicknameDuplication(duplicateNickname);
//        Assertions.assertTrue(result);
//
//        // 2. 닉네임 중복되지 않는 경우
//        result = registerService.checkNicknameDuplication(nonDuplicateNickname);
//        Assertions.assertFalse(result);
//    }
//
//    @Test
//    @DisplayName("회원가입 테스트")
//    public void registerMemberTest() {
//        RegisterResponseDto registerRequestDto = RegisterResponseDto.builder()
//                .roleCode("RC001")
//                .email("test2@test.com")
//                .password("Password1!")
//                .rewritePassword("Password1!")
//                .name("테스트")
//                .nickname("테스트닉네임2")
//                .birth("2020-01-01T12:00:00")
//                .phoneNumber("01012341234")
//                .receiveInfoConsent(true)
//                .infoConsentDate("2020-01-01T12:00:00")
//                .receiveMarketingConsent(true)
//                .marketingConsentDate("2020-01-01T12:00:00")
//                .build();
//
//        // 1. 성공
//        Assertions.assertTrue(registerService.registerMember(registerRequestDto));
//        // 이메일로 회원정보 가져오기
//        Optional<Member> member = memberRepository.findByEmail("test2@test.com");
//        // 식별번호가 자동 설정됐는지 확인
//        Assertions.assertNotNull(member.get().getId());
//        // 사용 상태 코드 - "US001", 총 팔로워 수 - 0 으로 설정됐는지 확인
//        Assertions.assertEquals("US001", member.get().getUsingStatusCode());
//        Assertions.assertEquals(0, member.get().getTotalFollow());
//        // 나머지 데이터도 입력한 값으로 저장됐는지 확인
//        Assertions.assertEquals("RC001", member.get().getRoleCode());
//        Assertions.assertEquals("test2@test.com", member.get().getEmail());
//        Assertions.assertTrue(bCryptPasswordEncoder.matches("Password1!", member.get().getPassword()));
//        Assertions.assertEquals("테스트", member.get().getName());
//        Assertions.assertEquals("테스트닉네임2", member.get().getNickname());
//        Assertions.assertEquals(LocalDateTime.parse("2020-01-01T12:00:00"), member.get().getBirth());
//        Assertions.assertEquals("01012341234", member.get().getPhoneNumber());
//        Assertions.assertEquals("true", member.get().getReceiveInfoConsent());
//        Assertions.assertEquals(LocalDateTime.parse("2020-01-01T12:00:00"), member.get().getInfoConsentDate());
//        Assertions.assertEquals("true", member.get().getReceiveMarketingConsent());
//        Assertions.assertEquals(LocalDateTime.parse("2020-01-01T12:00:00"), member.get().getMarketingConsentDate());
//
//        // 2. 실패
//    }
//}