package com.be3c.sysmetic.global.util.email;

import com.be3c.sysmetic.SysmeticApplication;
import com.be3c.sysmetic.global.util.email.apiclient.EmailApiClient;
import com.be3c.sysmetic.global.util.email.apiclient.EmailApiClientImpl;
import com.be3c.sysmetic.global.util.email.dto.*;
import com.be3c.sysmetic.global.util.email.service.GmailSenderService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/* 메일 오는지 admin 계정 확인하기 */
@SpringBootTest(classes = SysmeticApplication.class)
public class EmailApiClientTest {

    @Autowired
    private EmailApiClient emailApiClient;

    @Autowired
    private GmailSenderService gmailSenderService;

    @Value("${spring.mail.username}")
    private String adminEmail;



    @Test
    void callDeleteRequestTest(){

        emailApiClient.addTempSubscriberRequest(SubscriberRequest.builder()
                .subscribers(List.of(Subscriber.builder()
                        .email(adminEmail).build())).build());
        emailApiClient.deleteTempSubscriberRequest(List.of(adminEmail));

    }


    @Test
    void testDeleteSubscriberRequest_SuccessfulApiCall() {
        // Mock WebClient to return a successful response
        WebClient webClientMock = mock(WebClient.class, RETURNS_DEEP_STUBS);
        Mockito.when(
                webClientMock
                        .method(any())
                        .uri(any(String.class))
                        .bodyValue(any())
                        .retrieve()
                        .bodyToMono((Class<Object>) any()))
                .thenReturn(
                        Mono.just(new StibeeSimpleResponse()));

        // Inject the mock
        emailApiClient = new EmailApiClientImpl(webClientMock, gmailSenderService);

        // Test
        List<String> emails = List.of("test1@example.com", "test2@example.com");

        Mono<StibeeSimpleResponse> stringMono = emailApiClient.deleteUserSubscriberRequest(emails);

        StepVerifier.create(stringMono)
                .expectNext(new StibeeSimpleResponse())
                .expectComplete()
                .verify();
    }


    @Test /* 메일 오는지 admin 계정 확인하기 */
    void testDeleteSubscriberRequest_ApiCallFails_SendMailMannually() {
        // Mock WebClient to simulate an error response
        WebClient webClientMock = mock(WebClient.class, RETURNS_DEEP_STUBS);
        Mockito.when(
                webClientMock
                        .method(any())
                        .uri(any(String.class))
                        .bodyValue(any())
                        .retrieve()
                        .bodyToMono((Class<Object>) any()))
                .thenReturn(
                        Mono.error(new WebClientResponseException(500, "Internal Server Error", null, null, null)));

        // Inject the mock
        emailApiClient = new EmailApiClientImpl(webClientMock, gmailSenderService);

        // StepVerifier to wait for the async result
        StepVerifier.create(emailApiClient.deleteTempSubscriberRequest(List.of(adminEmail)))
                .expectComplete()
                .verify();

        /* 메일 오는지 admin 계정 확인하기 */
    }


    @Test
    void addSendDeleteDelayTest(){

        StibeeApiResponse addResponse = emailApiClient.addTempSubscriberRequest(SubscriberRequest.builder()
                .subscribers(List.of(Subscriber.builder()
                        .email(adminEmail).build())).build());
        System.out.println("addResponse = " + addResponse);

        emailApiClient.sendAuthEmailRequest(new AuthCodeRequest(adminEmail, "auth11"));


    }

    @Test
    void testSendAuthEmailRequestSync(){
        Mono<String> sendResponse = emailApiClient.sendAuthEmailRequest(new AuthCodeRequest(adminEmail, "test12"));
        StepVerifier.create(sendResponse)
                .expectNext("ok")
                .expectComplete()
                .verify();
        Disposable disposable = sendResponse.subscribe(String::toString);
        System.out.println("sendResponse.subscribe() = " + disposable);
//        System.out.println("sendResponse = " + sendResponse);
    }

    @Test
    void testSendNofifyRequest(){

//        emailApiClient.addTraderSubscriberRequest(SubscriberRequest.builder()
//                        .eventOccurredBy(EventOccuredBy.MANUAL)
//                        .subscribers(List.of(Subscriber.builder()
//                                .email(adminEmail)
//                                .isAdConsent(true)
//                                .build()))
//                .build());


        Mono<String> sendResponse = emailApiClient.sendInterestRegistrationEmailRequest(new InterestRequest(adminEmail));
        StepVerifier.create(sendResponse)
                .expectNext("ok")
                .expectComplete()
                .verify();

        System.out.println("sendResponse.block().toString() = " + sendResponse.block().toString());

        sendResponse.subscribe(result -> {
            System.out.println("Email sent successfully: " + result);
        }, error -> {
            System.out.println("Error occurred: " + error.toString());
        });
    }


}
