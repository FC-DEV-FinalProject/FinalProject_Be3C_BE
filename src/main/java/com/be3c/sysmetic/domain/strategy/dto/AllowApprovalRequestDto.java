package com.be3c.sysmetic.domain.strategy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "승인 요청 DTO")
public class AllowApprovalRequestDto {

    @Schema(description = "승인 ID 리스트", example = "[1, 2, 3]")
    private List<Long> approvalId;

    @Schema(description = "승인 코드", example = "APPROVED")
    private String approvalCode;
}
