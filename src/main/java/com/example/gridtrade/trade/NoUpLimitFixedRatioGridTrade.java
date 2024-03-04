package com.example.gridtrade.trade;

import com.example.gridtrade.trade.pub.GridTrade;
import com.example.gridtrade.trade.pub.GridTradeItem;
import com.example.gridtrade.utils.DoubleUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 无上限固定比例网格交易
 */

@Slf4j
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NoUpLimitFixedRatioGridTrade extends GridTrade {

    protected double fixedRatio;

    public void init() {
        if (StringUtils.isAnyBlank(currency, market)) {
            log.error("currency market is any blank,currency:{},market:{}", currency, market);
            throw new RuntimeException("currency market is any blank");
        }
        double gridAmount = amount / gridNum;
        if (gridAmount < minGridAmount) {
            log.error("grid amount too less,grid amount:{},min gird amount:{}", gridAmount, minGridAmount);
            throw new RuntimeException("grid amount too less");
        }
        double prePrice = lowPrice;
        for (int i = 0; i < gridNum; i++) {
            double buyPrice = DoubleUtils.scaleOfRoundDown(prePrice, scale);
            double sellPrice = DoubleUtils.scaleOfRoundDown(prePrice * fixedRatio, scale);
            double quantity = DoubleUtils.scaleOfRoundDown(gridAmount / buyPrice, scale);
            GridTradeItem item = GridTradeItem.builder()
                    .code(i)
                    .currency(currency)
                    .market(market)
                    .quantity(quantity)
                    .buyPrice(buyPrice)
                    .sellPrice(sellPrice)
                    .tradePlatform(tradePlatform)
                    .buyStart(buyStart)
                    .build();
            gridList.add(item);
            prePrice = sellPrice;
        }
        highPrice = prePrice;
    }
}
