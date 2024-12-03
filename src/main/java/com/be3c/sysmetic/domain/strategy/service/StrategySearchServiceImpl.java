package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.*;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.repository.StrategyRepository;
import com.be3c.sysmetic.domain.strategy.util.StockGetter;
import com.be3c.sysmetic.global.common.response.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class StrategySearchServiceImpl implements StrategySearchService {

    private final StrategyRepository strategyRepository;
    private final StockGetter stockGetter;
    private final int PAGE_SIZE = 10;

    /*
        searchConditions : 상세 조건 검색 결과로 나온 전략을 StrategySearchResponseDto로 변환해서 PageReposne에 담아 반환
    */
    @Override
    public PageResponse<StrategySearchResponseDto> searchConditions(
            Integer pageNum, StrategySearchRequestDto strategySearchRequestDto) {

        Pageable pageable = PageRequest.of(pageNum, PAGE_SIZE);

        Page<Strategy> sPage = strategyRepository.searchByConditions(pageable, strategySearchRequestDto);

        List<StrategySearchResponseDto> strategyList = sPage.getContent()
                .stream()
                .map(s -> StrategySearchResponseDto.builder()
                        .strategyId(s.getId())
                        .traderId(s.getTrader().getId())
                        .traderNickname(s.getTrader().getNickname())
                        .methodId(s.getMethod().getId())
                        .methodName(s.getMethod().getName())
                        .name(s.getName())
                        .cycle(s.getCycle())
                        .stockList(stockGetter.getStocks(s.getId()))
                        .accumulatedProfitLossRate(s.getAccumulatedProfitLossRate())
                        .mdd(s.getMdd())
                        .smScore(s.getSmScore())
                        .build()
                )
                .toList();

        return PageResponse.<StrategySearchResponseDto>builder()
                .currentPage(sPage.getNumber())
                .pageSize(sPage.getSize())
                .totalElement(sPage.getNumberOfElements())
                .totalPages(sPage.getTotalPages())
                .content(strategyList)
                .build();
    }


    /*
        searchAlgorithm : 알고리즘별 전략 검색
    */
    @Override
    public PageResponse<StrategyAlgorithmResponseDto> searchAlgorithm(Integer pageNum, StrategyAlgorithmOption algorithm) {

        Pageable pageable = PageRequest.of(pageNum, PAGE_SIZE);

        Page<Strategy> sPage = strategyRepository.searchByAlgorithm(pageable, String.valueOf(algorithm));

        List<StrategyAlgorithmResponseDto> strategyList = sPage.getContent()
                .stream()
                .map(s -> StrategyAlgorithmResponseDto.builder()
                        .algorithm(algorithm)
                        .id(s.getId())
                        .traderId(s.getTrader().getId())
                        .traderNickname(s.getTrader().getNickname())
                        .methodId(s.getMethod().getId())
                        .methodName(s.getMethod().getName())
                        .stockList(stockGetter.getStocks(s.getId()))
                        .name(s.getName())
                        .cycle(s.getCycle())
                        .accumulatedProfitLossRate(s.getAccumulatedProfitLossRate())
                        .mdd(s.getMdd())
                        .smScore(s.getSmScore())
                        .build()
                )
                .toList();

        return PageResponse.<StrategyAlgorithmResponseDto>builder()
                .currentPage(sPage.getNumber())
                .currentPage(sPage.getNumber())
                .pageSize(sPage.getSize())
                .totalElement(sPage.getTotalElements())
                .totalPages(sPage.getTotalPages())
                .content(strategyList)
                .build();
    }
}
