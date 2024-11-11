package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.StrategyDetailDto;
import com.be3c.sysmetic.domain.strategy.dto.StrategyListDto;
import com.be3c.sysmetic.domain.strategy.dto.TraderListDto;
import org.springframework.data.domain.Page;

public interface StrategyListService {

    int getTotalPageNumber(String statusCode, int pageSize);

    // findStrategyPage : 매개변수로 넘어온 page에 해당하는 Strategy를 모두 찾음
    Page<StrategyListDto> findStrategyPage(Integer pageNum);

    // StrategyDetailDto getStrategyDetailById(Long strategyId);

    // findByTraderNickname :
    Page<TraderListDto> findByTrader(String nickname);
}