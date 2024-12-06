package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.member.repository.InterestStrategyRepository;
import com.be3c.sysmetic.domain.strategy.dto.*;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.repository.StrategyRepository;
import com.be3c.sysmetic.domain.strategy.util.StockGetter;
import com.be3c.sysmetic.global.common.response.PageResponse;
import com.be3c.sysmetic.global.util.SecurityUtils;
import com.be3c.sysmetic.global.util.file.dto.FileReferenceType;
import com.be3c.sysmetic.global.util.file.dto.FileRequest;
import com.be3c.sysmetic.global.util.file.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class StrategySearchServiceImpl implements StrategySearchService {

    private final StrategyRepository strategyRepository;
    private final StockGetter stockGetter;
    private final FileService fileService;
    private final SecurityUtils securityUtils;
    private final InterestStrategyRepository interestStrategyRepository;

    private final int PAGE_SIZE = 10;

    /*
        searchConditions : 상세 조건 검색 결과로 나온 전략을 StrategySearchResponseDto로 변환해서 PageReposne에 담아 반환
    */
    @Override
    public PageResponse<StrategySearchResponseDto> searchConditions(
            Integer pageNum, StrategySearchRequestDto strategySearchRequestDto) {

        Pageable pageable = PageRequest.of(pageNum, PAGE_SIZE);

        Page<Strategy> sPage = strategyRepository.searchByConditions(pageable, strategySearchRequestDto);

        List<StrategySearchResponseDto> strategyList = sPage.getContent()
                .stream()
                .map(s -> {
                    List<String> stockIconPaths = new ArrayList<>();

                    stockGetter.getStocks(s.getId()).getStockIds().forEach(stockId ->
                            stockIconPaths.add(fileService.getFilePathNullable(new FileRequest(FileReferenceType.STOCK, stockId)))
                    );

                    return StrategySearchResponseDto.builder()
                                    .strategyId(s.getId())
                                    .traderId(s.getTrader().getId())
                                    .traderNickname(s.getTrader().getNickname())
                                    .traderProfileImage(fileService.getFilePathNullable(new FileRequest(FileReferenceType.MEMBER, s.getTrader().getId())))
                                    .methodIconPath(fileService.getFilePathNullable(new FileRequest(FileReferenceType.METHOD, s.getMethod().getId())))
                                    .stockIconPath(stockIconPaths)
                                    .name(s.getName())
                                    .cycle(s.getCycle())
                                    .isFollow(false)
                                    .stockList(stockGetter.getStocks(s.getId()))
                                    .accumulatedProfitLossRate(s.getAccumulatedProfitLossRate())
                                    .mdd(s.getMdd())
                                    .smScore(s.getSmScore())
                                    .build();
    }).toList();

        try {
            Long userId = securityUtils.getUserIdInSecurityContext();

            HashSet<Long> interestStrategyList = interestStrategyRepository.findAllByMemberId(userId);

            strategyList.forEach(strategy -> {
                        if(interestStrategyList.contains(strategy.getStrategyId())) {
                            strategy.setIsFollow(true);
                        }
                    }
            );
        } catch (UsernameNotFoundException | AuthenticationCredentialsNotFoundException e) {
        }

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
    public PageResponse<StrategyAlgorithmResponseDto> searchAlgorithm(Integer pageNum, StrategyAlgorithmOption algorithm) {

        Pageable pageable = PageRequest.of(pageNum, PAGE_SIZE);

        Page<Strategy> sPage = strategyRepository.searchByAlgorithm(pageable, String.valueOf(algorithm));

        if (String.valueOf(algorithm).equals("DEFENSIVE")) {
            sPage = strategyRepository.findDefensiveStrategies(pageable);
        }

        List<StrategyAlgorithmResponseDto> strategyList = sPage.getContent()
                .stream()
                .map(s -> {
                    List<String> stockIconPaths = new ArrayList<>();

                    stockGetter.getStocks(s.getId()).getStockIds().forEach(stockId ->
                            stockIconPaths.add(fileService.getFilePathNullable(new FileRequest(FileReferenceType.STOCK, stockId)))
                    );

                    return StrategyAlgorithmResponseDto.builder()
                            .algorithm(algorithm)
                            .id(s.getId())
                            .traderId(s.getTrader().getId())
                            .traderNickname(s.getTrader().getNickname())
                            .traderProfileImage(fileService.getFilePathNullable(new FileRequest(FileReferenceType.MEMBER, s.getTrader().getId())))
                            .methodIconPath(fileService.getFilePathNullable(new FileRequest(FileReferenceType.METHOD, s.getMethod().getId())))
                            .stockIconPath(stockIconPaths)
                            .name(s.getName())
                            .isFollow(false)
                            .cycle(s.getCycle())
                            .accumulatedProfitLossRate(s.getAccumulatedProfitLossRate())
                            .mdd(s.getMdd())
                            .smScore(s.getSmScore())
                            .build();
                }).toList();

        try {
            Long userId = securityUtils.getUserIdInSecurityContext();

            HashSet<Long> interestStrategyList = interestStrategyRepository.findAllByMemberId(userId);

            strategyList.forEach(strategy -> {
                        if(interestStrategyList.contains(strategy.getId())) {
                            strategy.setIsFollow(true);
                        }
                    }
            );
        } catch (UsernameNotFoundException | AuthenticationCredentialsNotFoundException e) {
        }

        return PageResponse.<StrategyAlgorithmResponseDto>builder()
                .currentPage(sPage.getNumber())
                .currentPage(sPage.getNumber())
                .pageSize(sPage.getSize())
                .totalElement(sPage.getTotalElements())
                .totalPages(sPage.getTotalPages())
                .content(strategyList)
                .build();
    }
}
