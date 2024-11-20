package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.MethodAndStockGetResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.MethodGetResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.StockGetResponseDto;
import com.be3c.sysmetic.domain.strategy.repository.MethodRepository;
import com.be3c.sysmetic.domain.strategy.repository.StockRepository;
import com.be3c.sysmetic.global.common.Code;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class StrategyServiceImpl implements StrategyService {
    private final MethodRepository methodRepository;
    private final StockRepository stockRepository;

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
}
