package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.strategy.dto.StrategyDetailStatistics;
import com.be3c.sysmetic.domain.strategy.entity.StrategyStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StrategyStatisticsRepository extends JpaRepository<StrategyStatistics, Long> {
    StrategyStatistics findByStrategyId(Long strategyId);
    void deleteByStrategyId(Long strategyId);

    // 전략 상세 페이지에서 사용!
    @Query("SELECT new com.be3c.sysmetic.domain.strategy.dto.StrategyDetailStatistics(s.maximumCapitalReductionAmount, s.averageProfitLossRate, s.profitFactor, s.winningRate) "
        + "FROM StrategyStatistics s WHERE s.strategy.id = :strategyId")
    StrategyDetailStatistics findStrategyDetailStatistics(@Param("strategyId") Long strategyId);
}
