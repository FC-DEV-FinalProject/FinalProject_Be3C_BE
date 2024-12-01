package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.AccountImageRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.AccountImageResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.StrategyStatusCode;
import com.be3c.sysmetic.domain.strategy.entity.AccountImage;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.exception.StrategyBadRequestException;
import com.be3c.sysmetic.domain.strategy.exception.StrategyExceptionMessage;
import com.be3c.sysmetic.domain.strategy.repository.AccountImageRepository;
import com.be3c.sysmetic.domain.strategy.repository.StrategyRepository;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import com.be3c.sysmetic.global.common.response.PageResponse;
import com.be3c.sysmetic.global.util.SecurityUtils;
import com.be3c.sysmetic.global.util.file.dto.FileReferenceType;
import com.be3c.sysmetic.global.util.file.dto.FileRequest;
import com.be3c.sysmetic.global.util.file.exception.FileNotFoundException;
import com.be3c.sysmetic.global.util.file.service.FileServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
    private final SecurityUtils securityUtils;
    private final Integer size = 10;
    private final FileServiceImpl fileServiceImpl;

    // 실계좌이미지 조회 - PUBLIC 상태인 전략의 실계좌이미지 조회
    @Override
    public PageResponse<AccountImageResponseDto> findAccountImages(Long strategyId, Integer page) {
        Pageable pageable = PageRequest.of(page, size);

        // 전략 상태 PUBLIC 여부 검증
        Strategy strategy = strategyRepository.findById(strategyId).orElseThrow(() ->
                new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage(), ErrorCode.NOT_FOUND));

        String userRole = securityUtils.getUserRoleInSecurityContext();

        if (strategy.getStatusCode().equals("NOT_USING_STATE")) {
            // 1. 상태 코드가 NOT_USING_STATE인 경우 - 실패 (삭제된 전략)
            throw new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage(), ErrorCode.NOT_FOUND);
        } else if (strategy.getStatusCode().equals("PUBLIC")) {
            // 2. 상태 코드가 PUBLIC이면 모든 요청에 대해 성공
        } else if (strategy.getTrader().getId().equals(securityUtils.getUserIdInSecurityContext()) ||
                "USER_MANAGER".equals(userRole) ||
                "TRADER_MANAGER".equals(userRole) ||
                "ADMIN".equals(userRole)
        ) {
            // 3. 상태 코드가 NOT_USING_STATE, PUBLIC이 아니면서 트레이더 ID가 일치하거나 사용자 역할이 MANAGER인 경우 - 성공
        } else {
            // 4. 나머지 경우 - 실패
            throw new StrategyBadRequestException(StrategyExceptionMessage.INVALID_MEMBER.getMessage(), ErrorCode.NOT_FOUND);
        }

        Page<AccountImageResponseDto> accountImageResponseDtoPage = accountImageRepository
                .findAllByStrategyIdOrderByAccountImageCreatedAt(strategyId, pageable)
                .map(this::entityToDto);

        if(accountImageResponseDtoPage.hasContent()) {
            return PageResponse.<AccountImageResponseDto>builder()
                    .currentPage(accountImageResponseDtoPage.getPageable().getPageNumber())
                    .pageSize(accountImageResponseDtoPage.getPageable().getPageSize())
                    .totalElement(accountImageResponseDtoPage.getTotalElements())
                    .totalPages(accountImageResponseDtoPage.getTotalPages())
                    .content(accountImageResponseDtoPage.getContent())
                    .build();
        }

        throw new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage(), ErrorCode.NOT_FOUND);
    }

    /*
    실계좌이미지 조회
    1) 트레이더
    본인의 전략이면서 공개, 비공개, 승인대기 상태의 전략 조회 가능
    2) 관리자
    모든 상태의 전략 조회 가능
     */
    @Override
    public PageResponse<AccountImageResponseDto> findTraderAccountImages(Long strategyId, Integer page) {
        Pageable pageable = PageRequest.of(page, size);

        String userRole = securityUtils.getUserRoleInSecurityContext();

        // trader일 경우, 본인의 전략인지 검증
        if(userRole.equals("TRADER")) {
            validUser(strategyId);
        }

        // member일 경우, 권한 없음 처리
        if(userRole.equals("USER")) {
            throw new StrategyBadRequestException(StrategyExceptionMessage.INVALID_STATUS.getMessage(), ErrorCode.FORBIDDEN);
        }

        Strategy strategy = strategyRepository.findById(strategyId).orElseThrow(() ->
                new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage(), ErrorCode.NOT_FOUND));

        // 전략 상태 NOT_USING_STATE 일 경우 예외 처리
        if(!strategy.getStatusCode().equals(StrategyStatusCode.NOT_USING_STATE.name())) {
            throw new StrategyBadRequestException(StrategyExceptionMessage.INVALID_STATUS.getMessage(), ErrorCode.DISABLED_DATA_STATUS);
        }

        Page<AccountImageResponseDto> accountImageResponseDtoPage = accountImageRepository
                .findAllByStrategyIdOrderByAccountImageCreatedAt(strategyId, pageable)
                .map(this::entityToDto);

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
        AccountImage accountImage = accountImageRepository.findById(accountImageId).orElseThrow(() ->
                new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage(), ErrorCode.NOT_FOUND));

        validUser(accountImage.getStrategy().getTrader().getId());

        // 파일 삭제
        fileServiceImpl.deleteFile(new FileRequest(FileReferenceType.ACCOUNT_IMAGE, accountImageId));

        accountImageRepository.deleteById(accountImageId);
    }

    // 실계좌이미지 등록
    @Transactional
    public void saveAccountImage(Long strategyId, List<AccountImageRequestDto> requestDtoList) {
        Strategy savedStrategy = strategyRepository.findById(strategyId).orElseThrow(() ->
                new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage(), ErrorCode.NOT_FOUND));

        validUser(savedStrategy.getTrader().getId());

        List<AccountImage> accountImageList = requestDtoList.stream().map(requestDto ->
                AccountImage.builder()
                        .title(requestDto.getTitle())
                        .strategy(findStrategyByStrategyId(strategyId))
                        .build()).collect(Collectors.toList());

        accountImageRepository.saveAll(accountImageList);

        // 파일 등록
        for(int i=0; i<accountImageList.size(); i++) {
            FileRequest fileRequest = new FileRequest(FileReferenceType.ACCOUNT_IMAGE, accountImageList.get(i).getId());
            fileServiceImpl.uploadImage(requestDtoList.get(i).getImage(), fileRequest);
        }
    }

    // 현재 로그인한 유저와 전략 업로드한 유저가 일치하는지 검증
    private void validUser(Long traderId) {
        if(!securityUtils.getUserIdInSecurityContext().equals(traderId)) {
            throw new StrategyBadRequestException(StrategyExceptionMessage.INVALID_MEMBER.getMessage(), ErrorCode.FORBIDDEN);
        }
    }

    private AccountImageResponseDto entityToDto(AccountImage accountImage) {
        try {
            return AccountImageResponseDto.builder()
                    .accountImageId(accountImage.getId())
                    .title(accountImage.getTitle())
                    .imageUrl(fileServiceImpl.getFilePath(
                            new FileRequest(FileReferenceType.ACCOUNT_IMAGE, accountImage.getId()))) // 파일 조회
                    .build();
        } catch (FileNotFoundException e) {
            return AccountImageResponseDto.builder()
                    .accountImageId(accountImage.getId())
                    .title(accountImage.getTitle())
                    .imageUrl(null) // 파일 조회
                    .build();
        }
    }

    private Strategy findStrategyByStrategyId(Long strategyId) {
        return strategyRepository.findById(strategyId).orElseThrow(() ->
                new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage(), ErrorCode.NOT_FOUND));
    }

}
