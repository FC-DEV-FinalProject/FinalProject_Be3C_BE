package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.member.dto.InterestStrategyGetResponseDto;
import com.be3c.sysmetic.domain.member.dto.MyStrategyGetResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.AdminStrategyGetResponseDto;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import jakarta.persistence.SqlResultSetMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import java.util.List;

@Repository
public interface StrategyRepository extends JpaRepository<Strategy, Long> {
    @Query("SELECT count(*) FROM Strategy s WHERE s.statusCode = 'PUBLIC'")
    Long countOpenStatus();

    // 닉네임으로 트레이더 조회, 일치한 닉네임, 전략 수 내림차순 정렬
    @Query("SELECT DISTINCT s FROM Strategy s JOIN s.trader m " +
            "WHERE m.nickname LIKE CONCAT('%', :nickname, '%') AND m.roleCode = 'UR001'")
    Page<Strategy> findByTraderNicknameContaining(@Param("nickname") String nickname, Pageable pageable);

    Optional<Strategy> findByIdAndStatusCode(Long id, String statusCode);

    // 전략명 중복 확인
    boolean existsByName(String name);

    @Query("SELECT s FROM Strategy s WHERE s.statusCode != 'NOT_USING_STATE'")
    List<Strategy> findAllUsingState();

    @Query("""
        SELECT new com.be3c.sysmetic.domain.member.dto.InterestStrategyGetResponseDto
            (
            s.id,
            s.name,
            m.name,
            null,
            s.followerCount,
            s.smScore,
            ss.accumulatedProfitLossRate,
            ss.maximumCapitalReductionRate
            )
        FROM InterestStrategy i
        JOIN i.strategy s
        JOIN s.trader m
        JOIN StrategyStatistics ss on ss.strategy.id = s.id
        WHERE i.member.id = :memberId AND i.folder.id = :folderId AND i.statusCode = :statusCode
        """)
    Page<MyStrategyGetResponseDto> findPageByIdAndStatusCode(
            Long memberId, Long folderId, String statusCode, Pageable pageable
    );

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
}