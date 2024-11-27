package com.be3c.sysmetic.domain.strategy.controller;

import com.be3c.sysmetic.domain.strategy.dto.StrategyAnalysisOption;
import com.be3c.sysmetic.domain.strategy.dto.StrategyDetailDto;
import com.be3c.sysmetic.global.common.response.APIResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    APIResponse<StrategyDetailDto> getDetailPage(
                    @RequestParam("id") Long id,
                    @RequestParam(value = "optionOne", defaultValue = "ACCUMULATED_PROFIT_LOSS_RATE") StrategyAnalysisOption optionOne,
                    @RequestParam(value = "optionTwo", defaultValue = "PRINCIPAL") StrategyAnalysisOption optionTwo);


    // TODO 프런트와 상의해서 분석지표 엔드 포인트 나누기
    // @Operation(
    //         summary = "전략 상세 페이지 - 분석 지표",
    //         description = "전략 분석 지표",
    //         responses= {
    //                 @ApiResponse(responseCode = "200"),
    //                 @ApiResponse(responseCode = "401")
    //         }
    // )
    // APIResponse<StrategyAnalysisResponseDto> getAnalysis(@RequestParam("optionFirst") String optionFirst, @RequestParam("optionSecond") String optionSecond)

}
