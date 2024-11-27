package com.be3c.sysmetic.global.util.email.service;

import com.be3c.sysmetic.global.util.email.dto.*;

import java.util.List;

/**
 * Stibee 솔루션을 이용하여 이메일 발송 요청을 보냅니다
 */
public interface EmailService {

    // 1. 주소록 api 사용  ---------------------------------------------------------------------------------

    // 추가
    /**
     * 일반 회원 구독자 추가
     * @param subscriberRequest 스티비 구독 api 요청 양식
     */
    StibeeApiResponse addUserSubscriberRequest(SubscriberRequest subscriberRequest);

    /**
     * 트레이더 회원 구독자 추가
     * @param subscriberRequest 스티비 구독 api 요청 양식
     */
    StibeeApiResponse addTraderSubscriberRequest(SubscriberRequest subscriberRequest);


    // 수정
    /**
     * 일반회원 주소록 정보 수정
     * @param subscriberRequest 스티비 구독 api 요청 양식
     */
    StibeeApiResponse updateUserSubscriberRequest(SubscriberRequest subscriberRequest);

    /**
     * 트레이더 주소록 정보 수정
     * @param subscriberRequest 스티비 구독 api 요청 양식
     */
    StibeeApiResponse updateTraderSubscriberRequest(SubscriberRequest subscriberRequest);


    // 삭제
    /**
     * 일반회원 주소록에서 삭제
     * @param emails 삭제할 회원의 이메일 주소를 리스트 형태로
     */
    StibeeSimpleResponse deleteUserSubscriberRequest(List<String> emails);

    /**
     * 트레이더 주소록에서 삭제
     * @param emails 삭제할 회원의 이메일 주소를 리스트 형태로
     */
    StibeeSimpleResponse deleteTraderSubscriberRequest(List<String> emails);

    // 2.자동 메일 api 사용 -------------------------------------------------------------------------------------

    /**
     * email 인증 코드를 메일로 보내고 레디스에 저장
     * @param toEmail 인증할 메일 주소
     */
    void sendAndSaveAuthCode(String toEmail);

    /**
     * 문의가 등록을 이메일로 알림
     * @param inquiryRequest
     */
    void notifyStrategyInquiryRegistration(InquiryRequest inquiryRequest);

    /**
     * 관심전략이 등록되면 전략 등록자에게 이메일로 알림
     * @param interestRequest
     */
    void notifyStrategyInterestRegistration(InterestRequest interestRequest);
}
