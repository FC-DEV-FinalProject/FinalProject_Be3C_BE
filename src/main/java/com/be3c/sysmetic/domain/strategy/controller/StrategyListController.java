package com.be3c.sysmetic.domain.strategy.controller;

import com.be3c.sysmetic.domain.strategy.dto.StrategyListByTraderDto;
import com.be3c.sysmetic.domain.strategy.dto.StrategyListDto;
import com.be3c.sysmetic.domain.strategy.dto.TraderListDto;
import com.be3c.sysmetic.domain.strategy.service.StrategyListService;
import com.be3c.sysmetic.global.common.response.ApiResponse;
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
public class StrategyListController {

    private final StrategyListService strategyListService;

    /*
        getStrategy : 전략 목록 페이지 조회 요청
        전략 10개를 수익률 기준으로 페이징
    */
    // 전략 목록은 전략명, 종목명, 트레이더 닉네임, 트레이더 프로필 이미지, 누적수익률, MDD, SM Score, 팔로우 수, 팔로우 버튼이 표시된다.
    // 로그인 하지 않은 회원이 팔로우 버튼을 클릭하면, 회원가입 / 로그인 페이지로 이동한다.
    @GetMapping("/strategy/list")           // 요청 경로 : http://localhost:8080/strategy/list?pageNum=0
    public ApiResponse<PageResponse<StrategyListDto>> getStrategies(
            @RequestParam(name = "pageNum", defaultValue = "0") Integer pageNum) throws Exception {
        PageResponse<StrategyListDto> strategyList = strategyListService.findStrategyPage(pageNum);

        if (strategyList.getContent().isEmpty())
            return ApiResponse.fail(ErrorCode.BAD_REQUEST, "요청하신 페이지가 없습니다.");

        // return ApiResponse.success(PageResponse.of(strategyList));
        return ApiResponse.success(strategyList);
    }


    /*
        searchByTrader : 트레이더 닉네임으로 검색, 팔로우 수 내림차순 정렬
    */
    @GetMapping("/strategy/trader")          // 요청 경로 : localhost:8080/strategy/trader?nickname=트레이더124
    public ApiResponse<PageResponse<TraderListDto>> searchByTraderNickname(
            @RequestParam("nickname") String nickname) throws Exception {
        PageResponse<TraderListDto> traderList = strategyListService.findTraderNickname(nickname);

        if (traderList.getContent().isEmpty())
            return ApiResponse.fail(ErrorCode.BAD_REQUEST, "해당 닉네임을 가진 트레이더가 없습니다.");

        return ApiResponse.success(traderList);
    }


    /*
        getStrategiesByTrader : 트레이더별 전략 목록
        searchByTraderNickname 트레이더 검색 -> 한 명 선택 -> getStrategiesByTrader 트레이더의 전략 목록 보여줌
    */
    @GetMapping("/strategy/choose")         // 요청 경로 : localhost:8080/strategy/choose?traderId=195
    public ApiResponse<PageResponse<StrategyListByTraderDto>> getStrategiesByTrader(
            @RequestParam("traderId") Long traderId, @RequestParam(name = "pageNum", defaultValue = "0") Integer pageNum) {
        PageResponse<StrategyListByTraderDto> strategyListByTrader = strategyListService.findStrategiesByTrader(traderId, pageNum);

        if (strategyListByTrader.getContent().isEmpty())
            return ApiResponse.fail(ErrorCode.NOT_FOUND, "해당 트레이더의 등록된 전략이 없습니다.");

        return ApiResponse.success(strategyListByTrader);
    }
}