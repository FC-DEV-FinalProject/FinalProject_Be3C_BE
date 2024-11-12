package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.AccountImageResponseDto;
import com.be3c.sysmetic.domain.strategy.entity.AccountImage;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.exception.StrategyBadRequestException;
import com.be3c.sysmetic.domain.strategy.exception.StrategyExceptionMessage;
import com.be3c.sysmetic.domain.strategy.repository.AccountImageRepository;
import com.be3c.sysmetic.domain.strategy.repository.StrategyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
    private final int size = 10;

    @Override
    public Page<AccountImageResponseDto> findAccountImage(Long strategyId, int page) {
        Pageable pageable = PageRequest.of(page, size);

        return accountImageRepository.findByStrategyId(strategyId, pageable).map(accountImage -> AccountImageResponseDto.builder()
                .accountImageId(accountImage.getId())
                .title(accountImage.getTitle())
                .imageUrl("") // TODO 추후 파일 테이블에서 조인 필요
                .createdAt(accountImage.getCreatedDate())
                .build());
    }

    public void deleteAccountImage(Long accountImageId) {
        accountImageRepository.deleteById(accountImageId);
    }

    public void saveAccountImage(Long strategyId, String title) {
        // TODO 추후 파일 service 사용 필요
        AccountImage accountImage = AccountImage.builder()
                .title(title)
                .strategy(findStrategyByStrategyId(strategyId))
                .build();
        accountImageRepository.save(accountImage);
    }

    private Strategy findStrategyByStrategyId(Long strategyId) {
        return strategyRepository.findById(strategyId).orElseThrow(() -> new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage()));
    }


}
