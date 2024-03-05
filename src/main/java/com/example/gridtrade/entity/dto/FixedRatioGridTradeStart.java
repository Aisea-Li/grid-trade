package com.example.gridtrade.entity.dto;

import lombok.Data;

@Data
public class FixedRatioGridTradeStart {
    private String currency;
    private String market;
    private Double amount;
    private Double lowPrice;
    private Double fixedRatio;
    private Integer gridNum;
    private Boolean buyStart = false;
}
