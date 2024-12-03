package com.be3c.sysmetic.domain.strategy.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class RejectStrategyApprovalDto {

    @NotNull
    private Long approvalId;

    @NotBlank
    @Max(50)
    private String rejectReason;
}
