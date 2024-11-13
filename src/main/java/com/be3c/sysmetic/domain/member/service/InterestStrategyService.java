package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.dto.FolderGetRequestDto;
import com.be3c.sysmetic.domain.member.dto.FolderGetResponseDto;
import com.be3c.sysmetic.domain.member.dto.FollowDeleteRequestDto;
import com.be3c.sysmetic.domain.member.dto.FollowPostRequestDto;
import com.be3c.sysmetic.global.common.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.List;
import java.util.Map;

public interface InterestStrategyService {

    PageResponse<FolderGetResponseDto> getInterestStrategyPage(FolderGetRequestDto folderGetRequestDto, Long userId) throws HttpStatusCodeException;

    boolean follow(FollowPostRequestDto followPostRequestDto, Long userId);
    Map<Long, String> unfollow(FollowDeleteRequestDto followDeleteRequestDto, Long userId);
}
