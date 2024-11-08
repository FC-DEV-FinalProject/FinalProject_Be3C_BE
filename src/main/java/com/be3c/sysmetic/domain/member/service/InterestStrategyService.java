package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.dto.FolderGetRequestDto;
import com.be3c.sysmetic.domain.member.dto.FolderGetResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface InterestStrategyService {

    Page<FolderGetResponseDto> getInterestStrategyPage(FolderGetRequestDto folderGetRequestDto, Long userId);
}
