package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.strategy.entity.StrategyStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StrategyStatisticsRepository extends JpaRepository<StrategyStatistics, Long> {
    StrategyStatistics findByStrategyId(Long strategyId);
    void deleteByStrategyId(Long strategyId);


    // @Query("SELECT SUM(s.accumulatedProfitLossRate), s.accumulatedProfitLossRate FROM StrategyStatistics s WHERE s.strategy.statusCode = :statusCode")
    // List<Object[]> findSumAndIndividualProfitLossRate(@Param("statusCode") String statusCode);

    // 알고리즘별 전략 목록에서 사용!
    @Query("SELECT s.accumulatedProfitLossRate FROM StrategyStatistics s WHERE s.strategy.statusCode = :statusCode")
    List<Double> findAllAccumulatedProfitLossRates(@Param("statusCode") String statusCode);

    // @Query("SELECT COUNT(s) FROM StrategyStatistics s WHERE s.strategy.statusCode = :statusCode")
    // Integer countAllPublic(@Param("statusCode") String statusCode);
}
