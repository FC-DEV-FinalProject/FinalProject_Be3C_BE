package com.be3c.sysmetic.domain.strategy.controller;

import com.be3c.sysmetic.domain.strategy.dto.StrategyAnalysisResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.StrategyDetailDto;
import com.be3c.sysmetic.global.common.response.APIResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "전략 상세 페이지 API", description = "전략 상세 페이지 조회")
public interface StrategyDetailControllerDocs {


    @Operation(
            summary = "전략 상세 페이지 조회 - 테스트 필요",
            description = "전략 상세 페이지",
            responses = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "401")
            }
    )
    APIResponse<StrategyDetailDto> getDetailPage(@PathVariable("id") Long id);


    @Operation(
            summary = "전략 상세 페이지 - 분석 지표",
            description = "전략 분석 지표 <br><br> " +
                    "id - 전략 id <br> ",
            responses= {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "401")
            }
    )
    APIResponse<StrategyAnalysisResponseDto> getAnalysis(
            @PathVariable("id") Long id);
}
