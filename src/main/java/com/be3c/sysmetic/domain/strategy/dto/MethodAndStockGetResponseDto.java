package com.be3c.sysmetic.domain.strategy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MethodAndStockGetResponseDto {
    @Schema(description = "매매방식 목록 조회")
    @NotEmpty
    private List<MethodGetResponseDto> methodList;

    @Schema(description = "종목 목록 조회")
    @NotEmpty
    private List<StockGetResponseDto> stockList;
}
