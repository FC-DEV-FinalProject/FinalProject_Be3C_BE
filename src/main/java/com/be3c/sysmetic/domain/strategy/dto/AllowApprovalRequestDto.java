package com.be3c.sysmetic.domain.strategy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("strategyId")
    @Schema(description = "승인 ID 리스트", example = "[1, 2, 3]")
    private List<Long> strategyId;
}
