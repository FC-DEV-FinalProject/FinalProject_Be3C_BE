package com.be3c.sysmetic.global.util.email;

import com.be3c.sysmetic.SysmeticApplication;
import com.be3c.sysmetic.global.config.security.RedisUtils;
import com.be3c.sysmetic.global.util.email.dto.*;
import com.be3c.sysmetic.global.util.email.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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

        emailService.deleteUserSubscriberRequest(emailsToDelete).subscribe();
    }

    @Test
    void testDeleteTraderRequest() {
        List<String> emailsToDelete = List.of(DELETE_TEST_EMAIL);

        emailService.deleteTraderSubscriberRequest(emailsToDelete).subscribe();

//        assertNotNull(response);
//        assertTrue(response.isOk(), "응답 상태가 'ok'여야 합니다.");
    }





    // Email API 테스트

    @Test
    void testNotifyStrategyInquiryRegistration() {
        // 실제 이메일 알림을 보내는 요청
        InquiryRequest inquiryRequest = new InquiryRequest(ADD_TEST_EMAIL, "Test Inquirer", 123L);

        // 실제 이메일 발송 요청
        Mono<String> monoResponse = emailService.notifyStrategyInquiryRegistration(inquiryRequest);

        StepVerifier.create(monoResponse)
                .expectNext("ok")
                .expectComplete()
                .verify();

        System.out.println("monoResponse.subscribe() = " + monoResponse.subscribe());

        // 이메일 발송이 정상적으로 처리되었는지 확인
    }

    @Test
    void testNotifyStrategyInterestRegistration() {
        // 실제 이메일 알림을 보내는 요청
        InterestRequest interestRequest = new InterestRequest(ADD_TEST_EMAIL);

        // 실제 이메일 발송 요청
        Mono<String> monoResponse = emailService.notifyStrategyInterestRegistration(interestRequest);

        StepVerifier.create(monoResponse)
                .expectNext("ok")
                .expectComplete()
                .verify();

        System.out.println("monoResponse.subscribe() = " + monoResponse.subscribe());

        // 이메일 발송이 정상적으로 처리되었는지 확인
    }

    @Test
    void testAddSendDeleteTiming(){

        Mono<Void> monoResponse = emailService.sendAndSaveAuthCode(ADD_TEST_EMAIL);

        StepVerifier.create(monoResponse)
                .expectComplete()
                .verify();

        System.out.println("monoResponse.subscribe() = " + monoResponse.subscribe());

    }
}
