package com.be3c.sysmetic.domain.strategy.controller;

import com.be3c.sysmetic.domain.strategy.dto.MainPageDto;
import com.be3c.sysmetic.domain.strategy.service.MainPageService;
import com.be3c.sysmetic.global.common.response.APIResponse;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1/main")
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class MainPageController implements MainPageControllerDocs {

    private final MainPageService mainPageService;

    /*
        getMainPage : 메인 페이지 요청
    */
    @GetMapping("/info")
    public APIResponse<MainPageDto> getMainPage() throws Exception {

        MainPageDto m = mainPageService.getMain();

        // if (m == null) {
        //     return APIResponse.fail(ErrorCode.BAD_REQUEST, "메인 페이지 데이터를 가져올 수 없습니다.");
        // }
        // if (m.getRankedTrader() == null) {
        //     return APIResponse.fail(ErrorCode.BAD_REQUEST, "트레이더 랭킹 정보가 없습니다.");
        // }
        // if (m.getTotalTraderCount() == null) {
        //     return APIResponse.fail(ErrorCode.BAD_REQUEST, "총 트레이더 수 정보가 없습니다.");
        // }
        // if (m.getTotalStrategyCount() == null) {
        //     return APIResponse.fail(ErrorCode.BAD_REQUEST, "총 전략 수 정보가 없습니다.");
        // }
        // if (m.getSmScoreTopFives() == null) {
        //     return APIResponse.fail(ErrorCode.BAD_REQUEST, "SM Score 랭킹 정보가 없습니다.");
        // }
        return APIResponse.success(m);
    }

    // TODO 대표전략 평균 지표 - 컨트롤러 메서드
}
