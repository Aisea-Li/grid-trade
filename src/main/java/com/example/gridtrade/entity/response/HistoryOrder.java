package com.example.gridtrade.entity.response;

import com.example.gridtrade.entity.dto.TradeOrder;
import com.example.gridtrade.entity.enums.TradeStatus;
import com.example.gridtrade.entity.enums.TradeType;
import com.example.gridtrade.utils.EnumUtils;
import lombok.Data;


@Data
public class HistoryOrder {
    private String id;
    private String amount;
    private String avgPrice;
    private String createTime;
    private String currency;
    private String dealAmount;
    private String dealQuantity;
    private String estPnl;
    private String fee;
    private String market;
    private String ocoId;
    private String orderType;
    private String price;
    private String quantity;
    private String source;

    /**
     * 状态
     * 2-已成交 4-已取消 5-部分成交
     */
    private String state;
    private String symbol;
    private String tradeType;
    private String triggerId;
    private String triggerPrice;
    private String triggerType;
    private String uniqueId;
    private String updateTime;

    public TradeOrder toTradeOrder() {
        return TradeOrder.builder()
                .id(this.getId())
                .tradeType(EnumUtils.getEnumByCode(Integer.valueOf(this.getTradeType()), TradeType.class))
                .status(EnumUtils.getEnumByCode(Integer.valueOf(this.getState()), TradeStatus.class))
                .build();
    }
}
