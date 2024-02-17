package com.example.gridtrade.entity.response;


import com.example.gridtrade.entity.dto.TradeOrder;
import com.example.gridtrade.entity.enums.TradeStatus;
import com.example.gridtrade.entity.enums.TradeType;
import com.example.gridtrade.utils.EnumUtils;
import lombok.Data;

import java.util.List;

@Data
public class Order {
    private String orderId;
    private String symbol;
    private String orderType;
    private String triggerType;
    private String tradeType;
    private String triggerTradeType;
    private String price;
    private String quantity;
    private String amount;
    private String dealQuantity;
    private String dealAmount;
    private String triggerPrice;
    private String state;
    private String triggerState;
    private Long createTime;
    private String avgPrice;
    private List<OrderDetail> deals;

    public TradeOrder toTradeOrder() {
        return TradeOrder.builder()
                .id(this.getOrderId())
                .tradeType(EnumUtils.getEnumByName(this.getTradeType(), TradeType.class))
                .status(EnumUtils.getEnumByName(this.getState(), TradeStatus.class))
                .build();
    }
}
