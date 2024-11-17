package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.strategy.entity.Daily;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DailyRepository extends JpaRepository<Daily, Long> {

    // 모든 일간분석데이터 목록 조회 - date 정렬
    List<Daily> findAllByStrategyIdOrderByDateDesc(Long strategyId);

    // 특정 date의 일간분석데이터 조회
    @Query(value = "SELECT * FROM daily d WHERE d.strategy_id = :strategyId " +
            "AND DATE(d.date) = :date", nativeQuery = true)
    Daily findByStrategyIdAndDate(@Param("strategyId") Long strategyId, @Param("date") LocalDate date);

    // 특정 date 이후의 일간분석데이터 목록 조회 - date 정렬
    @Query("SELECT d FROM Daily d WHERE d.strategy.id = :strategyId " +
            "AND FUNCTION('DATE', d.date) > :startDate " +
            "ORDER BY d.date ASC")
    List<Daily> findAllByStrategyIdAndDateAfterOrderByDateAsc(@Param("strategyId") Long strategyId, @Param("startDate") LocalDate startDate);

    // 특정 date 이전의 일간분석데이터 조회 - date 정렬
    @Query(value = "SELECT * FROM daily d WHERE d.strategy_id = :strategyId " +
            "AND DATE(d.date) < :startDate " +
            "ORDER BY d.date DESC LIMIT 1", nativeQuery = true)
    Daily findFirstByStrategyIdAndDateBeforeOrderByDateDesc(@Param("strategyId") Long strategyId, @Param("startDate") LocalDate startDate);

    // 일간분석데이터 조회 - 페이지당 10개, 기간 검색
    @Query("SELECT d FROM Daily d WHERE (:startDate IS NULL OR d.date >= :startDate) " +
            "AND d.strategy.id = :strategyId " +
            "AND (:endDate IS NULL OR d.date <= :endDate)")
    Page<Daily> findAllByStrategyIdAndDateBetween(@Param("strategyId") Long strategyId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("pageable") Pageable pageable);

    // 일간분석데이터 목록 조회 - 특정 년월
    @Query(value = "SELECT * FROM daily d WHERE d.strategy_id = :strategyId " +
            "AND YEAR(d.date) = :year " +
            "AND MONTH(d.date) = :month", nativeQuery = true)
    List<Daily> findAllByStrategyIdAndYearAndMonth(@Param("strategyId") Long strategyId, @Param("year") int year, @Param("month") int month);

    // 일간분석데이터 조회 - 첫 데이터, Date 정렬
    Daily findTopByStrategyIdOrderByDateAsc(Long strategyId);

    // 일간분석데이터 조회 - 최신 데이터, date 정렬
    Daily findTopByStrategyIdOrderByDateDesc(Long strategyId);

    // 입출금액 총합 조회
    @Query("SELECT SUM(d.depositWithdrawalAmount) FROM Daily d WHERE d.strategy.id = :strategyId")
    Double findTotalDepositWithdrawalAmountByStrategyId(@Param("strategyId") Long strategyId);

    // 손익금액 총합 조회
    @Query("SELECT SUM(d.profitLossAmount) FROM Daily d WHERE d.strategy.id = :strategyId")
    Double findTotalProfitLossAmountByStrategyId(@Param("strategyId") Long strategyId);

    // 손익률 총합 조회
    @Query("SELECT SUM(d.profitLossRate) FROM Daily d WHERE d.strategy.id = :strategyId")
    Double findTotalProfitLossRateByStrategyId(@Param("strategyId") Long strategyId);

    // 일간분석데이터 최대 기준가 조회
    Daily findTopByStrategyIdOrderByStandardAmountDesc(Long strategyId);

    // 일간분석데이터 총 개수 조회
    Long countByStrategyId(Long strategyId);

    // 일간분석데이터 최대일이익금액 조회
    Daily findTopByStrategyIdOrderByProfitLossAmountDesc(Long strategyId);

    // 일간분석데이터 최대일이익률 조회
    Daily findTopByStrategyIdOrderByProfitLossRateDesc(Long strategyId);

    // 일간분석데이터 최대일손실금액 조회
    Daily findTopByStrategyIdOrderByProfitLossAmountAsc(Long strategyId);

    // 일간분석데이터 최대일손실률 조회
    Daily findTopByStrategyIdOrderByProfitLossRateAsc(Long strategyId);

    // 일간분석데이터 현재연속손익일수 조회 - 손익금 + -> - 또는 - -> + 카운트
    @Query(value = """
    SELECT COUNT(*) 
    FROM (
        SELECT 
            d.date,
            d.profit_loss_amount,
            @prev_sign := @curr_sign AS prev_sign,
            @curr_sign := CASE 
                              WHEN d.profit_loss_amount > 0 THEN 1
                              WHEN d.profit_loss_amount < 0 THEN -1
                              ELSE 0
                          END AS curr_sign
        FROM daily d
        CROSS JOIN (SELECT @curr_sign := NULL, @prev_sign := NULL) vars
        WHERE d.strategy_id = :strategyId
        ORDER BY d.date DESC
    ) sub
    WHERE prev_sign IS NULL OR prev_sign = curr_sign;
    """, nativeQuery = true)
    Long findContinuousProfitLossDays(@Param("strategyId") Long strategyId);

    // 일간분석데이터 총이익일수 조회 - 손익금이 양수인 데이터 카운트
    @Query(value = "SELECT COUNT(*) FROM daily d WHERE d.strategy_id = :strategyId AND d.profit_loss_amount > 0", nativeQuery = true)
    Long countProfitDays(@Param("strategyId") Long strategyId);

    // 일간분석데이터 최대연속이익일수 조회
    @Query(value = """
    SELECT MAX(consecutive_days) 
        FROM (
            SELECT 
                COUNT(*) AS consecutive_days
            FROM (
                SELECT 
                    d.date,
                    d.profit_loss_amount,
                    @group_id := IF(d.profit_loss_amount >= 0, @group_id, @group_id + 1) AS group_id
                FROM daily d
                CROSS JOIN (SELECT @group_id := 0) vars
                WHERE d.strategy_id = :strategyId
                ORDER BY d.date ASC
            ) sub
            WHERE sub.profit_loss_amount >= 0
            GROUP BY group_id
        ) final;
    """, nativeQuery = true)
    Long findMaxConsecutiveProfitDays(@Param("strategyId") Long strategyId);

    // 일간분석데이터 총손실일수 조회 - 손익금이 음수인 데이터 카운트
    @Query(value = "SELECT COUNT(*) FROM daily d WHERE d.strategy_id = :strategyId AND d.profit_loss_amount < 0", nativeQuery = true)
    Long countLossDays(@Param("strategyId") Long strategyId);

    // 일간분석데이터 최대연속손실일수 조회
    @Query(value = """
    SELECT MAX(consecutive_days) 
        FROM (
            SELECT 
                COUNT(*) AS consecutive_days
            FROM (
                SELECT 
                    d.date,
                    d.profit_loss_amount,
                    @group_id := IF(d.profit_loss_amount < 0, @group_id, @group_id + 1) AS group_id
                FROM daily d
                CROSS JOIN (SELECT @group_id := 0) vars
                WHERE d.strategy_id = :strategyId
                ORDER BY d.date ASC
            ) sub
            WHERE sub.profit_loss_amount < 0
            GROUP BY group_id
        ) final;
    """, nativeQuery = true)
    Long findMaxConsecutiveLossDays(@Param("strategyId") Long strategyId);

    @Query(value = """
    SELECT DATEDIFF(CURDATE(), (
        SELECT MAX(d.date)
        FROM daily d
        WHERE d.strategy_id = :strategyId
          AND d.profit_loss_amount = (
              SELECT MAX(profit_loss_amount)
              FROM daily
              WHERE strategy_id = :strategyId
          )
    )) AS daysSinceHighPoint
    """, nativeQuery = true)
    Long findHighPointRenewalProgress(@Param("strategyId") Long strategyId);

    // 총이익금액
    @Query("SELECT SUM(d.profitLossAmount) FROM Daily d WHERE d.strategy.id = :strategyId AND d.profitLossAmount > 0")
    Double findTotalProfitAmountByStrategyId(@Param("strategyId") Long strategyId);

    // 총손실금액
    @Query("SELECT SUM(d.profitLossAmount) FROM Daily d WHERE d.strategy.id = :strategyId AND d.profitLossAmount < 0")
    Double findTotalLossAmountByStrategyId(@Param("strategyId") Long strategyId);

}