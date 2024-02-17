package com.example.gridtrade.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum TradeOrderType implements CodeEnum<String> {

    LIMIT_ORDER("LIMIT_ORDER", "限价单"),
    ;

    public final String code;

    public final String desc;
}
