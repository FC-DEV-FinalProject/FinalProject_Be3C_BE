package com.be3c.sysmetic.global.util.email.apiclient;

import com.be3c.sysmetic.global.util.email.dto.*;
import reactor.core.publisher.Mono;

import java.util.List;

public interface EmailApiClient {

    // 1. 주소록 api 사용  ---------------------------------------------------------------------------------

    // 추가
    /**
     * 일반회원 주소록에 추가
     * @param subscriberRequest 추가할 구독자 정보
     * @return API로부터의 응답
     */
    StibeeApiResponse addUserSubscriberRequest(SubscriberRequest subscriberRequest);

    /**
     * 트레이더 주소록에 추가
     * @param subscriberRequest 추가할 구독자 정보
     * @return API로부터의 응답
     */
    StibeeApiResponse addTraderSubscriberRequest(SubscriberRequest subscriberRequest);

    /**
     * 임시 주소록에 추가
     * @param subscriberRequest 추가할 구독자 정보
     * @return API로부터의 응답
     */
    StibeeApiResponse addTempSubscriberRequest(SubscriberRequest subscriberRequest);


    // 삭제
    /**
     * 일반회원 주소록으로부터 삭제
     * @param emails 삭제할 이메일 리스트
     * @return API로부터의 응답을 받는 작업 완료 객체
     */

    Mono<StibeeSimpleResponse> deleteUserSubscriberRequest(List<String> emails);
    /**
     * 트레이더 주소록으로부터 삭제
     * @param emails 삭제할 이메일 리스트
     * @return API로부터의 응답을 받는 작업 완료 객체
     */
    Mono<StibeeSimpleResponse> deleteTraderSubscriberRequest(List<String> emails);

    /**
     * 임시 주소록으로부터 삭제
     * @param emails 삭제할 이메일 리스트
     * @return API로부터의 응답을 받는 작업 완료 객체
     */
    Mono<StibeeSimpleResponse> deleteTempSubscriberRequest(List<String> emails);



    // 2.자동 메일 api 사용 -------------------------------------------------------------------------------------


    /**
     * 이메일 인증 코드 발송
     * @param authCodeRequestDto 인증 코드 발송 요청 양식
     * @return API로부터의 응답을 받는 작업 완료 객체. 정상 응답 : ok
     */
    Mono<String> sendAuthEmailRequest(AuthCodeRequest authCodeRequestDto);

    /**
     * 문의 등록 알림 발송
     * @param inquiryRequestDto 문의 등록 알림 발송 요청 양식
     * @return API로부터의 응답을 받는 작업 완료 객체. 정상 응답 : ok
     */
    Mono<String> sendInquiryRegistrationEmailRequest(InquiryRequest inquiryRequestDto);

    /**
     * 관심전략 등록 알림 발송
     * @param interestRequest 관심전략 등록 알림 발송 요청 양식
     * @return API로부터의 응답을 받는 작업 완료 객체. 정상 응답 : ok
     */
    Mono<String> sendInterestRegistrationEmailRequest(InterestRequest interestRequest);
}
