package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.dto.FolderGetRequestDto;
import com.be3c.sysmetic.domain.member.dto.FolderGetResponseDto;
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

        if(interestStrategy.isEmpty()) {
            interestStrategyRepository.save(
                    InterestStrategy.builder()
                    .id(new InterestStrategyId(
                            userId,
                            followPostRequestDto.getFolderId(),
                            followPostRequestDto.getStrategyId()
                    ))
                    .folder(folderRepository
                            .findByIdAndStatusCode(
                                    new FolderId(followPostRequestDto.getFolderId(),userId),
                                    Code.USING_STATE.getCode())
                            .orElseThrow(
                                    () -> new EntityNotFoundException("폴더 아이디를 제대로 입력해주세요.")
                            ))
                    .strategy(strategyRepository
                            .findById(
                                    followPostRequestDto.getStrategyId()
                            ).orElseThrow(
                                    () -> new EntityNotFoundException("해당 전략이 없습니다.")
                            )
                    )
                    .build()
            );

            interestStrategyLogRepository.save(
                    InterestStrategyLog.builder()
                            .interestStrategyLogId(
                                    new InterestStrategyLogId(
                                            userId,
                                            followPostRequestDto.getFolderId(),
                                            followPostRequestDto.getStrategyId()
                                    )
                            )
                            .build()
            );

            return true;
        } else if(interestStrategy.get().getStatusCode().equals(Code.NOT_USING_STATE.getCode())) {
            interestStrategy.get().setStatusCode(Code.USING_STATE.getCode());

            interestStrategyLogRepository.save(
                    InterestStrategyLog.builder()
                            .interestStrategyLogId(
                                    new InterestStrategyLogId(
                                            userId,
                                            followPostRequestDto.getFolderId(),
                                            followPostRequestDto.getStrategyId()
                                    )
                            )
                            .build()
            );

            return true;
        }

        throw new IllegalArgumentException("잘못된 요청입니다.");
    }
}
