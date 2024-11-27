package com.be3c.sysmetic.global.util.email.dto;

import com.be3c.sysmetic.global.util.email.util.YesNoToBooleanDeserializer;
import com.be3c.sysmetic.global.util.email.util.BooleanToYesNoSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 스티비에 구독자를 추가하도록 요청하는 양식(SubscriberRequestDto)에 필요한 양식
 *
 * 필드명: 스티비 기준
 */
@Getter @Setter @ToString
@Builder
@NoArgsConstructor @AllArgsConstructor
public class Subscriber {

    private String email;  // 이메일 주소
    private String name;  // 이름
    private LocalDateTime subscribedDate;   // 구독일

    @JsonProperty("$ad_agreed")
    @JsonSerialize(using = BooleanToYesNoSerializer.class)
    @JsonDeserialize(using = YesNoToBooleanDeserializer.class)
    private Boolean isAdConsent;    // null일 시 true:Y
}