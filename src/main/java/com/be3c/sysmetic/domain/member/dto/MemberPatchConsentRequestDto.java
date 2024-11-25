package com.be3c.sysmetic.domain.member.dto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MemberPatchConsentRequestDto {
    private Boolean receiveInfoConsent;
    private Boolean receiveMarketingConsent;
}
