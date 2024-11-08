package com.be3c.sysmetic.domain.strategy.controller;

import com.be3c.sysmetic.domain.strategy.dto.StrategyDetailResponseDto;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
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
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class StrategyListController {

    private final StrategyListService strategyListService;

    /*
        getStrategyPage : 전략 목록 페이지 조회 요청
        전략 10개를 수익률 기준으로 페이징
    */
    // 전략 목록은 전략명, 종목명, 트레이더 닉네임, 트레이더 프로필 이미지, 누적수익률, MDD, SM Score, 팔로우 수, 팔로우 버튼이 표시된다.
    // 로그인 하지 않은 회원이 팔로우 버튼을 클릭하면, 회원가입 / 로그인 페이지로 이동한다.
    @GetMapping("/strategy/list")
    // 페이징된 Strategy 목록을 포함하는 응답 형식을 가진 ApiResponse 객체 -> ResponseEntity로 보냄
    public ResponseEntity<ApiResponse<Page<Strategy>>> getStrategyPage(@RequestParam(defaultValue = "0") Integer pageNum) throws Exception {
        /* ExceptionHandler 추후 정의 */

        Page<Strategy> strategyList = strategyListService.findStrategyPage(pageNum);

        // page가 null인지 검증
        if (strategyList == null) {
            // ResponseEntity(HttpStatusCode)를 담은 ApiResonse 반환
            // 400 BAD_REQUEST 상태코드 지정
            // 응답으로 반환할 ApiResponse 객체 지정
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail(ErrorCode.BAD_REQUEST, "요청하신 페이지가 없습니다."));
        }
        // strategyList가 null 검증 통과
        // 응답 본문에 strategyList를 담은 ApiResponse 반환
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(strategyList));
    }

    /*
       전략 목록 -> 전략 상세 페이지
    */
    // @GetMapping("/strategy/list/{pageNum}/{id}")
    // public ResponseEntity<ApiResponse<StrategyDetailResponseDto>> getStrategyDetailPage(
    //         @RequestParam Integer pageNum, Integer id
    // ) throws Exception {
    //
    // }
}