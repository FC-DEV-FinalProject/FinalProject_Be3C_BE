package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.strategy.dto.AdminStrategyGetResponseDto;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
}
