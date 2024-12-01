package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.strategy.dto.StrategyDetailStatistics;
import com.be3c.sysmetic.domain.strategy.entity.StrategyStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import java.util.List;

@Repository
public interface StrategyStatisticsRepository extends JpaRepository<StrategyStatistics, Long> {
    // Optional로 변경? 변경 x?
    StrategyStatistics findByStrategyId(Long strategyId);
//    Optional<StrategyStatistics> findByStrategyId(Long strategyId);

    @Modifying
    void deleteByStrategyId(Long strategyId);

    // @Query("SELECT SUM(s.accumulatedProfitLossRate), s.accumulatedProfitLossRate FROM StrategyStatistics s WHERE s.strategy.statusCode = :statusCode")
    // List<Object[]> findSumAndIndividualProfitLossRate(@Param("statusCode") String statusCode);

    // 알고리즘별 전략 목록에서 사용!
    @Query("SELECT s.accumulatedProfitLossRate FROM StrategyStatistics s WHERE s.strategy.statusCode = :statusCode")
    List<Double> findAllAccumulatedProfitLossRates(@Param("statusCode") String statusCode);

    // @Query("SELECT COUNT(s) FROM StrategyStatistics s WHERE s.strategy.statusCode = :statusCode")
    // Integer countAllPublic(@Param("statusCode") String statusCode);

    // 전략 상세 페이지에서 사용!
    // TODO 초기값 넣으면 Optional 삭제하기
    @Query("SELECT new com.be3c.sysmetic.domain.strategy.dto.StrategyDetailStatistics(s.maximumCapitalReductionAmount, s.averageProfitLossRate, s.profitFactor, s.winningRate) "
        + "FROM StrategyStatistics s WHERE s.strategy.id = :strategyId")
    Optional<StrategyDetailStatistics> findStrategyDetailStatistics(@Param("strategyId") Long strategyId);
}
