package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.StockListDto;
import com.be3c.sysmetic.domain.strategy.dto.StrategyAlgorithmResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.StrategySearchRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.StrategySearchResponseDto;
import com.be3c.sysmetic.domain.strategy.entity.Stock;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.entity.StrategyStockReference;
import com.be3c.sysmetic.domain.strategy.repository.StrategyRepository;
import com.be3c.sysmetic.domain.strategy.repository.StrategyStockReferenceRepository;
import com.be3c.sysmetic.global.common.response.PageResponse;
import com.be3c.sysmetic.global.util.doublehandler.DoubleHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class StrategySearchServiceImpl implements StrategySearchService {

    private final StrategyRepository strategyRepository;
    private final StrategyStockReferenceRepository strategyStockReferenceRepository;
    private final DoubleHandler doubleHandler;

    /*
        searchConditions : 상세 조건 검색 결과로 나온 전략을 StrategySearchResponseDto로 변환해서 PageReposne에 담아 반환
    */
    @Override
    public PageResponse<StrategySearchResponseDto> searchConditions(
            Integer pageNum, StrategySearchRequestDto strategySearchRequestDto) {
        // log.info("controller log=pageNum={}, strategySearchRequestDto={}", pageNum, strategySearchRequestDto);

        int pageSize = 10;
        // TODO 정렬 기준 필요
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        // Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Order.desc("accumProfitLossRate")));

        Page<Strategy> sPage = strategyRepository.searchByConditions(pageable, strategySearchRequestDto);

        if (sPage.getContent().isEmpty())
            throw new NoSuchElementException("상세 조건에 해당하는 검색 결과가 존재하지 않습니다.");

        List<StrategySearchResponseDto> strategyList = sPage.getContent()
                .stream()
                .map(s -> StrategySearchResponseDto.builder()
                        .strategyId(s.getId())
                        .traderId(s.getTrader().getId())
                        .traderNickname(s.getTrader().getNickname())
                        .methodId(s.getMethod().getId())
                        .methodName(s.getMethod().getName())
                        .name(s.getName())
                        .cycle(s.getCycle())
                        .stockList(getStocks(s.getId()))
                        .accumProfitLossRate(doubleHandler.cutDouble(s.getAccumProfitLossRate()))
                        .mdd(doubleHandler.cutDouble(s.getMdd()))
                        .smScore(doubleHandler.cutDouble(s.getSmScore()))
                        .build()
                )
                .toList();

        return PageResponse.<StrategySearchResponseDto>builder()
                .currentPage(sPage.getNumber())
                .pageSize(sPage.getSize())
                .totalElement(sPage.getNumberOfElements())
                .totalPages(sPage.getTotalPages())
                .content(strategyList)
                .build();
    }


    /*
        searchAlgorithm : 알고리즘별 전략 검색
    */
    @Override
    public PageResponse<StrategyAlgorithmResponseDto> searchAlgorithm(Integer pageNum, String type) {
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNum, pageSize);

        Page<Strategy> sPage = strategyRepository.searchByAlgorithm(pageable, type);

        if (sPage.getContent().isEmpty())
            throw new NoSuchElementException(type + "알고리즘에 해당하는 전략이 없습니다.");

        List<StrategyAlgorithmResponseDto> strategyList = sPage.getContent()
                .stream()
                .map(s -> StrategyAlgorithmResponseDto.builder()
                        .algorithmType(type)
                        .id(s.getId())
                        .traderId(s.getTrader().getId())
                        .traderNickname(s.getTrader().getNickname())
                        .methodId(s.getMethod().getId())
                        .methodName(s.getMethod().getName())
                        .stockList(getStocks(s.getId()))
                        .name(s.getName())
                        .cycle(s.getCycle())
                        .accumProfitLossRate(doubleHandler.cutDouble(s.getAccumProfitLossRate()))
                        .mdd(doubleHandler.cutDouble(s.getMdd()))
                        .smScore(doubleHandler.cutDouble(s.getSmScore()))
                        .build()
                )
                .toList();

        return PageResponse.<StrategyAlgorithmResponseDto>builder()
                .currentPage(sPage.getNumber())
                .currentPage(sPage.getNumber())
                .pageSize(sPage.getSize())
                .totalElement(sPage.getTotalElements())
                .totalPages(sPage.getTotalPages())
                .content(strategyList)
                .build();
    }

    // getMethods : 전략에 포함된 종목 가져오기
    private StockListDto getStocks(Long id) {
        HashSet<Long> idSet = new HashSet<>();
        HashSet<String> nameSet = new HashSet<>();

        List<StrategyStockReference> references = strategyStockReferenceRepository.findByStrategyId(id);

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
