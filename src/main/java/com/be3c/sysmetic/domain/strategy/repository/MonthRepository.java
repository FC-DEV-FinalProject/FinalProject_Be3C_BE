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

    @Query("SELECT m FROM Monthly m WHERE m.strategy.id = :strategyId " +
            "AND m.yearNumber = :startYear AND m.monthNumber >= :startMonth " +
            "AND (m.yearNumber = :endYear AND m.monthNumber <= :endMonth) " +
            "OR (m.yearNumber > :startYear AND m.yearNumber < :endYear) " +
            "OR (m.yearNumber = :endYear AND m.monthNumber <= :endMonth)")
    Page<Monthly> findAllByStrategyIdAndDateBetween(
            @Param("strategyId") Long strategyId,
            @Param("startYear") int startYear,
            @Param("startMonth") int startMonth,
            @Param("endYear") int endYear,
            @Param("endMonth") int endMonth,
            @Param("pageable") Pageable pageable
    );
}
