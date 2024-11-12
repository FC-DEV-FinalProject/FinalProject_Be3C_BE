package com.be3c.sysmetic.domain.member.repository;

import com.be3c.sysmetic.domain.member.dto.FolderGetResponseDto;
import com.be3c.sysmetic.domain.member.entity.InterestStrategy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InterestStrategyRepository extends JpaRepository<InterestStrategy, Long> {

    @Query("SELECT new com.be3c.sysmetic.domain.member.dto.FolderGetResponseDto(" +
            "s.id, s.name, m.name, null, s.followerCount, ss.accumulatedProfitRate, s.smScore, ss.maximumCapitalReductionRate) " +
            "FROM InterestStrategy is " +
            "JOIN is.strategy s " +
            "JOIN s.trader m " +
            "JOIN StrategyStatistics ss ON s.id = ss.id " +
            "WHERE s.statusCode = :statusCode " +
            "AND is.member.id = :memberId " +
            "AND is.folder.id = :folderId")
    Page<FolderGetResponseDto> findPageByIdAndStatusCode(
            Long memberId, Long folderId, String statusCode, Pageable pageable
    );

    Optional<InterestStrategy> findByMemberIdAndFolderIdAndStrategyIdAndStatusCode(
            Long memberId, Long folderId, Long strategyId, String statusCode
    );

}
