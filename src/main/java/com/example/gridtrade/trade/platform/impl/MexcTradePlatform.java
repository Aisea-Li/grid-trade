package com.example.gridtrade.trade.platform.impl;

import com.example.gridtrade.client.MexcWebClient;
import com.example.gridtrade.entity.dto.TradeOrder;
import com.example.gridtrade.entity.enums.TradeOrderType;
import com.example.gridtrade.entity.enums.TradeType;
import com.example.gridtrade.entity.request.PlaceOrderReq;
import com.example.gridtrade.entity.response.CurrentOrder;
import com.example.gridtrade.entity.response.Order;
import com.example.gridtrade.entity.response.Page;
import com.example.gridtrade.entity.response.Response;
import com.example.gridtrade.trade.platform.TradePlatform;
import com.example.gridtrade.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MexcTradePlatform extends TradePlatform {

    @Autowired
    private MexcWebClient mexcWebClient;

    // 最大100
    private final static int PAGE_SIZE = 100;

    @Override
    public TradeOrder queryOrder(String orderId, TradeOrderType tradeOrderType) {
        Response<Order> res = mexcWebClient.queryOrderDetail(orderId, tradeOrderType.getCode());
        if (Objects.isNull(res) || !res.hasData()) {
            return null;
        }
        return res.getData().toTradeOrder();
    }

    @Override
    public String placeOrder(String currency, String market, TradeType tradeType, TradeOrderType tradeOrderType, double price, double quantity) {
        PlaceOrderReq req = PlaceOrderReq.builder()
                .currency(currency)
                .market(market)
                .tradeType(tradeType.name())
                .orderType(tradeOrderType.name())
                .price(String.valueOf(price))
                .quantity(String.valueOf(quantity))
                .build();
        Response<String> res = mexcWebClient.placeOrder(req);
        if (ResponseUtils.hasData(res)) {
            return res.getData();
        } else {
            return null;
        }
    }

    @Override
    public boolean refreshCurrentOrder(String currency, String market, int num) {
        List<TradeOrder> currentOrderList = queryCurrentOrder(currency, market, num);
        if (Objects.isNull(currentOrderList)) {
            return false;
        }
        this.currentOrderCache.clear();
        for (TradeOrder currentOrder : currentOrderList) {
            this.currentOrderCache.put(currentOrder.getId(), currentOrder);
        }
        return true;
    }

    protected List<TradeOrder> queryCurrentOrder(String currency, String market, int num) {
        List<TradeOrder> ret = new ArrayList<>();
        int page = 1, maxPage = num / PAGE_SIZE + (num % PAGE_SIZE > 0 ? 1 : 0);
        Response<Page<CurrentOrder>> res;
        do {
            res = mexcWebClient.queryCurrentOrders(
                    currency,
                    market,
                    "1,2,3,4,5,100,101,102",
                    page,
                    PAGE_SIZE
            );
            if (!ResponseUtils.hasData(res)) {
                log.error("query current order fail,res:{}", res);
                return null;
            }
            ret.addAll(
                    ListUtils.emptyIfNull(res.getData().getResultList())
                            .stream()
                            .map(CurrentOrder::toTradeOrder)
                            .collect(Collectors.toList())
            );
            page++;
        } while (page <= Math.min(maxPage, res.getData().getTotalPage()));
        return ret;
    }
}
