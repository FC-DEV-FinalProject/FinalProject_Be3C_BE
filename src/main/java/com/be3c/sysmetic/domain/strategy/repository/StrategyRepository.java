package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.strategy.dto.KpRatios;
import com.be3c.sysmetic.domain.member.dto.InterestStrategyGetResponseDto;
import com.be3c.sysmetic.domain.member.dto.MyStrategyGetResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.AdminStrategyGetResponseDto;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import org.apache.http.cookie.SM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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
    @Query("UPDATE Strategy s SET s.statusCode = 'PRIVATE' WHERE s.id = :strategyId")
    int updateStatusToPrivate(@Param("strategyId") Long strategyId);

    @Query("""
        SELECT
            s
        FROM Strategy s
        WHERE
            s.id = :id
        AND s.trader.id = :userId
        AND s.statusCode = :statusCode
    """)
    Optional<Strategy> findByIdAndTraderIdAndStatusCode(
            @Param("id") Long id,
            @Param("userId") Long userId,
            @Param("statusCode") String statusCode
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

    // TODO SM Score 1위 전략 찾기
    @Query(value = "SELECT s.strategy_name FROM strategy s ORDER BY s.sm_score DESC LIMIT 1", nativeQuery = true)
    String findTop1SmScore();
}