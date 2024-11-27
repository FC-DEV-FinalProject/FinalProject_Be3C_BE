package com.be3c.sysmetic.domain.strategy.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@TestPropertySource(locations = "/application-test.properties")
@Slf4j
@SpringBootTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SMScoreCalculateTest {

    double[] testData = new double[]{1.97, 0.85, 2.45, 1.50, 3.0, 0.2, 2.0, 1.2, 4.2, 7.0};

    @Test
    @DisplayName("표준화 후 sm score 계산")
    @Order(1)
    public void testCal() {
        // 데이터 평균과 표준편차 계산
        double mean = calculateMean(testData);
        double stdDev = calculateStdDev(testData, mean);

        System.out.println("Mean: " + mean + ", Std Dev: " + stdDev);

        // 평균과 표준편차를 사용하여 데이터를 표준화
        double[] standardizedData = new double[testData.length];
        for (int i = 0; i < testData.length; i++) {
            standardizedData[i] = (testData[i] - mean) / stdDev;
        }

        // 표준 정규 분포 생성 (평균 0, 표준편차 1)
        NormalDistribution normal = new NormalDistribution(0, 1);

        // 표준화된 데이터에 대해 CDF 계산
        for (double value : standardizedData) {
            double cdf = normal.cumulativeProbability(value);
            System.out.println("Standardized Value: " + value + ", CDF: " + cdf);
        }
    }

    // 평균 계산
    private double calculateMean(double[] data) {
        double sum = 0.0;
        for (double num : data) {
            sum += num;
        }
        return sum / data.length;
    }

    // 표준편차 계산
    private double calculateStdDev(double[] data, double mean) {
        double sum = 0.0;
        for (double num : data) {
            sum += Math.pow(num - mean, 2);
        }
        return Math.sqrt(sum / data.length);
    }
}