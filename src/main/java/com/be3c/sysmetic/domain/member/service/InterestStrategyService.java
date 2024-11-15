package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.dto.InterestStrategyGetRequestDto;
import com.be3c.sysmetic.domain.member.dto.InterestStrategyGetResponseDto;
import com.be3c.sysmetic.domain.member.dto.FollowDeleteRequestDto;
import com.be3c.sysmetic.domain.member.dto.FollowPostRequestDto;
import com.be3c.sysmetic.global.common.response.PageResponse;

import java.util.Map;

public interface InterestStrategyService {

    PageResponse<InterestStrategyGetResponseDto> getInterestStrategyPage(InterestStrategyGetRequestDto interestStrategyGetRequestDto);

    boolean follow(FollowPostRequestDto followPostRequestDto);
    Map<Long, String> unfollow(FollowDeleteRequestDto followDeleteRequestDto);
}
