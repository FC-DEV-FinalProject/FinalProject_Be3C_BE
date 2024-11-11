package com.be3c.sysmetic.domain.member.repository;

import com.be3c.sysmetic.domain.member.dto.FolderGetResponseDto;
import com.be3c.sysmetic.domain.member.entity.FolderId;
import com.be3c.sysmetic.domain.member.entity.InterestStrategyId;
import com.be3c.sysmetic.domain.member.entity.InterestStrategy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InterestStrategyRepository extends JpaRepository<InterestStrategy, Long> {

    // 쿼리문 작성 필요.
    Page<FolderGetResponseDto> findPageByIdAndStatusCode(
            FolderId folder_id, String statusCode, Pageable pageable
    );

    Optional<InterestStrategy> findById(
            InterestStrategyId follow_strategy_id
    );

}
