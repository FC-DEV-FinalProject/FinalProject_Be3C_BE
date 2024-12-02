package com.be3c.sysmetic.domain.member.repository;

import com.be3c.sysmetic.domain.member.dto.InterestStrategyGetResponseDto;
import com.be3c.sysmetic.domain.member.entity.InterestStrategy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InterestStrategyRepository extends JpaRepository<InterestStrategy, Long> {

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
        WHERE i.folder.member.id = :memberId AND i.folder.id = :folderId AND i.statusCode = :statusCode
        """)
    Page<InterestStrategyGetResponseDto> findPageByIdAndStatusCode(
            Long memberId, Long folderId, String statusCode, Pageable pageable
    );

    @Query("""
        SELECT i
        from InterestStrategy i
        WHERE
        i.folder.member.id = :memberId AND i.strategy.id = :strategyId AND i.statusCode = :statusCode
    """)
    Optional<InterestStrategy> findByMemberIdAndStrategyIdAndStatusCode(
            Long memberId, Long strategyId, String statusCode
    );

    @Query("""
        SELECT i
        from InterestStrategy i
        WHERE
        i.folder.member.id = :memberId AND i.folder.id = :folderId AND i.strategy.id = :strategyId AND i.statusCode = :statusCode
    """)
    Optional<InterestStrategy> findByMemberIdAndFolderIdAndStrategyIdAndStatusCode(
            Long memberId, Long folderId, Long strategyId, String statusCode
    );

    @Query("""
        SELECT i
        from InterestStrategy i
        WHERE
        i.folder.member.id = :memberId AND i.strategy.id = :strategyId
    """)
    Optional<InterestStrategy> findByMemberIdAndStrategyId(
            Long memberId, Long strategyId
    );

    @Modifying
    @Query("DELETE FROM InterestStrategy i WHERE i.folder.id = :folderId")
    void deleteByFolderId(Long folderId);

}
