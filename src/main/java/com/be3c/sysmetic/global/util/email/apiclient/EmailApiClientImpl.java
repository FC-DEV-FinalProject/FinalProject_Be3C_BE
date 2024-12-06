package com.be3c.sysmetic.global.util.email.apiclient;

import com.be3c.sysmetic.global.util.email.dto.*;
import com.be3c.sysmetic.global.util.email.exception.EmailSendingException;
import com.be3c.sysmetic.global.util.email.service.GmailSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

/* Stibee API에 요청을 보내는 client */
@Component
@RequiredArgsConstructor
@Slf4j
public class EmailApiClientImpl implements EmailApiClient {

    private final WebClient webClient;
    private final GmailSenderService gmailSenderService;

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
                    return Mono.error(new EmailSendingException("구독 추가/수정 API call failed"));
                })
                .bodyToMono(StibeeApiResponse.class);

        return apiResponseMono.block(); // authCode보내는 코드를 위해서는 동기적으로 기다려야 함.
    }


    // 삭제

    @Override
    public Mono<StibeeSimpleResponse> deleteUserSubscriberRequest(List<String> emails) {

        String uri = baseUrlAddressBook + userListId + "/subscribers";
        return deleteSubscriberRequest(emails, uri);
    }

    @Override
    public Mono<StibeeSimpleResponse> deleteTraderSubscriberRequest(List<String> emails) {

        String uri = baseUrlAddressBook + traderListId + "/subscribers";
        return deleteSubscriberRequest(emails, uri);
    }

    @Override
    public Mono<StibeeSimpleResponse> deleteTempSubscriberRequest(List<String> emails) {

        String uri = baseUrlAddressBook + tempListId + "/subscribers";
        return deleteSubscriberRequest(emails, uri);
    }

    private Mono<StibeeSimpleResponse> deleteSubscriberRequest(List<String> emails, String uri){

        return webClient
                .method(HttpMethod.DELETE)
                .uri(uri)
                .bodyValue(emails)
                .retrieve()
                .bodyToMono(StibeeSimpleResponse.class)
                .retry(5)
                .onErrorResume(e -> {
                    gmailSenderService.sendEmailToAdmin("[스티비] 구독자 정보 삭제 실패, 수동 삭제 요청 드립니다.",
                            "삭제 실패한 구독자를 수동으로 지워주세요: " + e.getMessage());
                    return Mono.empty();
                });
    }

    /*
     *
     * 주소록 API
     * ------------------------------------------------------------------------------------------
     * 자동 이메일 API
     *
     */

    @Override
    public Mono<String> sendAuthEmailRequest(AuthCodeRequest authCodeRequestDto) {
        String uri = baseUrlEmail + emailAuthEndPoint;

        return sendAutoEmailAsync(authCodeRequestDto, uri);
    }

    @Override
    public Mono<String> sendInquiryRegistrationEmailRequest(InquiryRequest inquiryRequestDto) {
        String uri = baseUrlEmail + inquiryRegistrationEndPoint;

        return sendAutoEmailAsync(inquiryRequestDto, uri);
    }

    @Override
    public Mono<String> sendInterestRegistrationEmailRequest(InterestRequest interestRequest) {
        String uri = baseUrlEmail + interestRegistrationEndPoint;

        return sendAutoEmailAsync(interestRequest, uri);
    }

    private String sendAutoEmailSync(EmailRequest emailRequest, String uri) {

        Mono<String> apiResponseMono = webClient.post()
                .uri(uri)
                .bodyValue(emailRequest)
                .retrieve()
                .onStatus(status-> status.isError(), response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> {
                                    System.out.println("오류 응답: " + body);
                                    return Mono.error(new RuntimeException("API 호출 실패: " + body));
                                })
                )
                .bodyToMono(String.class)  // Content type : 'text/plain;charset=utf-8' 성공 : "ok"
                ;

        return apiResponseMono.block(); // 동기적으로 기다려야 함
    }

    private Mono<String> sendAutoEmailAsync(EmailRequest emailRequest, String uri){

        Mono<String> apiResponseMono = webClient.post()
                .uri(uri)
                .bodyValue(emailRequest)
                .retrieve()
                .onStatus(status -> status.isError(), response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> {
                                    System.out.println("오류 응답: " + body);
                                    return Mono.error(new RuntimeException("API 호출 실패: " + body));
                                })
                )
                .bodyToMono(String.class)  // Content type : 'text/plain;charset=utf-8' 성공 : "ok"
                .retry(5)
                .onErrorResume(e -> {
                    gmailSenderService.sendEmailToAdmin("[스티비] 이메일 자동 전송에 실패했습니다. ",
                            "요청: " + emailRequest + "스티비 응답: "+ e.getMessage());
                    return Mono.just(e+e.getMessage()+e.getCause());
                });

        return apiResponseMono;
    }

}