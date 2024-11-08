package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StrategyListService {
    // findStrategyPage : 매개변수로 넘어온 page에 해당하는 Strategy를 모두 찾음
    Page<Strategy> findStrategyPage(Integer pageNum);
}