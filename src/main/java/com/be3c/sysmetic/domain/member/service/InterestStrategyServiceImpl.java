package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.dto.InterestStrategyGetRequestDto;
import com.be3c.sysmetic.domain.member.dto.InterestStrategyGetResponseDto;
import com.be3c.sysmetic.domain.member.dto.FollowDeleteRequestDto;
import com.be3c.sysmetic.domain.member.dto.FollowPostRequestDto;
import com.be3c.sysmetic.domain.member.entity.*;
import com.be3c.sysmetic.domain.member.repository.FolderRepository;
import com.be3c.sysmetic.domain.member.repository.InterestStrategyLogRepository;
import com.be3c.sysmetic.domain.member.repository.InterestStrategyRepository;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.domain.strategy.repository.StrategyRepository;
import com.be3c.sysmetic.global.common.Code;
import com.be3c.sysmetic.global.common.response.PageResponse;
import com.be3c.sysmetic.global.util.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static com.be3c.sysmetic.global.common.Code.*;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Transactional
public class InterestStrategyServiceImpl implements InterestStrategyService {

    private final InterestStrategyRepository interestStrategyRepository;

    private final InterestStrategyLogRepository interestStrategyLogRepository;

    private final FolderRepository folderRepository;

    private final StrategyRepository strategyRepository;

    private final MemberRepository memberRepository;

    private final SecurityUtils securityUtils;

    @Override
    public PageResponse<InterestStrategyGetResponseDto> getInterestStrategyPage(
            InterestStrategyGetRequestDto interestStrategyGetRequestDto
    ) throws HttpStatusCodeException {
        Long userId = securityUtils.getUserIdInSecurityContext();
        userId = 1L;

        Pageable pageable = PageRequest.of(
                interestStrategyGetRequestDto.getPage(),
                10,
                Sort.by("modifiedAt").descending());

        Page<InterestStrategyGetResponseDto> folderPage = interestStrategyRepository
                .findPageByIdAndStatusCode(
                        userId,
                        interestStrategyGetRequestDto.getFolderId(),
                        USING_STATE.getCode(),
                        pageable
                );

        if(folderPage.hasContent()) {
            return PageResponse.<InterestStrategyGetResponseDto>builder()
                    .totalPages(folderPage.getTotalPages())
                    .totalElement(folderPage.getTotalElements())
                    .pageSize(folderPage.getNumberOfElements())
                    .currentPage(interestStrategyGetRequestDto.getPage())
                    .content(folderPage.getContent())
                    .build();
        }

        throw new NoSuchElementException();
    }

    @Override
    public boolean follow(FollowPostRequestDto followPostRequestDto) {
        Long userId = securityUtils.getUserIdInSecurityContext();

        Optional<InterestStrategy> interestStrategy = interestStrategyRepository
                .findByMemberIdAndFolderIdAndStrategyIdAndStatusCode(
                        userId,
                        followPostRequestDto.getFolderId(),
                        followPostRequestDto.getStrategyId(),
                        USING_STATE.getCode()
                );

        Folder folder = folderRepository
                .findByMemberIdAndIdAndStatusCode(
                        userId,
                        followPostRequestDto.getFolderId(),
                        USING_STATE.getCode())
                .orElseThrow(EntityNotFoundException::new);

        if(interestStrategy.isEmpty()) {
            followStrategy(userId, folder, followPostRequestDto.getStrategyId());

            followStrategyLog(
                    userId,
                    followPostRequestDto.getFolderId(),
                    followPostRequestDto.getStrategyId(),
                    FOLLOW.getCode()
            );
            return true;
        } else if(interestStrategy.get().getStatusCode().equals(Code.NOT_USING_STATE.getCode())) {
            interestStrategy.get().setStatusCode(USING_STATE.getCode());

            followStrategyLog(
                    userId,
                    followPostRequestDto.getFolderId(),
                    followPostRequestDto.getStrategyId(),
                    FOLLOW.getCode()
            );

            return true;
        }

        throw new EntityNotFoundException();
    }

    @Override
    public Map<Long, String> unfollow(FollowDeleteRequestDto followDeleteRequestDto) {
        Long userId = securityUtils.getUserIdInSecurityContext();

        Map<Long, String> fail_unfollow = new HashMap<>();

        for(Long unfollowId : followDeleteRequestDto.getStrategyId()) {
            String unFollowResult = unFollowStrategy(userId, followDeleteRequestDto.getFolderId(), unfollowId);
            if(!unFollowResult.isEmpty()) {
                fail_unfollow.put(unfollowId, unFollowResult);
            }
        }

        return fail_unfollow;
    }

    private String unFollowStrategy(Long userId, Long folderId, Long strategyId) {
        Optional<InterestStrategy> interestStrategy = interestStrategyRepository
                .findByMemberIdAndFolderIdAndStrategyIdAndStatusCode(
                        userId,
                        folderId,
                        strategyId,
                        USING_STATE.getCode()
                );

        if(interestStrategy.isEmpty()) {
            return "해당 관심 전략을 찾을 수 없습니다.";
        }

        InterestStrategy find_is = interestStrategy.get();

        if(find_is.getStatusCode().equals(FOLLOW.getCode())) {
            return "해당 관심 전략을 찾을 수 없습니다.";
        }

        find_is.setStatusCode(UNFOLLOW.getCode());

        interestStrategyRepository.save(find_is);

        followStrategyLog(
                userId,
                folderId,
                strategyId,
                UNFOLLOW.getCode()
        );

        return "";
    }

    private void followStrategy(Long userId, Folder folder, Long strategyId) {
        interestStrategyRepository.save(
                InterestStrategy.builder()
                        .member(memberRepository.findByIdAndUsingStatusCode(
                                userId,
                                USING_STATE.getCode()
                        ).orElseThrow(EntityNotFoundException::new))
                        .folder(folder)
                        .strategy(strategyRepository
                                .findById(strategyId)
                                .orElseThrow(EntityNotFoundException::new)
                        )
                        .build()
        );

    }

    private void followStrategyLog(Long userId, Long folderId, Long strategyId, String LogCode) {
        interestStrategyLogRepository.save(
                InterestStrategyLog.builder()
                        .member(memberRepository.findByIdAndUsingStatusCode(
                                        userId,
                                        USING_STATE.getCode()
                                ).orElseThrow(EntityNotFoundException::new))
                        .folder(folderRepository.findByMemberIdAndIdAndStatusCode(
                                        userId,
                                        folderId,
                                        USING_STATE.getCode()
                                ).orElseThrow(EntityNotFoundException::new))
                        .strategy(strategyRepository.findByIdAndStatusCode(
                                        strategyId,
                                        USING_STATE.getCode()
                                ).orElseThrow(EntityNotFoundException::new))
                        .LogCode(LogCode)
                        .build()
        );

    }
}
