package com.be3c.sysmetic.domain.strategy.util;

import com.be3c.sysmetic.domain.strategy.dto.KpRatioParametersDto;
import com.be3c.sysmetic.domain.strategy.dto.KpRatios;
import com.be3c.sysmetic.domain.strategy.repository.DailyRepository;
import com.be3c.sysmetic.domain.strategy.repository.StrategyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// TODO 테스트 안함. 테스트 필요!
@Component
@Slf4j
@RequiredArgsConstructor
public class StrategyIndicatorsCalculator {

    /*
        StrategyIndicators : MDD, KP Ratio, SM Score, AccumProfitLossRate 업데이트
        DoubleHandler - cutDouble 메서드로 소수점 잘라서 저장
    */
    private final StrategyRepository strategyRepository;
    private final DailyRepository dailyRepository;
    private final DoubleHandler doubleHandler;

    @Transactional
    public void updateIndicators(Long strategyId) {

        Object[] result = dailyRepository.findMaxAndMinProfitLossAmount(strategyId);
        Double mdd = calculateMdd(result);
        strategyRepository.updateMdd(strategyId, mdd);

        Double kpRatio = calculateKpRatio(strategyId);
        strategyRepository.updateKpRatio(strategyId, kpRatio);
        strategyRepository.flush();

        Double smScore = calculateSmScore(strategyId);
        strategyRepository.updateSmScore(strategyId, smScore);

        Double accumulatedProfitLossRate = doubleHandler.cutDouble(dailyRepository.findLatestAccumulatedProfitLossRate(strategyId));
        strategyRepository.updateAccumulatedLProfitLossRate(strategyId, accumulatedProfitLossRate);
    }


    // MDD 계산
    private Double calculateMdd(Object[] result) {
        if (result == null || result.length < 2 || result[0] == null || (Double) result[0] == 0.0 || result[1] == null)
            return 0.0;

        Double peak = (Double) result[0];
        Double trough = (Double) result[1];

        return doubleHandler.cutDouble((peak - trough) / peak * 100);
    }


    // KP Ratio 계산
    private Double calculateKpRatio(Long strategyId) {
        List<KpRatioParametersDto> days = dailyRepository.findKpRatioParameters(strategyId);
        if (days == null || days.isEmpty() || days.size() == 1) return 0.0;

        double accumulatedProfitLossRate = days.get(days.size() - 1).getAccumulatedProfitLossRate();
        if (accumulatedProfitLossRate == 0.0) return 0.0;

        double peak = days.get(0).getAccumulatedProfitLossRate();
        double minDropValue = Double.MAX_VALUE;
        double drawDownValueSum = 0.0;
        double drawDownDaysSum = 0;


        for (int i=1; i < days.size(); i++) {
            double currentAccumulatedProfitLossRate = days.get(i).getAccumulatedProfitLossRate();

            if (currentAccumulatedProfitLossRate < peak) {
                // DrawDown 발생
                if (currentAccumulatedProfitLossRate < minDropValue) minDropValue = currentAccumulatedProfitLossRate;
                drawDownDaysSum++;
            } else {        // DrawDown 끝남 또는 아님
                drawDownValueSum += peak - minDropValue;
                minDropValue = Double.MAX_VALUE;
                peak = currentAccumulatedProfitLossRate;
            }
        }
        if (drawDownValueSum == 0.0) return 0.0;
        return doubleHandler.cutDouble(accumulatedProfitLossRate / drawDownValueSum * Math.sqrt(drawDownDaysSum / days.size()));
    }

    // Sm Score 계산
    private Double calculateSmScore(Long strategyId) {
        String statusCode = "PUBLIC";
        List<KpRatios> kpRatios = strategyRepository.findKpRatios(statusCode);
        if (kpRatios == null || kpRatios.isEmpty()) return 0.0;

        // TODO 람다식과 for문 시간 비교
        // Double kpRatioAverage = 0.0;
        // for (KpRatios k : kpRatios)
        //     kpRatioAverage += k.getKpRatio();

        Double kpRatioAverage = kpRatios.stream()
                .mapToDouble(KpRatios::getKpRatio)
                .average()
                .orElse(0.0);
        if (kpRatioAverage == 0.0) return 0.0;


        Double kpRatioStandardDeviation = Math.sqrt(
                kpRatios.stream()
                        .mapToDouble(k -> Math.pow(k.getKpRatio() - kpRatioAverage, 2))
                        .sum() / kpRatios.size()
        );
        if (kpRatioStandardDeviation <= 0.0) return 0.0;


        // 표준화척도 Z
        Double zScore = (kpRatioAverage - kpRatios.get(0).getKpRatio()) / kpRatioStandardDeviation;

        // CDF (표준정규 누적분포값)
        NormalDistribution normalDistribution = new NormalDistribution();
        Double cdfValuePercentage = normalDistribution.cumulativeProbability(zScore) * 100;

        return doubleHandler.cutDouble(cdfValuePercentage);
    }
}
