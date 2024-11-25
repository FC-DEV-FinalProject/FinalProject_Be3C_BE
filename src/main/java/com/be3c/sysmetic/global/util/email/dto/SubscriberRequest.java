package com.be3c.sysmetic.global.util.email.dto;

import lombok.*;

import java.util.List;

/**
 * 스티비에 구독자를 추가하도록 요청하는 양식
 *
 * eventOccurredBy 구독자 추가 방법 (기본값 "MANUAL" or "SUBSCRIBER")
 * groupIds 그룹아이디 (기본값 null or 해당 그룹에 구독자를 할당)
 * subscribers 구독자 정보
 *         Key: 사용자 정의 필드의 태그(email, name 등)
 *         Value: Key에 해당하는 값
 * isAdConsent 광고성 정보 수신 동의 여부 체크
 *         Key: AdConsentFactor.KEY("$ad_agreed")
 *         Value: AdConsentFactor.VALUE_TRUE("Y") or AdConsentFactor.VALUE_FALSE("N")
 */
@Builder @Getter @Setter
public class SubscriberRequest {

    @Builder.Default
    private EventOccuredBy eventOccurredBy = EventOccuredBy.SUBSCRIBER;

    @Builder.Default
    private List<String> groupIds = null;

    private List<Subscriber> subscribers;

}
