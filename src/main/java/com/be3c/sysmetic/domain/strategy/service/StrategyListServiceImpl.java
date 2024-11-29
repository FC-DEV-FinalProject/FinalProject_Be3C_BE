package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.domain.strategy.dto.*;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.repository.StrategyListRepository;
import com.be3c.sysmetic.domain.strategy.repository.StrategyRepository;
import com.be3c.sysmetic.domain.strategy.util.DoubleHandler;
import com.be3c.sysmetic.domain.strategy.util.StockGetter;
import com.be3c.sysmetic.global.common.response.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;


@Service
@Transactional
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class StrategyListServiceImpl implements StrategyListService {

    private final StrategyListRepository strategyListRepository;
    private final MemberRepository memberRepository;
    private final StockGetter stockGetter;
    private final DoubleHandler doubleHandler;
    private final StrategyRepository strategyRepository;

    /*
        findStrategyPage : 메인 전략 목록 페이지 (수익률순 조회)
        Strategy 엔티티를 StrategyListDto로 반환
    */
    @Override
    public PageResponse<StrategyListDto> findStrategyPage(Integer pageNum) {

        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Order.desc("accumulatedProfitLossRate")));

        Page<StrategyListDto> strategies = strategyListRepository.findAllByStatusCode(String.valueOf(StrategyStatusCode.PUBLIC), pageable)
                .map(strategy -> new StrategyListDto(
                        strategy.getId(),
                        strategy.getTrader().getId(),
                        strategy.getTrader().getNickname(),
                        strategy.getMethod().getId(),
                        strategy.getMethod().getName(),
                        strategy.getName(),
                        strategy.getCycle(),
                        stockGetter.getStocks(strategy.getId()),
                        doubleHandler.cutDouble(strategy.getAccumulatedProfitLossRate()),
                        doubleHandler.cutDouble(strategy.getMdd()),
                        doubleHandler.cutDouble(strategy.getSmScore())
                ));

        return PageResponse.<StrategyListDto>builder()
                 .currentPage(strategies.getNumber())
                 .pageSize(strategies.getSize())
                 .totalElement(strategies.getTotalElements())
                 .totalPages(strategies.getTotalPages())
                 .content(strategies.getContent())
                 .build();
    }


    /*
        findByTraderNickname : 트레이더 닉네임으로 검색, 일치한 닉네임, 팔로우 수 정렬
    */
    @Override
    public PageResponse<TraderNicknameListDto> findTraderNickname(String nickname, Integer pageNum) {

        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNum, pageSize);            // 팔로우 수 내림차순 정렬

        Page<TraderNicknameListDto> traders = strategyListRepository.findDistinctByTraderNickname(nickname, pageable);

        return PageResponse.<TraderNicknameListDto>builder()
                .currentPage(traders.getNumber())
                .pageSize(traders.getSize())
                .totalElement(traders.getTotalElements())       // 검색 결과 수 대체 가능
                .totalPages(traders.getTotalPages())
                .content(traders.getContent())
                .build();
    }


    /*
        findStrategiesByTrader : 트레이더 별 전략 목록 - 전략 목록 내에서는 똑같이 수익률순 내림차순
    */
    @Override
    public PageResponse<StrategyListByTraderDto> findStrategiesByTrader(Long traderId, Integer pageNum) {

        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Order.desc("accumulatedProfitLossRate")));

        Member trader = memberRepository.findById(traderId)
                .orElseThrow(() -> new NoSuchElementException("해당 트레이더가 존재하지 않습니다."));

        Page<StrategyListByTraderDto> strategiesByTrader = strategyListRepository.findAllByTraderAndStatusCode(trader, String.valueOf(StrategyStatusCode.PUBLIC), pageable)
                .map(strategy -> new StrategyListByTraderDto(
                        strategy.getTrader().getId(),
                        strategy.getTrader().getNickname(),
                        strategy.getMethod().getId(),
                        strategy.getMethod().getName(),
                        stockGetter.getStocks(strategy.getId()),
                        strategy.getId(),
                        strategy.getName(),
                        strategy.getCycle(),
                        strategy.getFollowerCount(),
                        strategyRepository.countStrategyByOneTrader(strategy.getTrader().getId()),
                        doubleHandler.cutDouble(strategy.getAccumulatedProfitLossRate()),
                        doubleHandler.cutDouble(strategy.getMdd()),
                        doubleHandler.cutDouble(strategy.getSmScore())
                ));

        return PageResponse.<StrategyListByTraderDto>builder()
                .currentPage(strategiesByTrader.getNumber())
                .pageSize(strategiesByTrader.getSize())
                .totalElement(strategiesByTrader.getTotalElements())
                .totalPages(strategiesByTrader.getTotalPages())
                .content(strategiesByTrader.getContent())
                .build();
    }


    /*
        findStrategiesByName : 전략명으로 검색
    */
    @Override
    public PageResponse<StrategyListByNameDto> findStrategiesByName(String keyword, Integer pageNum) {

        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Order.desc("accumulatedProfitLossRate")));

        Page<Strategy> strategies = strategyListRepository.findAllByContainingName(keyword, pageable);

        // Strategy 엔티티를 DTO로 매핑
        Page<StrategyListByNameDto> resultPage = strategies.map(strategy ->
                new StrategyListByNameDto(
                        strategy.getId(),
                        strategy.getTrader().getId(),
                        strategy.getTrader().getNickname(),
                        strategy.getMethod().getId(),
                        strategy.getMethod().getName(),
                        strategy.getName(),
                        strategy.getCycle(),
                        stockGetter.getStocks(strategy.getId()),
                        strategy.getAccumulatedProfitLossRate(),
                        strategy.getMdd(),
                        strategy.getSmScore()
                )
        );

        return PageResponse.<StrategyListByNameDto>builder()
                .currentPage(resultPage.getNumber())
                .pageSize(resultPage.getSize())
                .totalElement(resultPage.getTotalElements())
                .totalPages(resultPage.getTotalPages())
                .content(resultPage.getContent())
                .build();
    }
}