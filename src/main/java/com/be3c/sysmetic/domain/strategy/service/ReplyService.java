package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.ReplyDeleteRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.ReplyPostRequestDto;
import com.be3c.sysmetic.domain.strategy.entity.Reply;

public interface ReplyService {
    boolean insertReply(ReplyPostRequestDto replyPostRequestDto);
    boolean deleteReply(ReplyDeleteRequestDto replyDeleteRequestDto);
}
