package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.strategy.dto.PageReplyResponseDto;
import com.be3c.sysmetic.domain.strategy.entity.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {

    @Query("SELECT new com.be3c.sysmetic.domain.strategy.dto.PageReplyResponseDto(" +
            "r.member.id, r.strategy.id, r.content) " +
            "FROM Reply r " +
            "WHERE r.member.id = :memberId and r.statusCode = :statusCode")
    Page<PageReplyResponseDto> findPageByMemberIdAndStatusCode(
            @Param("memberId") Long memberId,
            @Param("statusCode") String statusCode,
            Pageable pageable
    );

    @Query("SELECT new com.be3c.sysmetic.domain.strategy.dto.PageReplyResponseDto" +
            "(r.member.id, r.strategy.id, r.content) " +
            "FROM Reply r " +
            "WHERE r.strategy.id = :strategyId and r.statusCode = :statusCode")
    Page<PageReplyResponseDto> findPageByStrategyIdAndStatusCode(
            @Param("strategyId") Long strategyId,
            @Param("statusCode") String statusCode,
            Pageable pageable
    );

    List<Reply> findByStrategyIdAndStatusCode(Long strategyId, String statusCode);

    Optional<Reply> findByIdAndMemberIdAndStatusCode(
            Long id, Long member_id, String statusCode
    );

    Optional<Reply> findByStrategyIdAndIdAndStatusCode(
            Long strategyId, Long id, String statusCode
    );
}
