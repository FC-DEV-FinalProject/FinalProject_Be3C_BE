package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.StrategyListByNameDto;
import com.be3c.sysmetic.domain.strategy.dto.StrategyListByTraderDto;
import com.be3c.sysmetic.domain.strategy.dto.StrategyListDto;
import com.be3c.sysmetic.domain.strategy.dto.TraderNicknameListDto;
import com.be3c.sysmetic.global.common.response.PageResponse;

public interface StrategyListService {

    // findStrategyPage : 매개변수로 넘어온 page에 해당하는 Strategy를 모두 찾음;
    PageResponse<StrategyListDto> findStrategyPage(Integer pageNum);

    // findTraderNickname : 트레이더 닉네임으로 조회
    PageResponse<TraderNicknameListDto> findTraderNickname(String nickname, Integer pageNum);

    // findStrategyPageByTrader : 트리이더 전략 목록
    StrategyListByTraderDto findStrategiesByTrader(Long traderId, Integer pageNum);

    //findStrategiesByName : 키워드로 전략명 검색
    PageResponse<StrategyListDto> findStrategiesByName(String keyword, Integer pageNum);
}