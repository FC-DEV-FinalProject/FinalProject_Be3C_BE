package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.StrategyListByTraderDto;
import com.be3c.sysmetic.domain.strategy.dto.StrategyListDto;
import com.be3c.sysmetic.domain.strategy.dto.TraderListDto;
import org.springframework.data.domain.Page;

public interface StrategyListService {

    int getTotalPageNumber(String statusCode, int pageSize);

    // findStrategyPage : 매개변수로 넘어온 page에 해당하는 Strategy를 모두 찾음
    Page<StrategyListDto> findStrategyPage(Integer pageNum);

    // findTraderNickname : 트레이더 닉네임으로 조회
    Page<TraderListDto> findTraderNickname(String nickname);

    // findStrategyPageByTrader : 트리이더 전략 목록
    Page<StrategyListByTraderDto> findStrategiesByTrader(Long traderId, Integer pageNum);
}