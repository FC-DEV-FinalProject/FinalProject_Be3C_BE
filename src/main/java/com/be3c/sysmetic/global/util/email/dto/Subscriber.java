package com.be3c.sysmetic.global.util.email.dto;

import com.be3c.sysmetic.global.util.email.util.YesNoToBooleanDeserializer;
import com.be3c.sysmetic.global.util.email.util.BooleanToYesNoSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @Builder
@ToString
public class Subscriber {
    /* 필드명은 스티비 기준 */
    private String email;  // 이메일 주소
    private String name;  // 이름
    private LocalDateTime subscribedDate;   // 구독일

    @JsonProperty("$ad_agreed")
    @JsonSerialize(using = BooleanToYesNoSerializer.class)
    @JsonDeserialize(using = YesNoToBooleanDeserializer.class)
    private Boolean isAdConsent;
}