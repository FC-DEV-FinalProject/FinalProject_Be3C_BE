package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.strategy.entity.StrategyGraphAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StrategyGraphAnalysisRepository extends JpaRepository<StrategyGraphAnalysis, Long> {

    void deleteByDailyId(Long dailyId);

    @Query("SELECT s FROM StrategyGraphAnalysis s WHERE s.strategy.id = :strategyId AND s.date > :date")
    List<StrategyGraphAnalysis> findByIdAndAfterDate(@Param("strategyId") Long strategyId, @Param("date") LocalDate date);

    Double findTop1MaximumCapitalReductionAmountByStrategyId(Long strategyId);

    @Query("SELECT COUNT(s) FROM StrategyGraphAnalysis s WHERE s.strategy.id = :strategyId AND s.date <= :date")
    Long countAllByStrategyIdBeforeDate(@Param("strategyId") Long strategyId, @Param("date") LocalDate date);

    @Query("SELECT COUNT(s) FROM StrategyGraphAnalysis s WHERE s.strategy.id = :strategyId AND s.profitLossAmount >= 0 AND s.date <= :date")
    Long countProfitDaysByStrategyIdBeforeDate(@Param("strategyId") Long strategyId, @Param("date") LocalDate date);

    @Query("SELECT MAX(s.profitLossAmount), MIN(s.profitLossAmount) FROM StrategyGraphAnalysis s WHERE s.strategy.id = :strategyId AND s.date <= :date")
    Object[] findProfitLossAmount(@Param("strategyId") Long strategyId, @Param("date") LocalDate date);

    @Query("SELECT MAX(s.maximumCapitalReductionAmount) FROM StrategyGraphAnalysis s WHERE s.strategy.id = :strategyId AND s.date <= :date")
    Double findMaximumCapitalReductionAmountBeforeDate(@Param("strategyId") Long strategyId, @Param("date") LocalDate date);

    // 전략 삭제 시 전체 삭제
    void deleteAllByStrategyId(Long strategyId);

    // 시작 날짜 찾기
    @Query("SELECT MIN(s.date) FROM StrategyGraphAnalysis s")
    Optional<LocalDate> findStartDate();

    // 마지막 날짜 찾기
    @Query("SELECT MAX(s.date) FROM StrategyGraphAnalysis s")
    Optional<LocalDate> findLastDate();

    // 전체 날짜 찾기
    @Query("SELECT s.date FROM StrategyGraphAnalysis s GROUP BY s.date ORDER BY s.date ASC")
    Optional<List<LocalDate>> findDates();

    // 시작 날짜부터 통합 평균 기준가 찾기
    @Query("SELECT SUM(s.standardAmount) / COUNT(s.date), s.date FROM StrategyGraphAnalysis s GROUP BY s.date ORDER BY s.date ASC")
    Optional<List<Double>> findAverageStandardAmounts();

    StrategyGraphAnalysis findByDailyId(Long dailyId);
}
