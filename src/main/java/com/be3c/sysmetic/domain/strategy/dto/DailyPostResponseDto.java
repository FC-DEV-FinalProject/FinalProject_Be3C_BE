package com.be3c.sysmetic.domain.strategy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DailyPostResponseDto {

    @Schema(description = "일간분석 중복여부", example = "false")
    @NotNull
    private boolean isDuplicate;

}
