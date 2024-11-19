package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.strategy.entity.Monthly;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MonthRepository extends JpaRepository<Monthly, Long> {

    // 특정 년월의 월간분석 데이터 조회
    // year, month null일 경우 전체 조회
    @Query("SELECT m FROM Monthly m WHERE m.strategy.id = :strategyId " +
            "AND (:startYear IS NULL OR (m.yearNumber > :startYear OR (m.yearNumber = :startYear AND (:startMonth IS NULL OR m.monthNumber >= :startMonth)))) " +
            "AND (:endYear IS NULL OR (m.yearNumber < :endYear OR (m.yearNumber = :endYear AND (:endMonth IS NULL OR m.monthNumber <= :endMonth))))")
    Page<Monthly> findAllByStrategyIdAndDateBetween(
            @Param("strategyId") Long strategyId,
            @Param("startYear") Integer startYear,
            @Param("startMonth") Integer startMonth,
            @Param("endYear") Integer endYear,
            @Param("endMonth") Integer endMonth,
            @Param("pageable") Pageable pageable
    );

}
