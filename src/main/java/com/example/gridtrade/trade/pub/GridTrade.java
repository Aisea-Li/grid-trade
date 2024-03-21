package com.example.gridtrade.trade.pub;

import com.example.gridtrade.entity.dto.Income;
import com.example.gridtrade.trade.FixedDiffGridTrade;
import com.example.gridtrade.trade.FixedRatioGridTrade;
import com.example.gridtrade.trade.platform.TradePlatform;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * 抽象网格交易
 */
@Slf4j
@Data
@SuperBuilder
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = {
        @JsonSubTypes.Type(value = FixedDiffGridTrade.class, name = "FIXED_DIFF_GRID_TRADE"),
        @JsonSubTypes.Type(value = FixedRatioGridTrade.class, name = "FIXED_RATIO_GRID_TRADE"),
})
public abstract class GridTrade {

    protected int code;

    protected String currency;

    protected String market;

    protected double amount;

    protected double lowPrice;

    protected double highPrice;

    protected double startPrice;

    protected int gridNum;

    protected boolean buyStart = true;

    @JsonIgnore
    protected TradePlatform tradePlatform;

    @JsonIgnore
    protected ThreadPoolTaskExecutor executor;

    @Builder.Default
    protected double minGridAmount = 5.1;

    @Builder.Default
    protected int scale = 4;

    @Builder.Default
    protected List<GridTradeItem> gridList = new ArrayList<>();

    public abstract void init();

    public abstract String getType();

    public void execute() {
        if (!tradePlatform.refreshCurrentOrder(currency, market, gridNum * 2)) {
            log.error("refresh current order fail,currency:{},market:{}", currency, market);
            return;
        }
        List<Future<?>> futureList = new ArrayList<>();
        for (GridTradeItem item : gridList) {
            Future<?> future = executor.submit(item::checkAndTrade);
            futureList.add(future);
        }
        for (Future<?> future : futureList) {
            try {
                future.get();
            } catch (Exception e) {
                log.error("trade item exception,currency:{},market:{}", currency, market, e);
            }
        }
    }

    public void setTradePlatform(TradePlatform tradePlatform) {
        this.tradePlatform = tradePlatform;
        for (GridTradeItem item : gridList) {
            item.setTradePlatform(tradePlatform);
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
