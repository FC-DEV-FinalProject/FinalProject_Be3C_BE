package com.be3c.sysmetic.domain.strategy.controller;

import com.be3c.sysmetic.domain.strategy.dto.StrategyDetailDto;
import com.be3c.sysmetic.domain.strategy.service.StrategyDetailService;
import com.be3c.sysmetic.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class StrategyDetailController {

    private final StrategyDetailService strategyDetailService;

    /*
        getDetailPage : 전략 상세 보기 페이지 요청
    */
    @GetMapping("/strategy/detail")
    public ApiResponse<StrategyDetailDto> getDetailPage(
            @RequestParam("id") Long id) {

        StrategyDetailDto s = strategyDetailService.getDetail(id);

        return ApiResponse.success(s);
    }
}
