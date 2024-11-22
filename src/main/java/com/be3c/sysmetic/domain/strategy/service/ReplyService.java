package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.PageReplyResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.ReplyDeleteRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.ReplyPostRequestDto;
import com.be3c.sysmetic.global.common.response.PageResponse;

public interface ReplyService {
    PageResponse<PageReplyResponseDto> getMyReplyPage(Integer page);
    PageResponse<PageReplyResponseDto> getReplyPage(Long strategyId, Integer page);
    boolean insertReply(ReplyPostRequestDto replyPostRequestDto);
    boolean deleteReply(ReplyDeleteRequestDto replyDeleteRequestDto);
}
