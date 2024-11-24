package com.be3c.sysmetic.domain.strategy.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class StrategyListController implements StrategyListControllerDocs {

    private final StrategyListService strategyListService;


    /*
        getStrategies : 전략 목록 페이지 조회 요청
        요청 경로 : http://localhost:8080/strategy/list?pageNum=0
    */
    @Override
    @GetMapping("/strategy/list")
    public APIResponse<PageResponse<StrategyListDto>> getStrategies(
            @RequestParam(name = "pageNum", defaultValue = "0") Integer pageNum){
        PageResponse<StrategyListDto> strategyList = strategyListService.findStrategyPage(pageNum);

        log.info("controller ={}", strategyList);

        if (strategyList.getContent().isEmpty())
            return APIResponse.fail(ErrorCode.BAD_REQUEST, "요청하신 페이지가 없습니다.");

        // return ApiResponse.success(PageResponse.of(strategyList));
        return APIResponse.success(strategyList);
    }


    /*
        searchByTrader : 트레이더 닉네임으로 검색, 팔로우 수 내림차순 정렬
        요청 경로 : localhost:8080/strategy/trader?nickname=트레이더1  -> 트레이더119가 21개 전략을 가져서 첫 번째여야 함
    */
    @Override
    @GetMapping("/strategy/trader")
    public APIResponse<PageResponse<TraderNicknameListDto>> searchByTraderNickname(
            @RequestParam("nickname") String nickname,
            @RequestParam(name = "pageNum", defaultValue = "0") Integer pageNum) {

        log.info("Searching for nickname in Controller: {}", nickname); // 로그 추가

        PageResponse<TraderNicknameListDto> traderList = strategyListService.findTraderNickname(nickname, pageNum);

        if (traderList.getContent().isEmpty())
            return APIResponse.fail(ErrorCode.BAD_REQUEST, "해당 닉네임을 가진 트레이더가 없습니다.");

        return APIResponse.success(traderList);
    }


    /*
        getStrategiesByTraderId : 트레이더별 전략 목록
        searchByTraderNickname 트레이더 검색 -> 한 명 선택 -> getStrategiesByTrader 트레이더의 전략 목록 보여줌
        요청 경로 : localhost:8080/strategy/choose-trader?traderId=195
    */
    @Override
    @GetMapping("/strategy/choose-trader")
    public APIResponse<PageResponse<StrategyListByTraderDto>> getStrategiesByTraderId(
            @RequestParam("traderId") Long traderId,
            @RequestParam(name = "pageNum", defaultValue = "0") Integer pageNum) {
        PageResponse<StrategyListByTraderDto> strategyListByTrader = strategyListService.findStrategiesByTrader(traderId, pageNum);

        if (strategyListByTrader.getContent().isEmpty())
            return APIResponse.fail(ErrorCode.NOT_FOUND, "해당 트레이더의 등록된 전략이 없습니다.");

        return APIResponse.success(strategyListByTrader);
    }
}