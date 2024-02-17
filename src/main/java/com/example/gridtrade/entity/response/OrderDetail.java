package com.example.gridtrade.entity.response;

import lombok.Data;

@Data
public class OrderDetail {
    private String dealId;
    private String symbol;
    private String tradeType;
    private String quantity;
    private String price;
    private String amount;
    private String fee;
    private String feeCurrency;
    private Long createTime;
    private Integer isTaker;
}
