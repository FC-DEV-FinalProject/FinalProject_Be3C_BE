package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.domain.strategy.dto.PageReplyResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.ReplyDeleteRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.ReplyPostRequestDto;
import com.be3c.sysmetic.domain.strategy.entity.Reply;
import com.be3c.sysmetic.domain.strategy.repository.ReplyRepository;
import com.be3c.sysmetic.domain.strategy.repository.StrategyRepository;
import com.be3c.sysmetic.domain.strategy.util.StrategyViewAuthorize;
import com.be3c.sysmetic.global.common.Code;
import com.be3c.sysmetic.global.common.response.PageResponse;
import com.be3c.sysmetic.global.util.SecurityUtils;
import com.be3c.sysmetic.global.util.file.dto.FileReferenceType;
import com.be3c.sysmetic.global.util.file.dto.FileRequest;
import com.be3c.sysmetic.global.util.file.service.FileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static com.be3c.sysmetic.global.common.Code.USING_STATE;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ReplyServiceImpl implements ReplyService{
    private final SecurityUtils securityUtils;
    private final FileService fileService;

    private final MemberRepository memberRepository;

    private final ReplyRepository replyRepository;

    private final StrategyRepository strategyRepository;

    private final StrategyViewAuthorize strategyViewAuthorize;

    @Override
    public PageResponse<PageReplyResponseDto> getMyReplyPage(Integer page) {
        Long userId = securityUtils.getUserIdInSecurityContext();

        Pageable pageable = PageRequest.of(
                page,
                10,
                Sort.by("createdAt").descending()
        );

        Page<PageReplyResponseDto> findReplyPage = replyRepository
                .findPageByMemberIdAndStatusCode(
                        userId,
                        USING_STATE.getCode(),
                        pageable
                );

        if(!findReplyPage.hasContent()) {
            return PageResponse.<PageReplyResponseDto>builder()
                    .totalPages(findReplyPage.getNumber())
                    .totalElement(findReplyPage.getTotalElements())
                    .currentPage(findReplyPage.getNumber())
                    .pageSize(findReplyPage.getSize())
                    .content(findReplyPage.getContent())
                    .build();
        }

        throw new NoSuchElementException("잘못된 페이지 요청입니다.");
    }

    @Override
    public PageResponse<PageReplyResponseDto> getReplyPage(Long strategyId, Integer page) {

        strategyViewAuthorize.Authorize(strategyRepository.findById(strategyId).orElseThrow(EntityNotFoundException::new));

        Pageable pageable = PageRequest.of(
                page,
                10,
                Sort.by("createdAt").descending()
        );

        Page<PageReplyResponseDto> findReplyPage = replyRepository
                .findPageByStrategyIdAndStatusCode(
                        strategyId,
                        USING_STATE.getCode(),
                        pageable
                );

        if(findReplyPage.hasContent()) {
            findReplyPage.getContent().forEach(reply -> {
                reply.setMemberProfilePath(
                        fileService.getFilePath(new FileRequest(FileReferenceType.MEMBER, reply.getMemberId()))
                );
            });

            return PageResponse.<PageReplyResponseDto>builder()
                    .totalElement(findReplyPage.getTotalElements())
                    .currentPage(findReplyPage.getNumber())
                    .pageSize(findReplyPage.getNumberOfElements())
                    .totalPages(findReplyPage.getTotalPages())
                    .content(findReplyPage.getContent())
                    .build();
        }

        throw new NoSuchElementException("잘못된 페이지 요청입니다.");
    }

    @Override
    public boolean insertReply(ReplyPostRequestDto replyPostRequestDto) {
        Long userId = securityUtils.getUserIdInSecurityContext();

        Reply reply = Reply.builder()
                .member(memberRepository
                        .findByIdAndUsingStatusCode(
                                userId,
                                USING_STATE.getCode()
                        ).orElseThrow(() -> new EntityNotFoundException("해당 멤버를 찾을 수 없습니다.")))
                .strategy(strategyRepository
                        .findByIdAndOpenStatusCode(
                                replyPostRequestDto.getStrategyId()
                        ).orElseThrow(() -> new EntityNotFoundException("해당 전략을 찾을 수 없습니다.")))
                .content(replyPostRequestDto.getContent())
                .statusCode(USING_STATE.getCode())
                .build();

        replyRepository.save(reply);

        return true;
    }

    @Override
    public boolean deleteReply(ReplyDeleteRequestDto replyDeleteRequestDto) {
        Long userId = securityUtils.getUserIdInSecurityContext();

        Member member = memberRepository
                .findByIdAndUsingStatusCode(
                        userId,
                        USING_STATE.getCode()
                ).orElseThrow(() -> new EntityNotFoundException("해당 멤버를 찾을 수 없습니다."));

        // 관리자도 댓글 삭제가 가능한가?

        Reply reply = replyRepository.findByStrategyIdAndIdAndStatusCode(
                replyDeleteRequestDto.getStrategyId(),
                replyDeleteRequestDto.getId(),
                USING_STATE.getCode()
        ).orElseThrow(() -> new EntityNotFoundException("해당 댓글을 찾을 수 없습니다."));

        reply.setStatusCode(Code.NOT_USING_STATE.getCode());

        replyRepository.save(reply);

        return true;
    }
}
