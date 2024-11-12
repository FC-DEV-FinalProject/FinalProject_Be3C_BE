package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.PageReplyResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.ReplyDeleteRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.ReplyGetPageRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.ReplyPostRequestDto;
import com.be3c.sysmetic.domain.strategy.entity.Reply;
import com.be3c.sysmetic.global.common.response.PageResponseDto;

public interface ReplyService {
    PageResponseDto<PageReplyResponseDto> getMyReplyPage(Integer page);
    PageResponseDto<PageReplyResponseDto> getReplyPage(ReplyGetPageRequestDto replyGetPageRequestDto);
    boolean insertReply(ReplyPostRequestDto replyPostRequestDto);
    boolean deleteReply(ReplyDeleteRequestDto replyDeleteRequestDto);
}
