package com.be3c.sysmetic.domain.strategy.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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