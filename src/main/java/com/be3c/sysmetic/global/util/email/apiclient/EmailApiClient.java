package com.be3c.sysmetic.global.util.email.apiclient;

import com.be3c.sysmetic.global.util.email.dto.*;

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
     * @param emails
     * @return API로부터의 응답
     */

    StibeeSimpleResponse deleteUserSubscriberRequest(List<String> emails);
    /**
     * 트레이더 주소록으로부터 삭제
     * @param emails
     * @return API로부터의 응답
     */
    StibeeSimpleResponse deleteTraderSubscriberRequest(List<String> emails);

    /**
     * 임시 주소록으로부터 삭제
     * @param emails
     * @return API로부터의 응답
     */
    StibeeSimpleResponse deleteTempSubscriberRequest(List<String> emails);



    // 2.자동 메일 api 사용 -------------------------------------------------------------------------------------



    /**
     * 이메일 인증 코드 발송
     * @param authCodeRequestDto
     * @return 정상 : ok
     */
    String sendAuthEmailRequest(AuthCodeRequest authCodeRequestDto);

    /**
     * 문의 등록 알림 발송
     * @param inquiryRequestDto
     * @return 정상 : ok
     */
    String sendInquiryRegistrationEmailRequest(InquiryRequest inquiryRequestDto);

    /**
     * 관심전략 등록 알림 발송
     * @param interestRequest
     * @return 정상 : ok
     */
    String sendInterestRegistrationEmailRequest(InterestRequest interestRequest);
}
