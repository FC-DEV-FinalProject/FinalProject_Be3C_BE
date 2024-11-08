package com.be3c.sysmetic.domain.member.repository;

import com.be3c.sysmetic.domain.member.dto.FolderGetResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface InterestStrategyRepository {

    // 쿼리문 작성 필요.
    Page<FolderGetResponseDto> findPageByFolderIdAndStatusCode(
            Long folderId,
            String StatusCode,
            Pageable pageable
    );

}
