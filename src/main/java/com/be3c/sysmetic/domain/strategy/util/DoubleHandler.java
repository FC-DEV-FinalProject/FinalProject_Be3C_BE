package com.be3c.sysmetic.domain.strategy.util;

import org.springframework.stereotype.Component;

/* 소수점 5자리 이상은 버림, 4자리까지 유지 */
@Component
public class DoubleHandler {

    private static final double EPSILON = 0.0001;
    private static final int SCALE = 10000;

    public Double cutDouble(Double originalDoubleNumber){

        return Math.floor(originalDoubleNumber * SCALE) / SCALE;
    }

    public boolean compare(Double a, Double b){

        return Math.abs(a - b) < EPSILON;
    }
}