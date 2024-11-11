package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.dto.FolderGetRequestDto;
import com.be3c.sysmetic.domain.member.dto.FolderGetResponseDto;
import com.be3c.sysmetic.domain.member.dto.FollowPostRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.client.HttpStatusCodeException;

public interface InterestStrategyService {

    Page<FolderGetResponseDto> getInterestStrategyPage(FolderGetRequestDto folderGetRequestDto, Long userId) throws HttpStatusCodeException;

    boolean follow(FollowPostRequestDto followPostRequestDto, Long userId);
}
