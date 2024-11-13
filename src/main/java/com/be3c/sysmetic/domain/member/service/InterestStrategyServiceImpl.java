package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.dto.FolderGetRequestDto;
import com.be3c.sysmetic.domain.member.dto.FolderGetResponseDto;
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

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Transactional
public class InterestStrategyServiceImpl implements InterestStrategyService {

    private final InterestStrategyRepository interestStrategyRepository;

    private final InterestStrategyLogRepository interestStrategyLogRepository;

    private final FolderRepository folderRepository;

    private final StrategyRepository strategyRepository;

    private final MemberRepository memberRepository;

    @Override
    public PageResponse<FolderGetResponseDto> getInterestStrategyPage(
            FolderGetRequestDto folderGetRequestDto,
            Long userId
    ) throws HttpStatusCodeException {
        if(userId == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }

        Pageable pageable = PageRequest.of(
                folderGetRequestDto.getPage(),
                10,
                Sort.by("modifiedAt"));

        Page<FolderGetResponseDto> folder_page = interestStrategyRepository
                .findPageByIdAndStatusCode(
                        userId,
                        folderGetRequestDto.getFolderId(),
                        Code.USING_STATE.getCode(),
                        pageable
                );

        if(folder_page.hasContent()) {
            return PageResponse.<FolderGetResponseDto>builder()
                    .totalPageCount(folder_page.getTotalPages())
                    .totalItemCount(folder_page.getTotalElements())
                    .itemCountPerPage(folder_page.getNumberOfElements())
                    .currentPage(folderGetRequestDto.getPage())
                    .list(folder_page.getContent())
                    .build();
        }

        throw new NoSuchElementException("잘못된 페이지 요청입니다.");
    }

    @Override
    public boolean follow(FollowPostRequestDto followPostRequestDto, Long userId) {
        if(followPostRequestDto.getStrategyId() == null || followPostRequestDto.getFolderId() == null) {
            throw new IllegalArgumentException("잘못된 요청입니다.");
        }

        Optional<InterestStrategy> interestStrategy = interestStrategyRepository
                .findByMemberIdAndFolderIdAndStrategyIdAndStatusCode(
                        userId,
                        followPostRequestDto.getFolderId(),
                        followPostRequestDto.getStrategyId(),
                        Code.USING_STATE.getCode()
                );

        Folder folder = folderRepository
                .findByMemberIdAndIdAndStatusCode(
                        userId,
                        followPostRequestDto.getFolderId(),
                        Code.USING_STATE.getCode())
                .orElseThrow(
                        () -> new EntityNotFoundException("폴더 아이디를 제대로 입력해주세요.")
                );

        if(!folder.getMember().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }

        if(interestStrategy.isEmpty()) {
            followStrategy(userId, folder, followPostRequestDto.getStrategyId());

            followStrategyLog(
                    userId,
                    followPostRequestDto.getFolderId(),
                    followPostRequestDto.getStrategyId(),
                    Code.FOLLOW.getCode()
            );

            return true;
        } else if(interestStrategy.get().getStatusCode().equals(Code.NOT_USING_STATE.getCode())) {
            interestStrategy.get().setStatusCode(Code.USING_STATE.getCode());

            followStrategyLog(
                    userId,
                    followPostRequestDto.getFolderId(),
                    followPostRequestDto.getStrategyId(),
                    Code.FOLLOW.getCode()
            );

            return true;
        }

        throw new IllegalArgumentException("잘못된 요청입니다.");
    }

    @Override
    public Map<Long, String> unfollow(FollowDeleteRequestDto followDeleteRequestDto, Long userId) {
        if(followDeleteRequestDto.getStrategyId() == null || followDeleteRequestDto.getFolderId() == null) {
            throw new IllegalArgumentException("잘못된 요청입니다.");
        }

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
                        Code.USING_STATE.getCode()
                );

        if(interestStrategy.isEmpty()) {
            return "해당 관심 전략을 찾을 수 없습니다.";
        }

        InterestStrategy find_is = interestStrategy.get();

        if(find_is.getStatusCode().equals(Code.FOLLOW.getCode())) {
            return "해당 관심 전략을 찾을 수 없습니다.";
        }

        find_is.setStatusCode(Code.UNFOLLOW.getCode());

        interestStrategyRepository.save(find_is);

        followStrategyLog(
                userId,
                folderId,
                strategyId,
                Code.UNFOLLOW.getCode()
        );

        return "";
    }

    private boolean followStrategy(Long userId, Folder folder, Long strategyId) {
        interestStrategyRepository.save(
                InterestStrategy.builder()
                        .member(memberRepository.findByIdAndUsingStatusCode(
                                userId,
                                Code.USING_STATE.getCode()
                        ).orElseThrow(() -> new EntityNotFoundException("해당 유저가 없습니다.")))
                        .folder(folder)
                        .strategy(strategyRepository
                                .findById(
                                        strategyId
                                ).orElseThrow(
                                        () -> new EntityNotFoundException("해당 전략이 없습니다.")
                                )
                        )
                        .build()
        );

        return true;
    }

    private boolean followStrategyLog(Long userId, Long folderId, Long strategyId, String LogCode) {
        interestStrategyLogRepository.save(
                InterestStrategyLog.builder()
                        .member(memberRepository.findByIdAndUsingStatusCode(
                                userId,
                                Code.USING_STATE.getCode()
                        ).orElseThrow(() -> new EntityNotFoundException("해당 유저를 찾을 수 없습니다.")))
                        .folder(folderRepository.findByMemberIdAndIdAndStatusCode(
                                userId,
                                folderId,
                                Code.USING_STATE.getCode()
                        ).orElseThrow(() -> new EntityNotFoundException("해당 폴더를 찾을 수 없습니다.")))
                        .strategy(strategyRepository.findByIdAndStatusCode(
                                strategyId,
                                Code.USING_STATE.getCode()
                        ).orElseThrow(() -> new EntityNotFoundException("해당 전략을 찾을 수 없습니다.")))
                        .LogCode(LogCode)
                        .build()
        );

        return true;
    }
}
