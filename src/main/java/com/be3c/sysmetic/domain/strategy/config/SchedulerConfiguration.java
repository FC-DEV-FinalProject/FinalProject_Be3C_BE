package com.be3c.sysmetic.domain.strategy.config;

import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.repository.StrategyRepository;
import com.be3c.sysmetic.domain.strategy.service.StrategyStatisticsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class SchedulerConfiguration {
    private final StrategyRepository strategyRepository;
    private final StrategyStatisticsServiceImpl strategyStatisticsService;

    // 매일 자정에 계산 초, 분, 시
    @Scheduled(cron = "0 00 00 * * ?")
    public void run() {

        // 공개 상태인 전략에 한해 전략 통계 계산 -> todo 비공개 상태일 때도 트레이더는 전략 통계 확인 가능한데, 공개 상태만 계산하는 게 맞는가. 리팩토링시 고민
        List<Strategy> publicStrategies = strategyRepository.findAllByPublicStatus();
        for (Strategy strategy : publicStrategies) {
            try {
                strategyStatisticsService.runStrategyStatistics(strategy.getId());
            } catch (Exception e) {
                log.error("Error processing strategy ID: " + strategy.getId(), e);
            }
        }
    }
}
