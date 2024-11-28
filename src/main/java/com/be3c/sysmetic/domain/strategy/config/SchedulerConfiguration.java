package com.be3c.sysmetic.domain.strategy.config;

import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.repository.DailyRepository;
import com.be3c.sysmetic.domain.strategy.repository.StrategyRepository;
import com.be3c.sysmetic.domain.strategy.service.DailyServiceImpl;
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
    @Scheduled(cron = "0 0 0 * * ?")
    public void run() {
        // 미사용 상태가 아닌 전략 조회
        List<Strategy> strategies = strategyRepository.findAllUsingState();
        for (Strategy strategy : strategies) {
            try {
                strategyStatisticsService.runStrategyStatistics(strategy.getId());
            } catch (Exception e) {
                log.error("Error processing strategy ID: " + strategy.getId(), e);
            }
        }
    }
}
