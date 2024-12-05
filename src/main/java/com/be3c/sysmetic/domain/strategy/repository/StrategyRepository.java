package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.strategy.dto.*;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import java.util.List;

@Repository
public interface StrategyRepository extends JpaRepository<Strategy, Long>, StrategyRepositoryCustom {
    @Query("SELECT count(*) FROM Strategy s WHERE s.statusCode = 'PUBLIC'")
    Long countOpenStatus();

    List<Strategy> findByTraderId(Long traderId);

    // 닉네임으로 트레이더 조회, 일치한 닉네임, 전략 수 내림차순 정렬
    @Query("SELECT DISTINCT s FROM Strategy s JOIN s.trader m " +
            "WHERE m.nickname LIKE CONCAT('%', :nickname, '%') AND m.roleCode = 'UR001'")
    Page<Strategy> findByTraderNicknameContaining(@Param("nickname") String nickname, Pageable pageable);

    @Query("SELECT s FROM Strategy s WHERE s.id = :id AND s.statusCode = 'PUBLIC'")
    Optional<Strategy> findByIdAndOpenStatusCode(Long id);

    // 전략명 중복 확인
    boolean existsByName(String name);

    @Query("SELECT s FROM Strategy s WHERE s.statusCode != 'NOT_USING_STATE'")
    List<Strategy> findAllUsingState();

    // 전략 비공개 상태로 변환
    @Modifying
    @Query("UPDATE Strategy s SET s.statusCode = 'PRIVATE' WHERE s.id = :strategyId")
    int updateStatusToPrivate(@Param("strategyId") Long strategyId);

    @Query("""
        SELECT
            s
        FROM Strategy s
        WHERE
            s.id = :id
        AND s.trader.id = :userId
    """)
    Optional<Strategy> findByIdAndTraderId(
            @Param("id") Long id,
            @Param("userId") Long userId
    );

    @Query("""
        SELECT
            s
        FROM Strategy s
        WHERE
            s.id = :id
        AND s.trader.id = :userId
    """)
    Optional<Strategy> findPrivateByIdAndTraderId(
            @Param("id") Long id,
            @Param("userId") Long userId
    );

    @Modifying
    @Query("DELETE FROM Strategy s WHERE s.trader.id = :memberId")
    void deleteByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT count (*) FROM Strategy s WHERE s.trader.id = :memberId")
    long countByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT COUNT(*) FROM Strategy s WHERE s.trader.id = :traderId AND s.statusCode = 'PUBLIC'")
    Long countStrategyByOneTrader(@Param("traderId") Long traderId);

    // MDD 업데이트
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Strategy s SET s.mdd = :mdd WHERE s.id = :strategyId")
    void updateMdd(@Param("strategyId") Long strategyId, @Param("mdd") Double mdd);

    // KP Ratio 업데이트
    @Modifying
    @Query("UPDATE Strategy s SET s.kpRatio = :kpRatio WHERE s.id = :strategyId")
    void updateKpRatio(@Param("strategyId") Long strategyId, @Param("kpRatio") Double kpRatio);

    // SM Score 업데이트
    @Modifying
    @Query("UPDATE Strategy s SET s.smScore = :smScore WHERE s.id = :strategyId")
    void updateSmScore(@Param("strategyId") Long strategyId, @Param("smScore") Double smScore);

    // accumulatedProfitLossRate 업데이트
    @Modifying
    @Query("UPDATE Strategy s SET s.accumulatedProfitLossRate = :accumulatedProfitLossRate WHERE s.id = :strategyId")
    void updateAccumulatedLProfitLossRate(@Param("strategyId") Long strategyId, @Param("accumulatedProfitLossRate") Double accumulatedProfitLossRate);

    // SM Score 계산 시 필요한 KP Ratio 조회
    @Query("SELECT new com.be3c.sysmetic.domain.strategy.dto.KpRatios(s.id, s.kpRatio) FROM Strategy s WHERE s.statusCode = :statusCode")
    List<KpRatios> findKpRatios(String statusCode);

    @Query("""
        SELECT new com.be3c.sysmetic.domain.strategy.dto.MyStrategyListDto
            (
            s.id,
            me.id,
            null,
            s.cycle,
            null,
            s.name,
            s.accumulatedProfitLossRate,
            s.mdd,
            s.smScore
            )
        FROM Strategy s
        JOIN s.trader m
        JOIN s.method me
        LEFT JOIN StrategyStatistics ss on ss.strategy.id = s.id
        WHERE s.trader.id = :memberId AND s.statusCode <> 'NOT_USING_STATE'
        """)
    Page<MyStrategyListDto> findPageMyStrategy(
            Long memberId, Pageable pageable
    );

    @Query(value = "SELECT s.strategy_name FROM strategy s ORDER BY s.sm_score DESC LIMIT 1", nativeQuery = true)
    Optional<String> findTop1SmScore();

    @Query(value = "SELECT s.*, " +
            "       (mdd_rank + stddev_rank + winning_rate_rank) / 3 AS average_rank " +
            "FROM strategy s " +
            "JOIN (SELECT id, RANK() OVER (ORDER BY mdd ASC) AS mdd_rank " +
            "      FROM strategy) mdd_rank_tbl " +
            "ON s.id = mdd_rank_tbl.id " +
            "JOIN (SELECT id, RANK() OVER (ORDER BY STDDEV(accumulated_profit_loss_rate) ASC) AS stddev_rank " +
            "      FROM strategy " +
            "      GROUP BY id) stddev_rank_tbl " +
            "ON s.id = stddev_rank_tbl.id " +
            "JOIN (SELECT id, RANK() OVER (ORDER BY winning_rate DESC) AS winning_rate_rank " +
            "      FROM strategy) winning_rate_rank_tbl " +
            "ON s.id = winning_rate_rank_tbl.id " +
            "ORDER BY average_rank ASC",
            nativeQuery = true)
    Page<Strategy> findDefensiveStrategies(Pageable pageable);
}