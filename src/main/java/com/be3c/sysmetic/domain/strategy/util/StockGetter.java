package com.be3c.sysmetic.domain.strategy.util;

import com.be3c.sysmetic.domain.strategy.dto.StockListDto;
import com.be3c.sysmetic.domain.strategy.entity.Stock;
import com.be3c.sysmetic.domain.strategy.entity.StrategyStockReference;
import com.be3c.sysmetic.domain.strategy.repository.StockRepository;
import com.be3c.sysmetic.domain.strategy.repository.StrategyStockReferenceRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.List;

@Component
@Getter
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class StockGetter {

    private final StrategyStockReferenceRepository strategyStockReferenceRepository;
    private final StockRepository stockRepository;

    // getMethods : 전략에 포함된 종목 가져오기
    public StockListDto getStocks(Long strategyId) {
        HashSet<Long> idSet = new HashSet<>();
        HashSet<String> nameSet = new HashSet<>();

        List<StrategyStockReference> references = strategyStockReferenceRepository.findAllByStrategyId(strategyId);

        for (StrategyStockReference ref : references) {
            Stock stock = ref.getStock();
            idSet.add(stock.getId());
            nameSet.add(stock.getName());
        }

        return StockListDto.builder()
                .stockIds(idSet)
                .stockNames(nameSet)
                .build();
    }
}
