package com.be3c.sysmetic.domain.member.dto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MemberPatchInfoRequestDto {
    private Long userId;
    private String phone_number;
    private String nickname;
    private Boolean nicknameDuplCheck;

    private String receiveInfoConsent;
    private String receiveMarketingConsent;
}
