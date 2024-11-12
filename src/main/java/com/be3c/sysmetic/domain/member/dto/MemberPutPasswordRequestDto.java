package com.be3c.sysmetic.domain.member.dto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MemberPutPasswordRequestDto {
    private Long userId;
    private String currentPassword;
    private String newPassword;
    private String newPasswordConfirm;
}
