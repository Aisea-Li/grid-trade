package com.example.gridtrade.entity.response;

import com.example.gridtrade.entity.dto.TradeOrder;
import com.example.gridtrade.entity.enums.TradeStatus;
import com.example.gridtrade.entity.enums.TradeType;
import com.example.gridtrade.utils.EnumUtils;
import lombok.Data;


@Data
public class CurrentOrder {
    private String id;
    private String createTime;
    private String updateTime;
    private String currency;
    private String market;
    private String symbol;
    private String tradeType;
    private String orderType;
    private String price;
    private String triggerPrice;
    private String quantity;
    private String amount;
    private String dealQuantity;
    private String dealAmount;
    private String avgPrice;
    private String state;
    private String source;
    private String triggerType;
    private String fee;
    private String uniqueId;
    private String triggerId;
    private String estPnl;
    private String ocoId;

    public TradeOrder toTradeOrder() {
        return TradeOrder.builder()
                .id(this.getId())
                .tradeType(EnumUtils.getEnumByCode(Integer.valueOf(this.getTradeType()), TradeType.class))
                .status(EnumUtils.getEnumByCode(Integer.valueOf(this.getState()), TradeStatus.class))
                .build();
    }
}
