package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.strategy.dto.AdminStrategyGetResponseDto;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StrategyRepository extends JpaRepository<Strategy, Long> {
    boolean existsByName(String name); // 전략명 중복 확인

    @Query("""
        SELECT new com.be3c.sysmetic.domain.strategy.dto.AdminStrategyGetResponseDto(
            s.id, s.name, s.trader.name, s.statusCode
        )
        FROM Strategy s
    """)
    Page<AdminStrategyGetResponseDto> findStrategiesPage(Pageable pageable);

    // 닉네임으로 트레이더 조회, 일치한 닉네임, 전략 수 내림차순 정렬
    @Query("SELECT DISTINCT s FROM Strategy s JOIN s.trader m " +
            "WHERE m.nickname LIKE CONCAT('%', :nickname, '%') AND m.roleCode = 'UR001'")
    Page<Strategy> findByTraderNicknameContaining(@Param("nickname") String nickname, Pageable pageable);
}