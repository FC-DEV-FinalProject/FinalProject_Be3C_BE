package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.strategy.dto.AdminStrategyGetResponseDto;
import com.be3c.sysmetic.domain.strategy.entity.StrategyApprovalHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StrategyApprovalRepository extends JpaRepository<StrategyApprovalHistory, Long> {
    @Query("""
        SELECT new com.be3c.sysmetic.domain.strategy.dto.AdminStrategyApprovalGetResponseDto(
            s.id, sh.id, s.name, s.trader.name, s.statusCode
        )
        FROM StrategyApprovalHistory sh
        JOIN Strategy s ON s.id = sh.strategy.id
        WHERE sh.statusCode = :statusCode
    """)
    Page<AdminStrategyGetResponseDto> findApprovalStrategy(Pageable pageable, String statusCode);

    Optional<StrategyApprovalHistory> findByIdAndStatusCode(Long id, String statusCode);
}
