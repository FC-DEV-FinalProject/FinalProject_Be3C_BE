package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.StrategyDetailDto;
import com.be3c.sysmetic.domain.strategy.repository.StrategyDetailRepository;
import com.be3c.sysmetic.domain.strategy.util.DoubleHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class StrategyDetailServiceImpl implements StrategyDetailService {

    private final StrategyDetailRepository strategyDetailRepository;
    private final DoubleHandler doubleHandler;

    @Override
    public StrategyDetailDto getDetail(Long id) {

        String statusCode = "ST001";        // 공개중인 전략

        return strategyDetailRepository.findByIdAndStatusCode(id, statusCode)
                .map(strategy -> StrategyDetailDto.builder()
                        .id(strategy.getId())
                        .traderId(strategy.getTrader().getId())
                        .methodId(strategy.getMethod().getId())
                        .stock("STOCK LIST")        // TODO 종목 매핑
                        .name(strategy.getName())
                        .nickname(strategy.getTrader().getNickname())
                        .methodName(strategy.getMethod().getName())
                        .statusCode(strategy.getStatusCode())
                        .cycle(strategy.getCycle())
                        .content(strategy.getContent())
                        .followerCount(strategy.getFollowerCount())
                        // TODO 통계 이용해서 계산 구현
                        .mdd(doubleHandler.cutDouble(strategy.getMdd()))
                        .kpRatio(doubleHandler.cutDouble(strategy.getKpRatio()))
                        .smScore(doubleHandler.cutDouble(strategy.getSmScore()))
                        .accumProfitLossRate(doubleHandler.cutDouble(Math.random() * 100))
                        .maximumCapitalReductionAmount(10.0)
                        .averageProfitLossRate(10.0)
                        .profitFactor(10.0)
                        .winningRate(10.0)
                        .build())
                .orElseThrow(() -> new NoSuchElementException("해당 전략의 상세 보기 페이지가 존재하지 않습니다."));
    }
}