package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.strategy.entity.StrategyStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StrategyStatisticsRepository extends JpaRepository<StrategyStatistics, Long> {
    StrategyStatistics findByStrategyId(Long strategyId);
    void deleteByStrategyId(Long strategyId);
}
