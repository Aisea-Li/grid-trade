package com.example.gridtrade.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum TradeType implements CodeEnum<Integer> {

    BUY(1, "买入"),
    SELL(2, "买入");

    public final Integer code;

    public final String desc;
}
