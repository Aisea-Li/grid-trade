package com.example.gridtrade.entity.response;

import lombok.Data;


@Data
public class OrderRes {

    /**
     * 交易对
     */
    private String symbol;

    /**
     * 订单id
     */
    private String orderId;

    /**
     * 客户端订单列表
     */
    private String orderListId;
    /**
     * 价格
     */
    private String price;

    /**
     * 委托数量
     */
    private String origQty;

    /**
     * 订单类型
     */
    private String type;

    /**
     * 订单方向
     */
    private String side;

    /**
     * 下单时间
     */
    private long transactTime;

}
