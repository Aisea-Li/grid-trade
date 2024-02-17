package com.example.gridtrade.entity.dto;

import lombok.Data;

@Data
public class GridTradeStart {
    private String currency;
    private String market;
    private Double amount;
    private Double lowPrice;
    private Double highPrice;
    private Integer gridNum;
    private Boolean buyStart = false;
}
