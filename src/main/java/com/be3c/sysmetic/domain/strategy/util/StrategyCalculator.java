package com.be3c.sysmetic.domain.strategy.util;

import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

// 전략 계산 util
@Component
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class StrategyCalculator {

    private final DoubleHandler doubleHandler;

    /*
    원금
    첫데이터: 입출금
    이후데이터: 전 원금 + (입출금/(전 잔고/전 원금))
     */
    public Double getPrincipal(boolean isFirst, Double depositWithdrawalAmount, Double beforePrincipal, Double beforeBalance) {
        if(isFirst) {
            return doubleHandler.cutDouble(depositWithdrawalAmount);
        } else {
            if(beforePrincipal == 0 || beforeBalance == 0) return 0.0;
            return doubleHandler.cutDouble(beforePrincipal + (depositWithdrawalAmount/(beforeBalance/beforePrincipal)));
        }
    }

    /*
    기준가
    => 잔고/원금*1000
    첫데이터: (입출금 + 일손익) / 입출금 * 1000
    이후데이터: (전 잔고 + 입출금 + 일손익) / (전 원금 + (입출금/(전 잔고/전 원금))) * 1000
     */
    public Double getStandardAmount(boolean isFirst, Double depositWithdrawalAmount, Double dailyProfitLossAmount, Double beforeBalance, Double beforePrincipal) {
        Double balance = getCurrentBalance(isFirst, beforeBalance, depositWithdrawalAmount, dailyProfitLossAmount);
        Double principal = getPrincipal(isFirst, depositWithdrawalAmount, beforePrincipal, beforeBalance);

        if(principal == 0) return 0.0;
        return doubleHandler.cutDouble(balance / principal * 1000);
    }

    /*
    잔고
    첫데이터: 원금 + 일손익 => 입출금 + 일손익
    이후데이터: 전 잔고 + 입출금 + 일손익
     */
    public Double getCurrentBalance(boolean isFirst, Double beforeBalance, Double depositWithdrawalAmount, Double dailyProfitLossAmount) {
        if(isFirst) {
            return doubleHandler.cutDouble(depositWithdrawalAmount + dailyProfitLossAmount);
        } else {
            return doubleHandler.cutDouble(beforeBalance + depositWithdrawalAmount + dailyProfitLossAmount);
        }
    }

    /*
    일손익률
    첫데이터: (기준가 - 1000) * 1000
    이후데이터: (현재 기준가 - 이전 데이터 기준가) / 이전 데이터 기준가
     */
    public Double getDailyProfitLossRate(boolean isFirst, Double depositWithdrawalAmount, Double dailyProfitLossAmount, Double beforeBalance, Double beforePrincipal, Double beforeStandardAmount) {
        Double standardAmount = getStandardAmount(isFirst, depositWithdrawalAmount, dailyProfitLossAmount, beforeBalance, beforePrincipal);
        if(isFirst) {
            return doubleHandler.cutDouble((standardAmount - 1000) / 1000 * 100);
        } else {
            if(beforeStandardAmount == 0) return 0.0;
            return doubleHandler.cutDouble((standardAmount - beforeStandardAmount) / beforeStandardAmount * 100);
        }
    }

    /*
    평균
     */
    public Double calculateAverage(List<Double> dataList) {
        Double sum = 0.0;
        for (Double data : dataList) {
            sum += data;
        }
        return dataList.size() == 0 ? 0.0 : doubleHandler.cutDouble(sum / dataList.size());
    }

    /*
    표준편차
     */
    public Double calculateStandardDeviation(List<Double> dataList, Double avg) {
        Double sumSquaredDifferences = 0.0;

        for (Double data : dataList) {
            // 평균과의 차이 제곱 계산
            sumSquaredDifferences += Math.pow(data - avg, 2);
        }

        // 표준편차
        return Math.sqrt(sumSquaredDifferences / dataList.size());
    }

    /*
    sm score
     */
    public static Double getSmScore(Double kpRatio, Double averageKpRatio, Double standardDeviationKpRatio) {
        // 표준정규분포 (평균 0, 표준편차 1)
        NormalDistribution normalDistribution = new NormalDistribution(0, 1);

        // 표준화척도 = ((kp-ratio - kp 평균) / 표준편차)
        Double zScore = standardDeviationKpRatio == 0 ? 0.0 : (kpRatio - averageKpRatio) / standardDeviationKpRatio;

        // 표준정규누적분포
        Double cdfValue = normalDistribution.cumulativeProbability(zScore);

        // sm score = 표준정규누적분포 * 100
        return cdfValue * 100;
    }

    /*
    kp ratio
    누적손익률 / (DD 값 총합 * sqrt(DD 기간 총합 / 총 거래일))
     */
    public Double getKpRatio(Double accumulatedProfitLossRate, Double sumDrawDownValue, Long sumDrawDownPeriod, Long totalTradingDays) {
        sumDrawDownValue *= -1;

        // kp ratio = 누적손익률 / (DD 값 총합 * sqrt(DD 기간 총합 / 총 거래일))
        if(sumDrawDownValue == 0 || sumDrawDownPeriod == 0 || totalTradingDays == 0) return 0.0;
        if(Math.sqrt((double) sumDrawDownPeriod / totalTradingDays) == 0) return 0.0;
        return doubleHandler.cutDouble(accumulatedProfitLossRate / (sumDrawDownValue * Math.sqrt((double) sumDrawDownPeriod / totalTradingDays)));
    }

    /**
     * 누적 손익 계산
     * @param profitLossAmount 새로 추가되는 손익 값
     * @param formerAccumulatedProfitAmount 직전 누적 손익 값
     * @return 누적 손익 값 (직전 누적 손익 값 + 새로 추가되는 손익 값)
     */
    public Double getAccumulatedProfitLossAmount(Double profitLossAmount, Double formerAccumulatedProfitAmount) {

        if(formerAccumulatedProfitAmount == null)
            return doubleHandler.cutDouble(profitLossAmount);

        return doubleHandler.cutDouble(profitLossAmount + formerAccumulatedProfitAmount);
    }

    /**
     * 누적 손익률 계산
     * @param profitLossRate 새로 추가되는 손익률
     * @param formerProfitRate 직전 누적 손익률
     * @return 누적 손익률 (직전 누적 손익률 + 새로 추가되는 손익률)
     */
    public Double getAccumulatedProfitLossRate(Double profitLossRate, Double formerProfitRate) {

        if(formerProfitRate == null)
            return doubleHandler.cutDouble(profitLossRate);

        return doubleHandler.cutDouble(profitLossRate + formerProfitRate);
    }

}