package com.be3c.sysmetic.domain.strategy.controller;

import com.be3c.sysmetic.domain.strategy.dto.MainPageAnalysisDto;
import com.be3c.sysmetic.domain.strategy.dto.MainPageDto;
import com.be3c.sysmetic.domain.strategy.dto.StrategyAnalysisResponseDto;
import com.be3c.sysmetic.global.common.response.APIResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "메인 페이지 API", description = "메인 페이지 조회")
public interface MainPageControllerDocs {


    @Operation(
            summary = "메인 페이지 조회 API",
            description = "메인 페이지 정보 요청, 데이터 없으면 빈 리스트 반환 <br> " +
                        "팔로우 수 Top 3 트레이더, 총 트레이더 수, 총 전략 수, SM Score Top 5 전략",
            responses = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400")
            }
    )
    APIResponse<MainPageDto> getMainPage();


    @Operation(
            summary = "메인 페이지 대표전략 분석 지표 API",
            description = "메인 페이지 대표전략 분석 지표 요청, 디폴트 기간 ALL <br><br>" +
                "period - defaultValue ALL <br> " +
                "입력 가능한 옵션 - ALL, ",
            responses = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400")
            }
    )
    APIResponse<MainPageAnalysisDto> getMainGraph(
            @RequestParam(name = "period", defaultValue ="ALL") String period);
}
