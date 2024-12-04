package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.domain.strategy.dto.*;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.repository.*;
import com.be3c.sysmetic.domain.strategy.util.StockGetter;
import com.be3c.sysmetic.global.util.file.dto.FileReferenceType;
import com.be3c.sysmetic.global.util.file.dto.FileRequest;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@Service
public class MainPageServiceImpl implements MainPageService {

    private final MainPageRepository mainPageRepository;
    private final MemberRepository memberRepository;
    private final StrategyGraphAnalysisRepository strategyGraphAnalysisRepository;
    private final StrategyRepository strategyRepository;
    private final DailyRepository dailyRepository;
    private final FileService fileService;
    private final StockGetter stockGetter;

    @Override
    @Transactional
    public MainPageDto getMain() {

        return MainPageDto.builder()
                .rankedTrader(setTop3FollowerTrader())
                .totalTraderCount(memberRepository.countAllByRoleCode("TRADER").orElse(0L))
                .totalStrategyCount(mainPageRepository.count())
                .smScoreTopFives(setTop5SmScore())
                .build();
    }


    @Override
    @Transactional
    public MainPageAnalysisDto getAnalysis(String period) {

        LocalDate currentDate = LocalDate.now();
        LocalDate startDate = calculateStartDate(currentDate, period);

        return MainPageAnalysisDto.builder()
                .smScoreTopStrategyName(strategyRepository.findTop1SmScore().orElse(null))
                .xAxis(strategyGraphAnalysisRepository.findDates(startDate).orElse(null))
                .averageStandardAmount(strategyGraphAnalysisRepository.findAverageStandardAmounts(startDate).orElse(null))
                .accumProfitLossRate(dailyRepository.findAccumulatedProfitLossRates(startDate).orElse(null))
                .build();
    }

    private LocalDate calculateStartDate(LocalDate currentDate, String period) {
        switch (period) {
            case "ONE_MONTH":
                return currentDate.minusMonths(1);
            case "THREE_MONTH":
                return currentDate.minusMonths(3);
            case "SIX_MONTH":
                return currentDate.minusMonths(6);
            case "ONE_YEAR":
                return currentDate.minusYears(1);
            case "ALL":
            default:
                return LocalDate.of(2000, 1, 1);        // 2000년 1월 1일 이후의 기간
        }
    }


    // setTop3FollowerTrader : 트레이더 팔로우 수 Top 3
    private List<TraderRankingDto> setTop3FollowerTrader(){
        List<TraderRankingDto> traders = new ArrayList<>();

        Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Order.desc("followerCount")));

        Page<Strategy> strategyPage = mainPageRepository.findTop3ByFollowerCount(pageable);

        if (strategyPage.isEmpty())
            return new ArrayList<>();

        for (Strategy s : strategyPage.getContent()) {

            if (s.getContent() == null || s.getContent().isEmpty())
                return new ArrayList<>();

            traders.add(TraderRankingDto.builder()
                    .id(s.getId())
                    .nickname((s.getTrader().getNickname()))
                    .traderProfileImage(fileService.getFilePathNullable(new FileRequest(FileReferenceType.MEMBER, s.getId())))
                    .followerCount(s.getFollowerCount())
                    .accumProfitLossRate(s.getAccumulatedProfitLossRate())
                    .build()
            );
        }
        return traders;
    }

    // setTop5SmScore : SM Score Top 5 전략
    private List<SmScoreTopFive> setTop5SmScore(){

        Pageable pageable = PageRequest.of(0, 5);

        Page<Strategy> strategyPage = mainPageRepository.findTop5SmScore(pageable);

        if (strategyPage.isEmpty())
            return new ArrayList<>();

        return strategyPage.stream()
                .map(strategy -> SmScoreTopFive.builder()
                        .id(strategy.getId())
                        .traderId(strategy.getTrader().getId())
                        .traderProfileImage(fileService.getFilePathNullable(new FileRequest(FileReferenceType.MEMBER, strategy.getTrader().getId())))
                        .nickname(strategy.getTrader().getNickname())
                        .name(strategy.getName())
                        .stocks(stockGetter.getStocks(strategy.getId()))
                        .accumulatedProfitLossRate(strategy.getAccumulatedProfitLossRate())
                        .smScore(strategy.getSmScore())
                        .build())
                .collect(Collectors.toList());
    }
}
