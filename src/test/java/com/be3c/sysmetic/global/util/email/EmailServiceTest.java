package com.be3c.sysmetic.global.util.email;

import com.be3c.sysmetic.SysmeticApplication;
import com.be3c.sysmetic.global.config.security.RedisUtils;
import com.be3c.sysmetic.global.util.email.dto.*;
import com.be3c.sysmetic.global.util.email.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = SysmeticApplication.class)
public class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * response로 확인 불가능한 항목:
     * ADD_TEST_EMAIL은 꼭 존재하는 주소로 테스트
     * ADD_TEST_EMAIL로 메일이 발송되었는지 확인
     *
     * response 테스트 가능한 항목:
     * 각 주소록에 ADD_TEST_EMAIL이 존재하고, UPDATE_TEST_EMAIL가 수정되고, DELETE_TEST_EMAIL이 존재하지 않아야 함
     */
    private static final String ADD_TEST_EMAIL = "sysmetic.team@gmail.com";
    private static final String UPDATE_TEST_EMAIL = "update@test.com";
    private static final String DELETE_TEST_EMAIL = "delete@test.com";


    // 주소록 API TEST

    @Test
    void testAddUserSubscriberRequest() {
        // 구독 요청을 위한 SubscriberRequest 생성
        SubscriberRequest subscriberRequest = SubscriberRequest.builder()
                .subscribers(List.of(
                        Subscriber.builder()
                                .email(ADD_TEST_EMAIL)
                                .name("Test User")
                                .subscribedDate(LocalDateTime.now())
                        .build(),
                        Subscriber.builder()
                                .email(UPDATE_TEST_EMAIL)
                                .name("Test User")
                                .subscribedDate(LocalDateTime.now())
                                .build(),
                        Subscriber.builder()
                                .email(DELETE_TEST_EMAIL)
                                .name("Test User")
                                .subscribedDate(LocalDateTime.now())
                                .build()
                ))
                .build();

        // 구독자 추가 요청
        StibeeApiResponse response = emailService.addUserSubscriberRequest(subscriberRequest);
        assertNotNull(response);
        assertTrue(response.isOk(), "응답 상태가 'ok'여야 합니다.");
//        assertEquals(ADD_TEST_EMAIL, response.getValue().getSuccess().get(0).getEmail());
    }

    @Test
    void testAddTraderSubscriberRequest() {
        // 구독 요청을 위한 SubscriberRequest 생성
        SubscriberRequest subscriberRequest = SubscriberRequest.builder()
                .subscribers(List.of(
                        Subscriber.builder()
                        .email(ADD_TEST_EMAIL)
                        .name("Test User")
                        .subscribedDate(LocalDateTime.now())
                        .build(),
                        Subscriber.builder()
                                .email(UPDATE_TEST_EMAIL)
                                .name("Test User")
                                .subscribedDate(LocalDateTime.now())
                                .build(),
                        Subscriber.builder()
                                .email(DELETE_TEST_EMAIL)
                                .name("Test User")
                                .subscribedDate(LocalDateTime.now())
                                .build()
                ))
                .build();

        // 구독자 추가 요청
        StibeeApiResponse response = emailService.addTraderSubscriberRequest(subscriberRequest);
        assertNotNull(response);
        assertTrue(response.isOk(), "응답 상태가 'ok'여야 합니다.");
//        assertEquals(ADD_TEST_EMAIL, response.getValue().getSuccess().get(0).getEmail());
    }




    // 수정 테스트
    @Test
    void testUpdateUserSubscriberRequest() {

        SubscriberRequest subscriberRequest = SubscriberRequest.builder()
                .subscribers(List.of(Subscriber.builder()
                        .email(UPDATE_TEST_EMAIL)
                        .name("Updated User")
                        .subscribedDate(LocalDateTime.now())
                        .build()))
                .build();

        StibeeApiResponse response = emailService.updateUserSubscriberRequest(subscriberRequest);

        assertNotNull(response);
        assertTrue(response.isOk(), "응답 상태가 'ok'여야 합니다.");
//        assertEquals("Updated User", response.getValue().getUpdate().get(0).getName());
    }

    @Test
    void testUpdateTraderSubscriberRequest() {

        SubscriberRequest subscriberRequest = SubscriberRequest.builder()
                .subscribers(List.of(Subscriber.builder()
                        .email(UPDATE_TEST_EMAIL)
                        .name("Updated Trader")
                        .subscribedDate(LocalDateTime.now())
                        .build()))
                .build();


        StibeeApiResponse response = emailService.updateTraderSubscriberRequest(subscriberRequest);

        assertNotNull(response);
        assertTrue(response.isOk(), "응답 상태가 'ok'여야 합니다.");

    }



    // 삭제 테스트
    @Test
    void testDeleteUserSubscriberRequest() {
        List<String> emailsToDelete = List.of(DELETE_TEST_EMAIL);

        StibeeSimpleResponse response = emailService.deleteUserSubscriberRequest(emailsToDelete);

        assertNotNull(response);
        assertTrue(response.isOk(), "응답 상태가 'ok'여야 합니다.");
    }

    @Test
    void testDeleteTraderRequest() {
        List<String> emailsToDelete = List.of(DELETE_TEST_EMAIL);

        StibeeSimpleResponse response = emailService.deleteTraderSubscriberRequest(emailsToDelete);

        assertNotNull(response);
        assertTrue(response.isOk(), "응답 상태가 'ok'여야 합니다.");
    }





    // Email API 테스트


    @Test
    void testSendAndSaveAuthCode() throws InterruptedException {
        // 실제 이메일 API로 인증 코드 발송
        emailService.sendAndSaveAuthCode(ADD_TEST_EMAIL);

        // Redis에 인증 코드 저장 확인
        String authCode = redisUtils.getEmailAuthCode(ADD_TEST_EMAIL);
        assertNotNull(authCode, "인증 코드가 Redis에 저장되어야 합니다.");

        // 인증 코드 형식 확인 (6자리 숫자)
        assertTrue(authCode.matches("\\d{6}"), "인증 코드는 6자리 숫자여야 합니다.");

        // 이메일 발송 확인하기
    }

    @Test
    void testNotifyStrategyInquiryRegistration() {
        // 실제 이메일 알림을 보내는 요청
        InquiryRequest inquiryRequest = new InquiryRequest(ADD_TEST_EMAIL, "Test Inquirer", 123L);

        // 실제 이메일 발송 요청
        emailService.notifyStrategyInquiryRegistration(inquiryRequest);

        // 이메일 발송이 정상적으로 처리되었는지 확인
        // 해당 부분은 서비스 내부에서 이메일 발송 성공 여부를 로그나 다른 방식으로 확인해야 합니다.
        // 예를 들어 이메일 발송이 성공적으로 이루어졌다면, 로그에 성공 메시지가 있어야 합니다.
    }

    @Test
    void testNotifyStrategyInterestRegistration() {
        // 실제 이메일 알림을 보내는 요청
        InterestRequest interestRequest = new InterestRequest(ADD_TEST_EMAIL);

        // 실제 이메일 발송 요청
        emailService.notifyStrategyInterestRegistration(interestRequest);

        // 이메일 발송이 정상적으로 처리되었는지 확인
        // 해당 부분도 로그나 이메일 발송 API의 실제 응답을 확인하는 방법으로 검증할 수 있습니다.
    }
}
