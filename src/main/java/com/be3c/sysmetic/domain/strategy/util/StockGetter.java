package com.be3c.sysmetic.domain.strategy.util;

import com.be3c.sysmetic.domain.strategy.dto.StockListDto;
import com.be3c.sysmetic.domain.strategy.entity.Stock;
import com.be3c.sysmetic.domain.strategy.entity.StrategyStockReference;
import com.be3c.sysmetic.domain.strategy.repository.StockRepository;
import com.be3c.sysmetic.domain.strategy.repository.StrategyStockReferenceRepository;
import com.be3c.sysmetic.global.util.file.dto.FileReferenceType;
import com.be3c.sysmetic.global.util.file.dto.FileRequest;
import com.be3c.sysmetic.global.util.file.exception.FileNotFoundException;
import com.be3c.sysmetic.global.util.file.service.FileService;
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
    private final FileService fileService;

    public StockListDto getStocks(Long strategyId) {
        HashSet<Long> idSet = new HashSet<>();
        HashSet<String> nameSet = new HashSet<>();
        HashSet<String> pathSet = new HashSet<>();

        List<StrategyStockReference> references = strategyStockReferenceRepository.findAllByStrategyId(strategyId);

        for (StrategyStockReference ref : references) {
            Stock stock = ref.getStock();
            idSet.add(stock.getId());
            nameSet.add(stock.getName());
            try {
                pathSet.add(fileService.getFilePath(new FileRequest(FileReferenceType.STOCK, stock.getId())));
            } catch (FileNotFoundException e) {
                pathSet.add(null);
            }
        }

        return StockListDto.builder()
                .stockIds(idSet)
                .stockNames(nameSet)
                .stockIconPath(pathSet)
                .build();
    }
}
