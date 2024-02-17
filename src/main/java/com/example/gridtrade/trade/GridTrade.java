package com.example.gridtrade.trade;

import com.example.gridtrade.entity.dto.Income;
import com.example.gridtrade.trade.platform.TradePlatform;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;


@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GridTrade {

    protected String currency;

    protected String market;

    protected double amount;

    protected double lowPrice;

    protected double highPrice;

    protected int gridNum;

    private boolean buyStart = true;

    @JsonIgnore
    protected TradePlatform tradePlatform;

    @JsonIgnore
    protected ThreadPoolTaskExecutor executor;

    @Builder.Default
    protected double maxGridAmount = 5.1;

    @Builder.Default
    protected int scale = 4;

    @Builder.Default
    protected List<GridTradeItem> gridList = new ArrayList<>();

    public void init() {
        if (StringUtils.isAnyBlank(currency, market)) {
            log.error("currency market is any blank,currency:{},market:{}", currency, market);
            throw new RuntimeException("currency market is any blank");
        }
        if (highPrice <= lowPrice) {
            log.error("highPrice less than lowPrice,low price:{},high price:{}", lowPrice, highPrice);
            throw new RuntimeException("highPrice <= lowPrice");
        }
        double gridPriceDiff = (highPrice - lowPrice) / gridNum;
        double gridAmount = amount / gridNum;
        if (gridAmount < maxGridAmount) {
            log.error("grid amount too less,grid amount:{},max gird amount:{}", gridAmount, maxGridAmount);
            throw new RuntimeException("grid amount too less");
        }
        for (int i = 0; i < gridNum; i++) {
            double buyPrice = setScale(lowPrice + gridPriceDiff * i);
            double sellPrice = setScale(lowPrice + gridPriceDiff * (i + 1));
            double quantity = setScale(gridAmount / buyPrice);
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
        }
    }

    public void execute() {
        if (!tradePlatform.refreshToken()) {
            return;
        }
        if (!tradePlatform.refreshCurrentOrder(currency, market, gridNum * 2)) {
            log.error("refresh current order fail.");
            return;
        }
        List<Future<?>> futureList = new ArrayList<>();
        for (GridTradeItem item : gridList) {
            Future<?> future = executor.submit(() -> item.checkAndTrade());
            futureList.add(future);
        }
        for (Future<?> future : futureList) {
            try {
                future.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void setTradePlatform(TradePlatform tradePlatform) {
        this.tradePlatform = tradePlatform;
        for (GridTradeItem item : gridList) {
            item.setTradePlatform(tradePlatform);
        }
    }

    protected double setScale(double value) {
        return new BigDecimal(value)
                .setScale(scale, BigDecimal.ROUND_DOWN)
                .doubleValue();
    }

    public Income getIncome() {
        Income income = Income.builder().build();
        for (GridTradeItem item : gridList) {
            income.add(item.getIncome());
        }
        return income;
    }
}
