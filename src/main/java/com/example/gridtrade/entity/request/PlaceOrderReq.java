package com.example.gridtrade.entity.request;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class PlaceOrderReq {
    /**
     * 交易币种
     * MX
     */
    private String currency;

    /**
     * 价值锚定币种
     * USDT
     */
    private String market;

    /**
     * 交易类型
     * BUY-买入 SELL-卖出
     */
    private String tradeType;

    /**
     * 订单类型
     * LIMIT_ORDER-限价单
     */
    private String orderType;

    /**
     * 价格
     */
    private String price;

    /**
     * 数量
     */
    private String quantity;
}
