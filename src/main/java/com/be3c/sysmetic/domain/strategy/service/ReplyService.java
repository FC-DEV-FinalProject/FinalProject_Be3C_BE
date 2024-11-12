package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.PageReplyResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.ReplyDeleteRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.ReplyPostRequestDto;
import com.be3c.sysmetic.domain.strategy.entity.Reply;
import com.be3c.sysmetic.global.common.response.PageResponseDto;

public interface ReplyService {
    PageResponseDto<PageReplyResponseDto> getReplyPage(Integer page);
    boolean insertReply(ReplyPostRequestDto replyPostRequestDto);
    boolean deleteReply(ReplyDeleteRequestDto replyDeleteRequestDto);
}
