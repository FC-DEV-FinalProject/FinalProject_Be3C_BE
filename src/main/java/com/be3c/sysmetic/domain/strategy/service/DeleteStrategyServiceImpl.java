package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.exception.StrategyBadRequestException;
import com.be3c.sysmetic.domain.strategy.exception.StrategyExceptionMessage;
import com.be3c.sysmetic.domain.strategy.repository.StrategyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@Service
public class DeleteStrategyServiceImpl implements DeleteStrategyService {
    private final StrategyRepository strategyRepository;

    @Override
    public void deleteStrategy(Long id) {
        if(id == null) throw new StrategyBadRequestException(StrategyExceptionMessage.INVALID_VALUE.getMessage());

        // DB 존재 검증
        strategyRepository.findById(id).orElseThrow(() -> new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage()));

        // DB 삭제
        strategyRepository.deleteById(id);
    }
}
