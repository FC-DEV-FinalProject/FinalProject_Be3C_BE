package com.be3c.sysmetic.domain.strategy.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class RejectStrategyApprovalDto {

    @NotBlank
    private Long approvalId;

    @NotBlank
    private String rejectReason;
}
