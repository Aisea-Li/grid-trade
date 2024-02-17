package com.example.gridtrade.entity.dto;

import com.example.gridtrade.entity.enums.TradeStatus;
import com.example.gridtrade.entity.enums.TradeType;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class TradeOrder {

    private String id;

    private TradeStatus status;

    private TradeType tradeType;
}
