package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.domain.strategy.dto.*;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.repository.StrategyListRepository;
import com.be3c.sysmetic.domain.strategy.repository.StrategyRepository;
import com.be3c.sysmetic.domain.strategy.util.DoubleHandler;
import com.be3c.sysmetic.domain.strategy.util.PathGetter;
import com.be3c.sysmetic.domain.strategy.util.StockGetter;
import com.be3c.sysmetic.global.common.response.PageResponse;
import com.be3c.sysmetic.global.util.file.dto.FileReferenceType;
import com.be3c.sysmetic.global.util.file.dto.FileRequest;
import com.be3c.sysmetic.global.util.file.exception.FileNotFoundException;
import com.be3c.sysmetic.global.util.file.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


@Service
@Transactional
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class StrategyListServiceImpl implements StrategyListService {

    private final StrategyListRepository strategyListRepository;
    private final MemberRepository memberRepository;
    private final FileService fileService;
    private final StockGetter stockGetter;
    private final PathGetter pathGetter;
    private final int PAGE_SIZE = 10;

    /*
        findStrategyPage : 메인 전략 목록 페이지 (수익률순 조회)
        Strategy 엔티티를 StrategyListDto로 반환
    */
    @Override
    public PageResponse<StrategyListDto> findStrategyPage(Integer pageNum) {

        Pageable pageable = PageRequest.of(pageNum, PAGE_SIZE);

        Page<Strategy> strategyListPage = strategyListRepository.findStrategiesOrderByAccumulatedProfitLossRate(pageable);

        if (!strategyListPage.hasContent()) throw new NoSuchElementException("전략 목록이 없습니다.");

        Page<StrategyListDto> result = strategyListPage.map(strategy ->
                StrategyListDto.getStrategyListDto(
                        strategy,
                        stockGetter.getStocks(strategy.getId()),
                        fileService.getFilePathNullable(new FileRequest(FileReferenceType.MEMBER, strategy.getTrader().getId())),
                        fileService.getFilePathNullable(new FileRequest(FileReferenceType.METHOD, strategy.getMethod().getId()))
                ));

        // Page<StrategyListDto> strategyListPage = strategyListRepository.findAllByStatusCode(String.valueOf(StrategyStatusCode.PUBLIC), pageable)
        //         .map(strategy -> StrategyListDto.getStrategyListDto(
        //                         strategy,
        //                         stockGetter.getStocks(strategy.getId()),
        //                         fileService.getFilePath(new FileRequest(FileReferenceType.MEMBER, strategy.getTrader().getId())),
        //                         fileService.getFilePath(new FileRequest(FileReferenceType.METHOD, strategy.getMethod().getId()))
        //                 )
        //         );

        return PageResponse.<StrategyListDto>builder()
                 .currentPage(result.getNumber())
                 .pageSize(result.getSize())
                 .totalElement(result.getTotalElements())
                 .totalPages(result.getTotalPages())
                 .content(result.getContent())
                 .build();
    }


    /*
        findByTraderNickname : 트레이더 닉네임으로 검색
        공개 전략 수 내림차순 정렬
    */
    @Override
    public PageResponse<TraderNickNameListResponseDto> findTraderNickname(String nickname, Integer pageNum) {

        Pageable pageable = PageRequest.of(pageNum, PAGE_SIZE);

        Page<TraderNicknameListDto> strategyPage = strategyListRepository.findDistinctByTraderNickname(nickname, pageable);

        Page<TraderNickNameListResponseDto> result = strategyPage.map(traderDto -> {
                    String profileImagePath = fileService.getFilePathNullable(new FileRequest(FileReferenceType.MEMBER, traderDto.getId())
                );
            return TraderNickNameListResponseDto.builder()
                    .nicknameListDto(traderDto)
                    .traderProfileImage(profileImagePath)
                    .build();
        });

        return PageResponse.<TraderNickNameListResponseDto>builder()
                .currentPage(result.getNumber())
                .pageSize(result.getSize())
                .totalElement(result.getTotalElements())       // 검색 결과 수 대체 가능
                .totalPages(result.getTotalPages())
                .content(result.getContent())
                .build();
    }


    /*
        findStrategiesByTrader : 트레이더 별 전략 목록 - 전략 목록 내에서는 똑같이 수익률순 내림차순
    */
    @Override
    public StrategyListByTraderDto findStrategiesByTrader(Long traderId, Integer pageNum) {

        Pageable pageable = PageRequest.of(pageNum, PAGE_SIZE, Sort.by(Sort.Order.desc("accumulatedProfitLossRate")));

        Member trader = memberRepository.findById(traderId)
                .orElseThrow(() -> new NoSuchElementException("해당 트레이더가 존재하지 않습니다."));

        ArrayList<TraderStrategyListDto> arrayList = new ArrayList<>();

        Page<Strategy> strategies = strategyListRepository.findAllByTraderAndStatusCode(trader, String.valueOf(StrategyStatusCode.PUBLIC), pageable);
        strategies.getContent().forEach(strategy -> {
                    arrayList.add(
                            TraderStrategyListDto.builder()
                                    .strategyId(strategy.getId())
                                    .strategyName(strategy.getName())
                                    .methodIconPath(pathGetter.getMethodIconPath(strategy.getMethod().getId()))
                                    .stockList(stockGetter.getStocks(strategy.getId()))
                                    .cycle(strategy.getCycle())
                                    .accumulatedProfitLossRate(strategy.getAccumulatedProfitLossRate())
                                    .mdd(strategy.getMdd())
                                    .smScore(strategy.getSmScore())
                                    .build()
                    );
                });

        return StrategyListByTraderDto.builder()
                .traderId(trader.getId())
                .traderNickname(trader.getNickname())
                .traderProfileImage(pathGetter.getMemberProfilePath(trader.getId()))
                .followerCount(trader.getTotalFollow())
                .strategyCount(trader.getTotalStrategyCount())
                .strategyListDto(PageResponse.<TraderStrategyListDto>builder()
                        .totalElement(strategies.getTotalElements())
                        .totalPages(strategies.getTotalPages())
                        .pageSize(PAGE_SIZE)
                        .currentPage(pageNum)
                        .content(arrayList)
                        .build())
                .build();
    }


    /*
        findStrategiesByName : 전략명으로 검색
    */
    @Override
    public PageResponse<StrategyListDto> findStrategiesByName(String keyword, Integer pageNum) {

        Pageable pageable = PageRequest.of(pageNum, PAGE_SIZE, Sort.by(Sort.Order.desc("accumulatedProfitLossRate")));

        Page<Strategy> strategies = strategyListRepository.findAllByContainingName(keyword, pageable);

        // Strategy 엔티티를 DTO로 매핑
        Page<StrategyListDto> resultPage = strategies
                .map(strategy -> StrategyListDto.getStrategyListDto(
                        strategy,
                        stockGetter.getStocks(strategy.getId()),
                        fileService.getFilePathNullable(new FileRequest(FileReferenceType.MEMBER, strategy.getTrader().getId())),
                        fileService.getFilePathNullable((new FileRequest(FileReferenceType.METHOD, strategy.getMethod().getId()))
                        )
        ));

        return PageResponse.<StrategyListDto>builder()
                .currentPage(resultPage.getNumber())
                .pageSize(resultPage.getSize())
                .totalElement(resultPage.getTotalElements())
                .totalPages(resultPage.getTotalPages())
                .content(resultPage.getContent())
                .build();
    }
}