package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.exception.StrategyBadRequestException;
import com.be3c.sysmetic.domain.strategy.exception.StrategyExceptionMessage;
import com.be3c.sysmetic.domain.strategy.repository.StrategyRepository;
import com.be3c.sysmetic.domain.strategy.repository.StrategyStockReferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@Service
public class DeleteStrategyServiceImpl implements DeleteStrategyService {
    private final StrategyRepository strategyRepository;
    private final StrategyStockReferenceRepository strategyStockReferenceRepository;

    @Transactional
    @Override
    public void deleteStrategy(Long id) {
        if(id == null) throw new StrategyBadRequestException(StrategyExceptionMessage.INVALID_VALUE.getMessage());

        // DB 존재 검증
        strategyRepository.findById(id).orElseThrow(() -> new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage()));

        // 교차 테이블 DB 삭제
        strategyStockReferenceRepository.deleteByStrategyId(id);

        // DB 삭제
        strategyRepository.deleteById(id);
    }
}