package com.be3c.sysmetic.domain.member.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenApiResponseDto {

    private Long memberId;
    private String roleCode;
    private String email;
    private String nickname;
    private String profileImage;

}
