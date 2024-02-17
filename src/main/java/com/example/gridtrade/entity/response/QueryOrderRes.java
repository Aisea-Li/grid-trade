package com.example.gridtrade.entity.response;

import lombok.Data;


@Data
public class QueryOrderRes {
    /**
     * 交易对
     */
    private String symbol;

    /**
     * 原始客户端订单id
     */
    private String origClientOrderId;

    /**
     * 订单id
     */
    private String orderId;

    /**
     * 客户自定义id
     */
    private String clientOrderId;

    /**
     * 价格
     */
    private String price;

    /**
     * 原始订单数量
     */
    private String origOty;

    /**
     * 交易的订单数量
     */
    private String executedQty;

    /**
     * 累计订单金额
     */
    private String cummulativeQuoteQty;

    /**
     * 订单状态
     */
    private String status;

    /**
     * 订单的时效方式
     */
    private String timeInForce;

    /**
     * 订单类型
     */
    private String type;

    /**
     * 订单方向
     */
    private String side;

    /**
     * 止损价格
     */
    private String stopPrice;

    /**
     * 冰山数量
     */
    private String icebergQty;

    /**
     * 订单时间
     */
    private Long time;

    /**
     * 最后更新时间
     */
    private Long updateTime;

    /**
     * 是否在orderbook中
     */
    private String isWorking;

    /**
     * 原始的交易金额
     */
    private String origQuoteOrderQty;
}
