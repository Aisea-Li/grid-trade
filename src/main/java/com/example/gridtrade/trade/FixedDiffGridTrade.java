package com.example.gridtrade.trade;

import com.example.gridtrade.entity.dto.Income;
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
 * 固定差值网格交易
 */
@Slf4j
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FixedDiffGridTrade extends GridTrade {

    @Override
    public void init() {
        if (StringUtils.isAnyBlank(currency, market)) {
            log.error("currency market is any blank,currency:{},market:{}", currency, market);
            throw new RuntimeException("currency market is any blank");
        }
        if (highPrice <= lowPrice) {
            log.error("highPrice less than lowPrice,low price:{},high price:{}", lowPrice, highPrice);
            throw new RuntimeException("highPrice <= lowPrice");
        }
        double gridAmount = amount / gridNum;
        if (gridAmount < minGridAmount) {
            log.error("grid amount too less,grid amount:{},max gird amount:{}", gridAmount, minGridAmount);
            throw new RuntimeException("grid amount too less");
        }
        double gridPriceDiff = (highPrice - lowPrice) / gridNum;
        double prePrice = lowPrice;
        for (int i = 0; i < gridNum; i++) {
            double buyPrice = DoubleUtils.scaleOfRoundDown(prePrice, scale);
            double sellPrice = DoubleUtils.scaleOfRoundDown(prePrice + gridPriceDiff, scale);
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
    }

    public Income getIncome() {
        Income income = Income.builder().build();
        for (GridTradeItem item : gridList) {
            income.add(item.getIncome());
        }
        return income;
    }
}
