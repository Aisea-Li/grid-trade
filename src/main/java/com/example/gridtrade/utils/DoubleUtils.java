package com.example.gridtrade.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DoubleUtils {

    public static double scaleOfRoundDown(double value, int scale) {
        return new BigDecimal(value)
                .setScale(scale, RoundingMode.DOWN)
                .doubleValue();
    }
}
