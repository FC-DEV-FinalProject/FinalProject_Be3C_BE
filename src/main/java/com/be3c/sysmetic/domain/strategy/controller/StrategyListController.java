package com.be3c.sysmetic.domain.strategy.controller;

import com.be3c.sysmetic.domain.strategy.dto.StrategyListByNameDto;
import com.be3c.sysmetic.domain.strategy.dto.StrategyListByTraderDto;
import com.be3c.sysmetic.domain.strategy.dto.StrategyListDto;
import com.be3c.sysmetic.domain.strategy.dto.TraderNicknameListDto;
import com.be3c.sysmetic.domain.strategy.service.StrategyListService;
import com.be3c.sysmetic.global.common.response.APIResponse;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import com.be3c.sysmetic.global.common.response.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/v1/strategy/list")
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class StrategyListController implements StrategyListControllerDocs {

    private final StrategyListService strategyListService;


    /*
        getStrategies : 전략 목록 페이지 조회 요청
        요청 경로 : localhost:8080/v1/strategy/list?pageNum=0
    */
    @Override
    @GetMapping()
    public APIResponse<PageResponse<StrategyListDto>> getStrategies(
            @RequestParam(name = "pageNum", defaultValue = "0") Integer pageNum){
        PageResponse<StrategyListDto> strategyList = strategyListService.findStrategyPage(pageNum);

        if (strategyList.getContent().isEmpty())
            return APIResponse.fail(ErrorCode.BAD_REQUEST, "요청하신 페이지가 없습니다.");

        return APIResponse.success(strategyList);
    }


    /*
        searchByTrader : 트레이더 닉네임으로 검색, 팔로우 수 내림차순 정렬
        요청 경로 : localhost:8080/v1/strategy/list/trader?nickname=트레이더&pageNum=0
    */
    @Override
    @GetMapping("/trader")
    public APIResponse<PageResponse<TraderNicknameListDto>> searchByTraderNickname(
            @RequestParam("nickname") String nickname,
            @RequestParam(name = "pageNum", defaultValue = "0") Integer pageNum) {

        PageResponse<TraderNicknameListDto> traderList = strategyListService.findTraderNickname(nickname, pageNum);

        if (traderList.getContent().isEmpty())
            return APIResponse.fail(ErrorCode.BAD_REQUEST, "해당 닉네임을 가진 트레이더가 없습니다.");

        return APIResponse.success(traderList);
    }


    /*
        getStrategiesByTraderId : 트레이더별 전략 목록
        요청 경로 : localhost:8080/v1/strategy/list/pick?traderId=1&pageNum=0
    */
    @Override
    @GetMapping("/pick")
    public APIResponse<PageResponse<StrategyListByTraderDto>> getStrategiesByTraderId(
            @RequestParam("traderId") Long traderId,
            @RequestParam(name = "pageNum", defaultValue = "0") Integer pageNum) {
        PageResponse<StrategyListByTraderDto> strategyListByTrader = strategyListService.findStrategiesByTrader(traderId, pageNum);

        if (strategyListByTrader.getContent().isEmpty())
            return APIResponse.fail(ErrorCode.NOT_FOUND, "해당 트레이더가 등록한 전략이 없습니다.");

        return APIResponse.success(strategyListByTrader);
    }


    /*
        getStrategiesByName : 전략명으로 검색
    */
    @Override
    @GetMapping("/name")
    public APIResponse<PageResponse<StrategyListByNameDto>> getStrategiesByName(
            @RequestParam("keyword") String keyword,
            @RequestParam(name = "pageNum", defaultValue = "0") Integer pageNum) {

        PageResponse<StrategyListByNameDto> strategyListByName = strategyListService.findStrategiesByName(keyword, pageNum);




        return APIResponse.success();
    }
}