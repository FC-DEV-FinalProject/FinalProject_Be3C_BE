package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.MethodAndStockGetResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.MethodGetResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.StockGetResponseDto;
import com.be3c.sysmetic.domain.strategy.repository.MethodRepository;
import com.be3c.sysmetic.domain.strategy.repository.StockRepository;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.repository.StrategyRepository;
import com.be3c.sysmetic.global.common.Code;
import com.be3c.sysmetic.global.util.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.be3c.sysmetic.global.common.Code.*;
import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StrategyServiceImpl implements StrategyService {
    private final MethodRepository methodRepository;
    private final StockRepository stockRepository;

    private final StrategyRepository strategyRepository;

    private final SecurityUtils securityUtils;

    @Override
    public MethodAndStockGetResponseDto findMethodAndStock() {
        List<MethodGetResponseDto> methodList = methodRepository.findAllByStatusCode(Code.USING_STATE.getCode());
        if(methodList.isEmpty()) throw new EntityNotFoundException();

        List<StockGetResponseDto> stockList = stockRepository.findAllByStatusCode(Code.USING_STATE.getCode());
        if(stockList.isEmpty()) throw new EntityNotFoundException();

        return MethodAndStockGetResponseDto.builder()
                .methodList(methodList)
                .stockList(stockList)
                .build();
    }

    @Override
    public boolean privateStrategy(Long id) {
        Long userId = securityUtils.getUserIdInSecurityContext();
        Strategy strategy = strategyRepository.findByIdAndTraderIdAndStatusCode(id, userId, OPEN_STRATEGY.getCode()).orElseThrow(EntityNotFoundException::new);

        strategy.setStatusCode(Code.CLOSE_STRATEGY.getCode());
        strategyRepository.save(strategy);
        return true;
    }
}
