package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.strategy.entity.Monthly;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;

@Repository
public interface MonthRepository extends JpaRepository<Monthly, Long> {

    // 특정 년월의 월간분석 데이터 조회
    // year, month null일 경우 전체 조회
    @Query("""
        SELECT m FROM Monthly m WHERE m.strategy.id = :strategyId 
        AND (:startYearMonth IS NULL OR (m.yearNumber > :#{#startYearMonth?.year} 
        OR (m.yearNumber = :#{#startYearMonth?.year} AND m.monthNumber >= :#{#startYearMonth?.monthValue})))
        AND (:endYearMonth IS NULL OR (m.yearNumber < :#{#endYearMonth?.year} 
        OR (m.yearNumber = :#{#endYearMonth?.year} AND m.monthNumber <= :#{#endYearMonth?.monthValue})))
    """)
    Page<Monthly> findAllByStrategyIdAndDateBetween(
            @Param("strategyId") Long strategyId,
            @Param("startYearMonth") YearMonth startYearMonth,
            @Param("endYearMonth") YearMonth endYearMonth,
            Pageable pageable
    );

}
