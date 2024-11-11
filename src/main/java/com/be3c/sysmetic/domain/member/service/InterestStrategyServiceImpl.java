package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.dto.FolderGetRequestDto;
import com.be3c.sysmetic.domain.member.dto.FolderGetResponseDto;
import com.be3c.sysmetic.domain.member.dto.FollowDeleteRequestDto;
import com.be3c.sysmetic.domain.member.dto.FollowPostRequestDto;
import com.be3c.sysmetic.domain.member.entity.*;
import com.be3c.sysmetic.domain.member.repository.FolderRepository;
import com.be3c.sysmetic.domain.member.repository.InterestStrategyLogRepository;
import com.be3c.sysmetic.domain.member.repository.InterestStrategyRepository;
import com.be3c.sysmetic.domain.strategy.repository.StrategyRepository;
import com.be3c.sysmetic.global.common.Code;
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

import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Transactional
public class InterestStrategyServiceImpl implements InterestStrategyService {

    private final InterestStrategyRepository interestStrategyRepository;

    private final InterestStrategyLogRepository interestStrategyLogRepository;

    private final FolderRepository folderRepository;

    private final StrategyRepository strategyRepository;

    @Override
    public Page<FolderGetResponseDto> getInterestStrategyPage(
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

        Page<FolderGetResponseDto> folderPage = interestStrategyRepository
                .findPageByIdAndStatusCode(
                        new FolderId(userId, folderGetRequestDto.getFolderId()),
                        Code.USING_STATE.getCode(),
                        pageable
                );

        return null;
    }

    @Override
    public boolean follow(FollowPostRequestDto followPostRequestDto, Long userId) {
        if(followPostRequestDto.getStrategyId() == null || followPostRequestDto.getFolderId() == null) {
            throw new IllegalArgumentException("잘못된 요청입니다.");
        }

        Optional<InterestStrategy> interestStrategy = interestStrategyRepository
                .findById(
                        new InterestStrategyId(
                                userId,
                                followPostRequestDto.getFolderId(),
                                followPostRequestDto.getStrategyId()
                        )
                );

        Folder folder = folderRepository
                .findByIdAndStatusCode(
                        new FolderId(followPostRequestDto.getFolderId(),userId),
                        Code.USING_STATE.getCode())
                .orElseThrow(
                        () -> new EntityNotFoundException("폴더 아이디를 제대로 입력해주세요.")
                );

        InterestStrategyLogId interestStrategyLogId = new InterestStrategyLogId(
                userId,
                followPostRequestDto.getFolderId(),
                followPostRequestDto.getStrategyId()
        );

        if(!folder.getMember().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }

        if(interestStrategy.isEmpty()) {
            interestStrategyRepository.save(
                    InterestStrategy.builder()
                    .id(new InterestStrategyId(
                            userId,
                            followPostRequestDto.getFolderId(),
                            followPostRequestDto.getStrategyId()
                    ))
                    .folder(folder)
                    .strategy(strategyRepository
                            .findById(
                                    followPostRequestDto.getStrategyId()
                            ).orElseThrow(
                                    () -> new EntityNotFoundException("해당 전략이 없습니다.")
                            )
                    )
                    .build()
            );

            followStrategyLog(interestStrategyLogId, Code.FOLLOW.getCode());

            return true;
        } else if(interestStrategy.get().getStatusCode().equals(Code.NOT_USING_STATE.getCode())) {
            interestStrategy.get().setStatusCode(Code.USING_STATE.getCode());

            followStrategyLog(interestStrategyLogId, Code.FOLLOW.getCode());

            return true;
        }

        throw new IllegalArgumentException("잘못된 요청입니다.");
    }

    @Override
    public boolean unfollow(FollowDeleteRequestDto followDeleteRequestDto, Long userId) {
        if(followDeleteRequestDto.getStrategyId() == null || followDeleteRequestDto.getFolderId() == null) {
            throw new IllegalArgumentException("잘못된 요청입니다.");
        }

        InterestStrategyLogId interestStrategyLogId = new InterestStrategyLogId(
                userId,
                followDeleteRequestDto.getFolderId(),
                followDeleteRequestDto.getStrategyId()
        );

        InterestStrategy interestStrategy = interestStrategyRepository.findById(
                new InterestStrategyId(
                        userId,
                        followDeleteRequestDto.getFolderId(),
                        followDeleteRequestDto.getStrategyId()
                )
        ).orElseThrow(() -> new EntityNotFoundException("해당 전략을 팔로우 중이 아닙니다."));

        if(interestStrategy.getStatusCode().equals(Code.FOLLOW.getCode())) {
            throw new IllegalArgumentException("해당 전략을 팔로우 중이 아닙니다.");
        }

        interestStrategy.setStatusCode(Code.UNFOLLOW.getCode());

        interestStrategyRepository.save(interestStrategy);
        followStrategyLog(interestStrategyLogId, Code.UNFOLLOW.getCode());

        return true;
    }

    private boolean followStrategy(InterestStrategyId interestStrategyId, Folder folder, Long userId) {
        interestStrategyRepository.save(
                InterestStrategy.builder()
                        .id(interestStrategyId)
                        .folder(folder)
                        .strategy(strategyRepository
                                .findById(
                                        interestStrategyId.getStrategyId()
                                ).orElseThrow(
                                        () -> new EntityNotFoundException("해당 전략이 없습니다.")
                                )
                        )
                        .build()
        );

        return true;
    }

    private boolean followStrategyLog(InterestStrategyLogId interestStrategyLogId, String LogCode) {
        interestStrategyLogRepository.save(
                InterestStrategyLog.builder()
                        .interestStrategyLogId(
                                interestStrategyLogId
                        )
                        .LogCode(LogCode)
                        .build()
        );

        return true;
    }
}
