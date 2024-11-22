package com.be3c.sysmetic.domain.strategy.repository;


import com.be3c.sysmetic.domain.strategy.dto.StrategySearchRequestDto;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// QueryDSL로 작성된 메서드를 구현하기
public interface StrategyRepositoryCustom {

    // searchByConditions : 항목별 상세 조건으로 검색하기
    Page<Strategy> searchByConditions(Pageable pageable, StrategySearchRequestDto strategySearchRequestDto);

    // searchByAlgorithm : 알고리즘별 검색
    Page<Strategy> searchByAlgorithm(Pageable pageable, String type);
}
