package com.example.gridtrade.trade.pub;

import com.example.gridtrade.entity.dto.Income;
import com.example.gridtrade.entity.dto.TradeOrder;
import com.example.gridtrade.entity.enums.TradeOrderType;
import com.example.gridtrade.entity.enums.TradeStatus;
import com.example.gridtrade.entity.enums.TradeType;
import com.example.gridtrade.trade.platform.TradePlatform;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;


@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GridTradeItem {

    private int code;

    private String currency;

    private String market;

    private double buyPrice;

    private double sellPrice;

    private double quantity;

    private String currentOrderId;

    private boolean buyStart = true;

    @JsonIgnore
    private TradePlatform tradePlatform;

    @Builder.Default
    private Long tradeFinishTimes = 0L;

    @Builder.Default
    private boolean selling = false;

    public void checkAndTrade() {
        if (StringUtils.isBlank(currentOrderId)) {
            // 首次
            if (buyStart) {
                // 挂单买入
                buy();
            } else {
                // 持仓卖出
                sell();
            }
            return;
        }
        // 是否挂单中
        TradeOrder tradeOrder = tradePlatform.getCurrentOrder(currentOrderId);
        if (Objects.nonNull(tradeOrder)) {
            // 订单挂单中 无需处理
            return;
        }
        // 查询订单
        tradeOrder = tradePlatform.queryOrder(currentOrderId, TradeOrderType.LIMIT_ORDER);
        // 订单不存在
        if (Objects.isNull(tradeOrder)) {
            log.error("trade order not exist,code:{},current order id:{}", code, currentOrderId);
            return;
        }
        TradeStatus status = tradeOrder.getStatus();
        // 订单是否在等待
        if (TradeStatus.NEW.equals(status)) {
            return;
        }
        // 订单是否完成
        if (!TradeStatus.FILLED.equals(status)) {
            log.error("order trade state has problem,code:{},current order id:{},status:{}", code, currentOrderId, status);
            return;
        }
        // 订单已完成 切换交易方向 重新下单
        TradeType tradeType = tradeOrder.getTradeType();
        if (TradeType.BUY.equals(tradeType)) {
            // 买入完成 挂单卖出
            sell();
        } else if (TradeType.SELL.equals(tradeType)) {
            // 卖出完成 日志记录完成 挂单买入
            tradeFinishTimes++;
            log.info("sell finish,code:{},order id:{},trade finish times:{}", code, currentOrderId, tradeFinishTimes);
            buy();
        } else {
            log.error("order trade type has problem,trade type:{}", tradeType);
        }
    }

    private void buy() {
        String orderId = tradePlatform.placeOrder(
                currency,
                market,
                TradeType.BUY,
                TradeOrderType.LIMIT_ORDER,
                buyPrice,
                quantity
        );
        if (StringUtils.isBlank(orderId)) {
            log.error("buy,fail,code:{},order id:{}", code, currentOrderId);
            return;
        }
        currentOrderId = orderId;
        selling = false;
        log.info("buy,code:{},order id:{}", code, currentOrderId);
    }

    private void sell() {
        String orderId = tradePlatform.placeOrder(
                currency,
                market,
                TradeType.SELL,
                TradeOrderType.LIMIT_ORDER,
                sellPrice,
                quantity
        );
        if (StringUtils.isBlank(orderId)) {
            log.error("sell,fail,code:{},order id:{}", code, currentOrderId);
            return;
        }
        currentOrderId = orderId;
        selling = true;
        log.info("sell,code:{},order id:{}", code, currentOrderId);
    }

    public Income getIncome() {
        return Income.builder()
                .cost(selling ? buyPrice * quantity : 0)
                .quantity(selling ? quantity : 0)
                .tradeTimes(tradeFinishTimes)
                .realizedIncome((sellPrice - buyPrice) * quantity * tradeFinishTimes)
                .build();
    }
}
