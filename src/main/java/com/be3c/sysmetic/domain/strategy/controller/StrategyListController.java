package com.be3c.sysmetic.domain.strategy.controller;

import com.be3c.sysmetic.domain.strategy.dto.StrategyListDto;
import com.be3c.sysmetic.domain.strategy.dto.TraderListDto;
import com.be3c.sysmetic.domain.strategy.service.StrategyListService;
import com.be3c.sysmetic.global.common.response.ApiResponse;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
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
    @GetMapping("/strategy/list")
    public ResponseEntity<ApiResponse<Page<StrategyListDto>>> getStrategyList(
            @RequestParam(defaultValue = "0") Integer pageNum) throws Exception {
        Page<StrategyListDto> strategyList = strategyListService.findStrategyPage(pageNum);

        if (strategyList.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail(ErrorCode.BAD_REQUEST, "요청하신 페이지가 없습니다."));

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(strategyList));
    }

    /*
        searchByTrader : 트레이더 닉네임으로 검색
    */
    @GetMapping("/strategy/search/{nickname}")
    public ResponseEntity<ApiResponse<Page<TraderListDto>>> searchByTraderNickname(
            @PathVariable String nickname) throws Exception {
        Page<TraderListDto> traderList = strategyListService.findByTrader(nickname);

        if (traderList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail(ErrorCode.BAD_REQUEST, "해당 닉네임을 가진 트레이더가 없습니다."));
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(traderList));
    }

    /*
       getStrategyDetail : 전략 목록 -> 전략 상세 페이지
    */
    // @GetMapping("/strategy/detail/{strategyId}")
    // public ResponseEntity<ApiResponse<StrategyDetailDto>> getStrategyDetail(
    //         @RequestParam Long strategyId) throws Exception {
    //     StrategyDetailDto strategyDetailDto = strategyListService.getStrategyDetailById(strategyId);
    //
    //     if (ObjectUtils.isEmpty(strategyDetailDto)) {
    //         return ResponseEntity.status(HttpStatus.BAD_REQUEST)
    //                 .body(ApiResponse.fail(ErrorCode.BAD_REQUEST));
    //     }
    //
    //     return ResponseEntity.status(HttpStatus.OK)
    //             .body(ApiResponse.success(strategyDetailDto));
    // }
}