package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.AccountImageRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.AccountImageResponseDto;
import com.be3c.sysmetic.domain.strategy.entity.AccountImage;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.exception.StrategyBadRequestException;
import com.be3c.sysmetic.domain.strategy.exception.StrategyExceptionMessage;
import com.be3c.sysmetic.domain.strategy.repository.AccountImageRepository;
import com.be3c.sysmetic.domain.strategy.repository.StrategyRepository;
import com.be3c.sysmetic.global.common.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@Service
public class AccountImageServiceImpl implements AccountImageService {

    /*
    전략 실계좌 이미지 조회

    1. 클라이언트에서 실계좌 정보 조회 API 요청
    2. 트레이더, 전략에 일치하는 실계좌 페이징하여 조회
     */

    private final AccountImageRepository accountImageRepository;
    private final StrategyRepository strategyRepository;
    private final Integer size = 10;

    // 실계좌이미지 조회
    @Override
    public PageResponse<AccountImageResponseDto> findAccountImages(Long strategyId, Integer page) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AccountImageResponseDto> accountImageResponseDtoPage = accountImageRepository.findAllByStrategyIdOrderByAccountImageCreatedAt(strategyId, pageable).map(this::entityToDto);

        return PageResponse.<AccountImageResponseDto>builder()
                .currentPage(accountImageResponseDtoPage.getPageable().getPageNumber())
                .pageSize(accountImageResponseDtoPage.getPageable().getPageSize())
                .totalElement(accountImageResponseDtoPage.getTotalElements())
                .totalPages(accountImageResponseDtoPage.getTotalPages())
                .content(accountImageResponseDtoPage.getContent())
                .build();
    }

    // 실계좌이미지 삭제
    public void deleteAccountImage(Long accountImageId) {
        AccountImage accountImage = accountImageRepository.findById(accountImageId).orElseThrow(() -> new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage()));
        // todo. security context에서 회원 id 받아서 비교 필요.
        // if(accountImage.getCreatedBy() != id) throw new StrategyBadRequestException(StrategyExceptionMessage.INVALID_MEMBER.getMessage());

        accountImageRepository.deleteById(accountImageId);
    }

    // 실계좌이미지 등록
    @Transactional
    public void saveAccountImage(Long strategyId, List<AccountImageRequestDto> requestDtoList) {
        // todo. security context에서 회원 id 받아서 비교 필요.
        // if(accountImage.getCreatedBy() != id) throw new StrategyBadRequestException(StrategyExceptionMessage.INVALID_MEMBER.getMessage());

        List<AccountImage> accountImageList = requestDtoList.stream().map(requestDto -> AccountImage.builder()
                .title(requestDto.getTitle())
                .strategy(findStrategyByStrategyId(strategyId))
                .build()).collect(Collectors.toList());

        // todo. 이미지 파일 S3 업로드 로직 필요 -> 예슬님이 진행해 주실 예정입니다.
        // requestDtos.get(0).getImage();

        accountImageRepository.saveAll(accountImageList);
    }

    private AccountImageResponseDto entityToDto(AccountImage accountImage) {
        return AccountImageResponseDto.builder()
                .accountImageId(accountImage.getId())
                .title(accountImage.getTitle())
                // .imageUrl() todo. 파일 DB 조인 필요.
                .build();
    }

    private Strategy findStrategyByStrategyId(Long strategyId) {
        return strategyRepository.findById(strategyId).orElseThrow(() -> new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage()));
    }

}
