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

    // 매일 자정에 계산 - cron 초, 분, 시
    @Scheduled(cron = "0 00 00 * * ?")
    public void run() {

        // todo : 비공개 상태에도 트레이더는 자신의 전략 통계 확인 가능. 즉 비공개 상태인 전략도 계산 필요. -> 리팩토링시 진행
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
