package com.be3c.sysmetic.domain.strategy.controller;

import com.be3c.sysmetic.domain.strategy.dto.StrategyAnalysisOption;
import com.be3c.sysmetic.domain.strategy.dto.StrategyDetailDto;
import com.be3c.sysmetic.domain.strategy.service.StrategyDetailService;
import com.be3c.sysmetic.global.common.response.APIResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("v1/strategy/detail")
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class StrategyDetailController implements StrategyDetailControllerDocs {

    private final StrategyDetailService strategyDetailService;

    /*
        getDetailPage : 전략 상세 보기 페이지 요청
    */
    @Override
    @GetMapping
    public APIResponse<StrategyDetailDto> getDetailPage(
            @RequestParam("id") Long id,
            @RequestParam(value = "optionOne", defaultValue = "ACCUMULATED_PROFIT_LOSS_RATE") StrategyAnalysisOption optionOne,
            @RequestParam(value = "optionTwo", defaultValue = "PRINCIPAL") StrategyAnalysisOption optionTwo)  {

        StrategyDetailDto strategyDetailDto = strategyDetailService.getDetail(id, optionOne, optionTwo);

        return APIResponse.success(strategyDetailDto);
    }
}
