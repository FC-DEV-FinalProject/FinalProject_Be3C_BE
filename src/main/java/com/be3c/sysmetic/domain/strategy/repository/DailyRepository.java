package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.strategy.dto.KpRatioParametersDto;
import com.be3c.sysmetic.domain.strategy.entity.Daily;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyRepository extends JpaRepository<Daily, Long> {

    // 모든 일간분석데이터 목록 조회 - 최신순 정렬
    List<Daily> findAllByStrategyIdOrderByDateDesc(Long strategyId);

    // 모든 일간분석데이터 목록 조회 - 오래된순 정렬
//    List<Daily> findAllByStrategyIdOrderByDateAsc(Long strategyId);

    // 일간분석데이터 목록 조회 - 기간 검색, 최신순 정렬
    @Query("""
        SELECT d FROM Daily d
        WHERE d.strategy.id = :strategyId
        AND (:startDate IS NULL OR d.date >= :startDate)
        AND (:endDate IS NULL OR d.date <= :endDate)
        ORDER BY d.date DESC
    """)
    Page<Daily> findAllByStrategyIdAndDateBetween(
            @Param("strategyId") Long strategyId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    // 일간분석데이터 목록 조회 - 특정 년월
    @Query("""
        SELECT d FROM Daily d 
        WHERE d.strategy.id = :strategyId
        AND FUNCTION('YEAR', d.date) = :year
        AND FUNCTION('MONTH', d.date) = :month
    """)
    List<Daily> findAllByStrategyIdAndYearAndMonth(
            @Param("strategyId") Long strategyId,
            @Param("year") int year,
            @Param("month") int month
    );

    // 특정 일자의 일간분석데이터 조회
    @Query("""
        SELECT d FROM Daily d 
        WHERE d.strategy.id = :strategyId
        AND d.date = :date
    """)
    Daily findByStrategyIdAndDate(
            @Param("strategyId") Long strategyId,
            @Param("date") LocalDate date
    );

    // 특정 일자 이후의 일간분석데이터 목록 조회 - 오래된순 정렬
    @Query("""
        SELECT d FROM Daily d 
        WHERE d.strategy.id = :strategyId
        AND d.date > :startDate
        ORDER BY d.date ASC
    """)
    List<Daily> findAllByStrategyIdAndDateAfterOrderByDateAsc(
            @Param("strategyId") Long strategyId,
            @Param("startDate") LocalDate startDate
    );

    // 특정 일자 이전의 일간분석데이터 조회
    @Query("""
        SELECT d FROM Daily d
        WHERE d.strategy.id = :strategyId
        AND d.date < :startDate
        ORDER BY d.date DESC LIMIT 1
    """)
    Daily findFirstByStrategyIdAndDateBeforeOrderByDateDesc(
            @Param("strategyId") Long strategyId,
            @Param("startDate") LocalDate startDate
    );

    // 일간분석데이터 조회 - 첫 데이터
    Daily findTopByStrategyIdOrderByDateAsc(Long strategyId);

    // 일간분석데이터 조회 - 최신 데이터
    Daily findTopByStrategyIdOrderByDateDesc(Long strategyId);

    // 입출금액 총합 조회
    @Query("""
        SELECT SUM(d.depositWithdrawalAmount)
        FROM Daily d 
        WHERE d.strategy.id = :strategyId
    """)
    Double findTotalDepositWithdrawalAmountByStrategyId(@Param("strategyId") Long strategyId);

    // 손익금액 총합 조회
    @Query("""
        SELECT SUM(d.profitLossAmount) 
        FROM Daily d 
        WHERE d.strategy.id = :strategyId
    """)
    Double findTotalProfitLossAmountByStrategyId(@Param("strategyId") Long strategyId);

    // 손익률 총합 조회
    @Query("""
        SELECT SUM(d.profitLossRate) 
        FROM Daily d 
        WHERE d.strategy.id = :strategyId
    """)
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
                LAG(d.profit_loss_amount) OVER (PARTITION BY d.strategy_id ORDER BY d.date DESC) AS prev_profit_loss,
                CASE
                    WHEN d.profit_loss_amount > 0 THEN 1
                    WHEN d.profit_loss_amount < 0 THEN -1
                    ELSE 0
                END AS curr_sign, -- 현재 부호
                CASE
                    WHEN LAG(d.profit_loss_amount) OVER (PARTITION BY d.strategy_id ORDER BY d.date DESC) > 0 THEN 1
                    WHEN LAG(d.profit_loss_amount) OVER (PARTITION BY d.strategy_id ORDER BY d.date DESC) < 0 THEN -1
                    ELSE 0
                END AS prev_sign -- 이전 부호
            FROM daily d
            WHERE d.strategy_id = :strategyId
        ) sub
        WHERE sub.prev_sign IS NULL OR sub.prev_sign = sub.curr_sign;
    """, nativeQuery = true)
    Long findContinuousProfitLossDays(@Param("strategyId") Long strategyId);

    // 일간분석데이터 총이익일수 조회 - 손익금이 양수인 데이터 카운트
    @Query("""
        SELECT COUNT(*) FROM Daily d 
        WHERE d.strategy.id = :strategyId 
        AND d.profitLossAmount > 0
    """)
    Long countProfitDays(@Param("strategyId") Long strategyId);

    // 일간분석데이터 최대연속이익일수 조회
    @Query(value = """
        WITH profit_groups AS (
            SELECT 
                d.date,
                d.profit_loss_amount,
                CASE 
                    WHEN d.profit_loss_amount > 0 THEN 1
                    ELSE 0
                END AS is_profit, -- 현재 손익 상태
                CASE 
                    WHEN LAG(d.profit_loss_amount) OVER (PARTITION BY d.strategy_id ORDER BY d.date ASC) >= 0 THEN 1
                    ELSE 0
                END AS prev_is_profit -- 이전 일의 손익 상태
            FROM daily d
            WHERE d.strategy_id = :strategyId
        ),
        grouped AS (
            SELECT 
                *,
                SUM(CASE WHEN is_profit != prev_is_profit THEN 1 ELSE 0 END) -- 상태 변경시 새로운 그룹
                OVER (PARTITION BY strategy_id ORDER BY date ASC) AS group_id
            FROM profit_groups
        )
        SELECT MAX(COUNT(*)) AS consecutive_days
        FROM grouped
        WHERE is_profit = 1 -- 연속 이익
        GROUP BY group_id;
    """, nativeQuery = true)
    Long findMaxConsecutiveProfitDays(@Param("strategyId") Long strategyId);

    // 일간분석데이터 총손실일수 조회 - 손익금이 음수인 데이터 카운트
    @Query("""
        SELECT COUNT(*) FROM Daily d 
        WHERE d.strategy.id = :strategyId 
        AND d.profitLossAmount < 0
    """)
    Long countLossDays(@Param("strategyId") Long strategyId);

    // 일간분석데이터 최대연속손실일수 조회
    @Query(value = """
        WITH loss_groups AS (
            SELECT 
                d.date,
                d.profit_loss_amount,
                CASE 
                    WHEN d.profit_loss_amount < 0 THEN 1
                    ELSE 0
                END AS is_loss, -- 현재 손익 상태
                CASE 
                    WHEN LAG(d.profit_loss_amount) OVER (PARTITION BY d.strategy_id ORDER BY d.date ASC) < 0 THEN 1
                    ELSE 0
                END AS prev_is_profit -- 이전 일의 손익 상태
            FROM daily d
            WHERE d.strategy_id = :strategyId
        ),
        grouped AS (
            SELECT 
                *,
                SUM(CASE WHEN is_loss != prev_is_loss THEN 1 ELSE 0 END) -- 상태 변경시 새로운 그룹
                OVER (PARTITION BY strategy_id ORDER BY date ASC) AS group_id
            FROM loss_groups
        )
        SELECT MAX(COUNT(*)) AS consecutive_days
        FROM grouped
        WHERE is_loss = 1 -- 연속 이익
        GROUP BY group_id;
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
    @Query("""
        SELECT SUM(d.profitLossAmount) FROM Daily d 
        WHERE d.strategy.id = :strategyId 
        AND d.profitLossAmount > 0
    """)
    Double findTotalProfitAmountByStrategyId(@Param("strategyId") Long strategyId);

    // 총손실금액
    @Query("""
        SELECT SUM(d.profitLossAmount) FROM Daily d
        WHERE d.strategy.id = :strategyId 
        AND d.profitLossAmount < 0
    """)
    Double findTotalLossAmountByStrategyId(@Param("strategyId") Long strategyId);


    /* 엑셀을 위한 메서드 */
    List<Daily> findAllByStrategyIdOrderByDateAsc(Long strategyId);         // 전략에서도 사용!
    List<Daily> findByDateGreaterThanEqualOrderByDateAsc(LocalDate date);
    Optional<Daily> findTop1ByDateBeforeOrderByDateDesc(LocalDate date);


    // 전략 지표 값 업데이트
    @Query("SELECT MAX(d.profitLossAmount), MIN(d.profitLossAmount) FROM Daily d WHERE d.strategy.id = :strategyId")
    Object[] findMaxAndMinProfitLossAmount(@Param("strategyId") Long strategyId);

    // 누적손익률 조회
    @Query("SELECT d.accumulatedProfitLossRate FROM Daily d WHERE d.strategy.id = :strategyId ORDER BY d.date DESC")
    Double findLatestAccumulatedProfitLossRate(@Param("strategyId") Long strategyId);


    // KP Ratio 계산에서 사용
    @Query("SELECT new com.be3c.sysmetic.domain.strategy.dto.KpRatioParametersDto(d.date, d.profitLossRate, d.accumulatedProfitLossRate) "
            + "FROM Daily d WHERE d.strategy.id = :strategyId ORDER BY d.date DESC")
    List<KpRatioParametersDto> findKpRatioParameters(@Param("strategyId") Long strategyId);

    @Modifying
    @Query("DELETE FROM Daily d WHERE d.strategy.id = :strategyId")
    int deleteByStrategyId(Long strategyId);

    // 메인 페이지 평균 통합 누적 손익률
    @Query("SELECT SUM(d.accumulatedProfitLossRate) / COUNT(d.date) FROM Daily d WHERE d.date >= :startDate GROUP BY d.date")
    Optional<List<Double>> findAccumulatedProfitLossRates(@Param("startDate") LocalDate startDate);
}