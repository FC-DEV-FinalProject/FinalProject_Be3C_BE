package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.strategy.dto.ReplyPostRequestDto;
import com.be3c.sysmetic.domain.strategy.entity.Reply;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.repository.MemberRepository;
import com.be3c.sysmetic.domain.strategy.repository.ReplyRepository;
import com.be3c.sysmetic.domain.strategy.repository.StrategyRepository;
import com.be3c.sysmetic.global.common.Code;
import com.be3c.sysmetic.global.util.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ReplyServiceImpl implements ReplyService{
    private final SecurityUtils securityUtils;

    private final MemberRepository memberRepository;

    private final ReplyRepository replyRepository;

    private final StrategyRepository strategyRepository;

    @Override
    public boolean insertReply(ReplyPostRequestDto replyPostRequestDto) {
        Long userId = securityUtils.getUserIdInSecurityContext();

        Member member = memberRepository
                .findByIdAndStatusCode(
                        userId,
                        Code.USING_STATE.getCode()
                ).orElseThrow(() -> new EntityNotFoundException("해당 멤버를 찾을 수 없습니다."));

        Strategy strategy = strategyRepository
                .findByIdAndStatusCode(
                        replyPostRequestDto.getStrategyId(),
                        Code.USING_STATE.getCode()
                ).orElseThrow(() -> new EntityNotFoundException("해당 전략을 찾을 수 없습니다."));

        Reply reply = Reply.builder()
                .member(member)
                .strategy(strategy)
                .content(replyPostRequestDto.getContent())
                .statusCode(Code.USING_STATE.getCode())
                .build();

        replyRepository.save(reply);

        return true;
    }
}
