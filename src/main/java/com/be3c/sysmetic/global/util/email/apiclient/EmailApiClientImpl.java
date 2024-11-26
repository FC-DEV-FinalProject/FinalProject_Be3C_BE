package com.be3c.sysmetic.global.util.email.apiclient;

import com.be3c.sysmetic.global.util.email.dto.*;
import com.be3c.sysmetic.global.util.email.exception.EmailSendingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

/* Stibee API에 요청을 보내는 client */
@Component
@RequiredArgsConstructor
public class EmailApiClientImpl implements EmailApiClient {

    private final WebClient webClient;

    @Value("${stibee.base.url.address.book}")
    private String baseUrlAddressBook;

    @Value("${stibee.base.url.email}")
    private String baseUrlEmail;


    @Value("${stibee.address.book.list.id.user}")
    private String userListId;

    @Value("${stibee.address.book.list.id.trader}")
    private String traderListId;

    @Value("${stibee.address.book.list.id.temp}")
    private String tempListId;


    @Value("${stibee.email.auth.endpoint}")
    private String emailAuthEndPoint;

    @Value("${stibee.inquiry.registration.endpoint}")
    private String inquiryRegistrationEndPoint;

    @Value("${stibee.interest.registration.endpoint}")
    private String interestRegistrationEndPoint;


    @Override
    public StibeeApiResponse addUserSubscriberRequest(SubscriberRequest subscriberRequest) {
        String uri = baseUrlAddressBook + userListId + "/subscribers";
        return addSubscriberRequest(subscriberRequest, uri);
    }

    @Override
    public StibeeApiResponse addTraderSubscriberRequest(SubscriberRequest subscriberRequest) {
        String uri = baseUrlAddressBook + traderListId + "/subscribers";
        return addSubscriberRequest(subscriberRequest, uri);
    }

    @Override
    public StibeeApiResponse addTempSubscriberRequest(SubscriberRequest subscriberRequest) {
        String uri = baseUrlAddressBook + tempListId + "/subscribers";
        return addSubscriberRequest(subscriberRequest, uri);
    }


    /**
     * 가입한 트레이더 회원을 subscriber로 등록
     *
     * @param subscriberRequest subscriber 정보
     * @param uri               요청 target
     * @return 응답body
     */
    private StibeeApiResponse addSubscriberRequest(SubscriberRequest subscriberRequest, String uri) {

        Mono<StibeeApiResponse> apiResponseMono = webClient.post()
                .uri(uri)
                .bodyValue(subscriberRequest)
                .retrieve()
                .onStatus(status -> status.isError(), response -> {
                    System.out.println("Error response: " + response.statusCode());
                    return Mono.error(new EmailSendingException("구독 요청 API call failed"));
                })
                .bodyToMono(StibeeApiResponse.class);

        return apiResponseMono.block();
    }


    // 삭제


    @Override
    public StibeeApiResponse deleteUserSubscriberRequest(List<String> emails) {

        String uri = baseUrlAddressBook + userListId + "/subscribers";

        return deleteSubscriberRequest(emails, uri);
    }

    @Override
    public StibeeApiResponse deleteTraderSubscriberRequest(List<String> emails) {

        String uri = baseUrlAddressBook + traderListId + "/subscribers";

        return deleteSubscriberRequest(emails, uri);
    }

    @Override
    public StibeeApiResponse deleteTempSubscriberRequest(List<String> emails) {

        String uri = baseUrlAddressBook + tempListId + "/subscribers";

        return deleteSubscriberRequest(emails, uri);
    }

    private StibeeApiResponse deleteSubscriberRequest(List<String> emails, String uri){

        Mono<StibeeApiResponse> apiResponseMono = webClient
                .method(HttpMethod.DELETE)
                .uri(uri)
                .bodyValue(emails)
                .retrieve()
//                    .toBodilessEntity() // 응답 본문을 받지 않으면, Mono<Void>로 변환
                .onStatus(status -> status.isError(), response -> {
                    System.out.println("Error response: " + response.statusCode());
                    return Mono.error(new RuntimeException("API call failed"));
                })
                .bodyToMono(StibeeApiResponse.class);

        StibeeApiResponse apiResponse = apiResponseMono.block();
        System.out.println("apiResponse = " + apiResponse);

        return apiResponseMono.block();
    }

    /*
     *
     * 주소록 API
     * ------------------------------------------------------------------------------------------
     * 자동 이메일 API
     *
     */


    @Override
    public String sendAuthEmailRequest(AuthCodeRequest authCodeRequestDto) {
        String uri = baseUrlEmail + emailAuthEndPoint;

        return sendAutoEmail(authCodeRequestDto, uri);
    }

    @Override
    public String sendInquiryRegistrationEmailRequest(InquiryRequest inquiryRequestDto) {
        String uri = baseUrlEmail + inquiryRegistrationEndPoint;

        return sendAutoEmail(inquiryRequestDto, uri);
    }

    @Override
    public String sendInterestRegistrationEmailRequest(InterestRequest interestRequest) {
        String uri = baseUrlEmail + inquiryRegistrationEndPoint;

        return sendAutoEmail(interestRequest, uri);
    }

    private String sendAutoEmail(EmailRequest emailRequest, String uri) {

        Mono<String> apiResponseMono = webClient.post()
                .uri(uri)
                .bodyValue(emailRequest)
                .retrieve()
                .onStatus(status -> status.isError(), response -> {
                    System.out.println("Error response: " + response.statusCode());
                    return Mono.error(new RuntimeException("API call failed"));
                })
                .bodyToMono(String.class)  // Content type : 'text/plain;charset=utf-8' 성공 : "ok"
                ;

        return apiResponseMono.block(); // 동기적으로 기다림
    }

}