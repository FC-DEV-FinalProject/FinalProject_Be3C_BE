package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.strategy.dto.AdminStrategyGetResponseDto;
import com.be3c.sysmetic.domain.strategy.entity.StrategyApprovalHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StrategyApprovalRepository extends JpaRepository<StrategyApprovalHistory, Long> {
    Optional<StrategyApprovalHistory> findByIdAndStatusCode(Long id, String statusCode);

    @Query("""
        SELECT
            s
        FROM
            StrategyApprovalHistory s
        WHERE
            s.id = :id
        AND s.statusCode IN ('SA001', 'SA002')
    """)
    Optional<StrategyApprovalHistory> findByStrategyIdAndStatusCodeNotApproval(Long id);

    @Query("""
        SELECT
            s
        FROM
            StrategyApprovalHistory s
        WHERE
            s.id = :id
        AND s.statusCode IN ('SA001', 'SA002')
    """)
    Optional<StrategyApprovalHistory> findByStrategyIdNotApproval(Long id);

    @Query(value = """
        SELECT
            s.id AS strategyId,
            s.strategy_name AS strategyName,
            m.name AS traderName,
            s.status_code AS openStatusCode,
            s.created_at AS strategyCreateDate,
            sa.status_code AS approvalStatusCode,
            null
        FROM
            strategy s
        JOIN
            member m ON s.member_id = m.id
        JOIN
            (
                SELECT
                    strategy_id,
                    status_code,
                    modified_at
                FROM
                    strategy_approval_history
                WHERE
                    (strategy_id, modified_at) IN (
                        SELECT
                            strategy_id,
                            MAX(modified_at)
                        FROM
                            strategy_approval_history
                        GROUP BY
                            strategy_id
                    )
            ) sa ON sa.strategy_id = s.id
        WHERE
            (:openStatus IS NULL OR s.status_code = :openStatus)
            AND (:approvalStatusCode IS NULL OR sa.status_code = :approvalStatusCode)
            AND (:strategyName IS NULL OR s.strategy_name LIKE CONCAT('%', :strategyName, '%'));
        ORDER BY strategyCreateDate DESC
    """, nativeQuery = true)
    /* 윈도우 함수 사용 쿼리
    @Query(value = """
        WITH LatestApprovalHistory AS (
            SELECT
                sa.strategy_id,
                sa.status_code,
                sa.modified_at,
                ROW_NUMBER() OVER (PARTITION BY sa.strategy_id ORDER BY sa.modified_at DESC) AS row_num
            FROM
                strategy_approval_history sa
        )
        SELECT
            s.id AS strategyId,
            s.strategy_name AS strategyName,
            m.name AS traderName,
            s.status_code AS openStatusCode,
            s.created_at AS strategyCreateDate,
            lah.status_code AS approvalStatusCode
        FROM
            strategy s
        JOIN
            member m ON s.member_id = m.id
        JOIN
            LatestApprovalHistory lah ON lah.strategy_id = s.id
        WHERE
            lah.row_num = 1;
    """, nativeQuery = true)
     */
    Page<AdminStrategyGetResponseDto> findStrategiesAdminPage(
            @Param("openStatus") String openStatus,
            @Param("approvalStatusCode") String approvalStatusCode,
            @Param("strategyName") String strategyName,
            Pageable pageable
    );
}
