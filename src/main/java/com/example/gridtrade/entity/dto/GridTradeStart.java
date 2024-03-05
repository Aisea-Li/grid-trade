package com.example.gridtrade.entity.dto;

import lombok.Data;

@Data
public class GridTradeStart {
    private String type;
    private String currency;
    private String market;
    private Double amount;
    private Double lowPrice;
    private Double highPrice;
    private Double fixedRatio;
    private Integer gridNum;
    private Integer scale;
    private Boolean buyStart = false;
}
