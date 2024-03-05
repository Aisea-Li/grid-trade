package com.example.gridtrade.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GridTradeType implements CodeEnum<Integer> {

    FIXED_DIFF_GRID_TRADE(1, "固定差值网格交易"),
    FIXED_RATIO_GRID_TRADE(2, "固定比例网格交易"),
    ;

    public final Integer code;

    public final String desc;
}
