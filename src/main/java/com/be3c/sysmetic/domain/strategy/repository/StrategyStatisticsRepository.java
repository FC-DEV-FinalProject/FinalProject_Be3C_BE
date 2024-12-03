package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.strategy.dto.StrategyDetailStatistics;
import com.be3c.sysmetic.domain.strategy.entity.StrategyStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StrategyStatisticsRepository extends JpaRepository<StrategyStatistics, Long> {

    /* 주석 처리된 메서드 삭제 ======================== 12월 3일 */

    StrategyStatistics findByStrategyId(Long strategyId);

    @Modifying
    void deleteByStrategyId(Long strategyId);

    // 알고리즘별 전략 목록에서 사용!
    @Query("SELECT s.accumulatedProfitLossRate FROM StrategyStatistics s WHERE s.strategy.statusCode = :statusCode")
    List<Double> findAllAccumulatedProfitLossRates(@Param("statusCode") String statusCode);

    // 전략 상세 페이지에서 사용!
    @Query("SELECT new com.be3c.sysmetic.domain.strategy.dto.StrategyDetailStatistics(s.maximumCapitalReductionAmount, s.averageProfitLossRate, s.profitFactor, s.winningRate) "
        + "FROM StrategyStatistics s WHERE s.strategy.id = :strategyId")
    StrategyDetailStatistics findStrategyDetailStatistics(@Param("strategyId") Long strategyId);
}
