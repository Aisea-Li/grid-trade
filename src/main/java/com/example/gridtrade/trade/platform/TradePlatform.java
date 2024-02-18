package com.example.gridtrade.trade.platform;

import com.example.gridtrade.entity.dto.TradeOrder;
import com.example.gridtrade.entity.enums.TradeOrderType;
import com.example.gridtrade.entity.enums.TradeType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class TradePlatform {

    protected Map<String, TradeOrder> currentOrderCache = new ConcurrentHashMap<>();

    public abstract boolean refreshCurrentOrder(String currency, String market, int num);

    public TradeOrder getCurrentOrder(String orderId) {
        return currentOrderCache.get(orderId);
    }

    public abstract TradeOrder queryOrder(String orderId, TradeOrderType tradeOrderType);

    public abstract String placeOrder(String currency, String market, TradeType tradeType, TradeOrderType tradeOrderType, double price, double quantity);

}
