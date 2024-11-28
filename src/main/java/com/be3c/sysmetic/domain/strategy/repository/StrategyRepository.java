package com.be3c.sysmetic.domain.strategy.repository;

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
public interface StrategyRepository extends JpaRepository<Strategy, Long>, StrategyRepositoryCustom {

    // 닉네임으로 트레이더 조회, 일치한 닉네임, 전략 수 내림차순 정렬
    @Query("SELECT DISTINCT s FROM Strategy s JOIN s.trader m " +
            "WHERE m.nickname LIKE CONCAT('%', :nickname, '%') AND m.roleCode = 'UR001'")
    Page<Strategy> findByTraderNicknameContaining(@Param("nickname") String nickname, Pageable pageable);

    Optional<Strategy> findByIdAndStatusCode(Long id, String statusCode);

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
}