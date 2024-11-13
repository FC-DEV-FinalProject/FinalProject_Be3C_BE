package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.strategy.entity.Daily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DailyRepository extends JpaRepository<Daily, Long> {
    // 최신 일간분석데이터 조회 - date 정렬
    Daily findTopByStrategyIdOrderByDateDesc(Long strategyId);

    // 모든 일간분석데이터 목록 조회 - date 정렬
    List<Daily> findAllByStrategyIdOrderByDateDesc(Long strategyId);

    // 특정 date의 일간분석데이터 조회
    @Query(value = "SELECT * FROM daily d WHERE d.strategy_id = :strategyId AND DATE(d.date) = :date", nativeQuery = true)
    Daily findByStrategyIdAndDate(@Param("strategyId") Long strategyId, @Param("date") LocalDate date);

    // 특정 date 이후의 일간분석데이터 목록 조회 - date 정렬
    @Query("SELECT d FROM Daily d WHERE d.strategy.id = :strategyId AND FUNCTION('DATE', d.date) > :startDate ORDER BY d.date ASC")
    List<Daily> findAllByStrategyIdAndDateAfterOrderByDateAsc(@Param("strategyId") Long strategyId, @Param("startDate") LocalDate startDate);

    // 특정 date 이전의 일간분석데이터 조회 - date 정렬
    @Query(value = "SELECT * FROM daily d WHERE d.strategy_id = :strategyId AND DATE(d.date) < :startDate ORDER BY d.date DESC LIMIT 1", nativeQuery = true)
    Daily findFirstByStrategyIdAndDateBeforeOrderByDateDesc(@Param("strategyId") Long strategyId, @Param("startDate") LocalDate startDate);

}