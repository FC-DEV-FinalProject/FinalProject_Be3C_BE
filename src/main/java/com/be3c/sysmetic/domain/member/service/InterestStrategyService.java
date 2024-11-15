package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.dto.*;
import com.be3c.sysmetic.global.common.response.PageResponse;

import java.util.Map;

public interface InterestStrategyService {

    PageResponse<InterestStrategyGetResponseDto> getInterestStrategyPage(InterestStrategyGetRequestDto interestStrategyGetRequestDto);

    boolean moveFolder(FollowPutRequestDto followPutRequestDto);
    boolean follow(FollowPostRequestDto followPostRequestDto);
    Map<Long, String> unfollow(FollowDeleteRequestDto followDeleteRequestDto);
}
