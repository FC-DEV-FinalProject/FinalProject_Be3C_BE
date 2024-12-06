package com.be3c.sysmetic.domain.strategy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class StrategyPostResponseDto {
    @Schema(description = "등록된 전략 식별번호", example = "1")
    @NotNull
    private Long strategyId;
}
