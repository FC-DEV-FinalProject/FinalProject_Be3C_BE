package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.strategy.entity.Daily;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
    @Query("SELECT d FROM Daily d WHERE d.strategy.id = :strategyId AND FUNCTION('DATE', d.date) = :date")
    Daily findByStrategyIdAndDate(@Param("strategyId") Long strategyId, @Param("date") LocalDate date);

    // 특정 date 이후의 일간분석데이터 목록 조회 - date 정렬
    @Query("SELECT d FROM Daily d WHERE d.strategy.id = :strategyId AND FUNCTION('DATE', d.date) > :startDate ORDER BY d.date ASC")
    List<Daily> findAllByStrategyIdAndDateAfterOrderByDateAsc(Long strategyId, LocalDate startDate);

    // 특정 date 이전의 일간분석데이터 조회 - date 정렬
    @Query("SELECT d FROM Daily d WHERE d.strategy.id = :strategyId AND FUNCTION('DATE', d.date) < :startDate ORDER BY d.date DESC")
    Daily findTopByStrategyIdAndDateBeforeOrderByDateDesc(Long strategyId, LocalDate startDate);

}