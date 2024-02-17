package com.example.gridtrade.run;

import com.example.gridtrade.entity.dto.GridTradeStart;
import com.example.gridtrade.trade.GridTrade;
import com.example.gridtrade.trade.platform.TradePlatform;
import com.example.gridtrade.utils.FileUtils;
import com.example.gridtrade.utils.JacksonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.LockSupport;


@Slf4j
@Component
public class TradeRunner implements CommandLineRunner {

    @Autowired
    @Qualifier("mexcTradePlatform")
    private TradePlatform tradePlatform;

    private final static ThreadPoolTaskExecutor EXECUTOR = new ThreadPoolTaskExecutor();

    static {
        EXECUTOR.setCorePoolSize(20);
        EXECUTOR.setMaxPoolSize(20);
        EXECUTOR.setQueueCapacity(1000);
        EXECUTOR.setAllowCoreThreadTimeOut(true);
        EXECUTOR.setThreadNamePrefix("thread-pool-");
        EXECUTOR.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        EXECUTOR.initialize();
    }

    @Override
    public void run(String... args) {
        String startFilePath = "./grid-trade-start.json";
        String saveFilePath = "./grid-trade-cache.json";
        String switchFilePath = "./switch.txt";
        Duration checkInterval = Duration.ofSeconds(10);

        GridTrade gridTrade = loadGridTrade(
                startFilePath,
                saveFilePath,
                tradePlatform,
                EXECUTOR
        );

        FileUtils.writeFile(switchFilePath, "1");

        while (isContinue(switchFilePath)) {
            gridTrade.execute();
            cache(gridTrade, saveFilePath);
            LockSupport.parkNanos(checkInterval.toNanos());
        }

        EXECUTOR.shutdown();
    }

    protected GridTrade loadGridTrade(
            String startFilePath,
            String saveFilePath,
            TradePlatform tradePlatform,
            ThreadPoolTaskExecutor executor
    ) {
        String cacheJson = FileUtils.readFile(saveFilePath);
        GridTrade gridTrade;
        if (StringUtils.isNotBlank(cacheJson)) {
            gridTrade = JacksonUtils.readValue(cacheJson, GridTrade.class);
            if (Objects.isNull(gridTrade)) {
                log.error("load cache fail,cache json:{}", cacheJson);
                throw new RuntimeException();
            }
        } else {
            String startJson = FileUtils.readFile(startFilePath);
            GridTradeStart gridTradeStart = JacksonUtils.readValue(startJson, GridTradeStart.class);
            if (Objects.isNull(gridTradeStart)) {
                log.error("GridTradeStart,is null,start json:{}", startJson);
                throw new RuntimeException();
            }
            gridTrade = GridTrade.builder()
                    .currency(gridTradeStart.getCurrency())
                    .market(gridTradeStart.getMarket())
                    .amount(gridTradeStart.getAmount())
                    .lowPrice(gridTradeStart.getLowPrice())
                    .highPrice(gridTradeStart.getHighPrice())
                    .gridNum(gridTradeStart.getGridNum())
                    .buyStart(gridTradeStart.getBuyStart())
                    .tradePlatform(tradePlatform)
                    .build();
            gridTrade.init();
        }
        gridTrade.setTradePlatform(tradePlatform);
        gridTrade.setExecutor(executor);
        return gridTrade;
    }

    protected boolean isContinue(String switchFilePath) {
        return new File(switchFilePath).exists();
    }

    protected void cache(GridTrade gridTrade, String saveFilePath) {
        String jsonCache = JacksonUtils.writeValueAsString(gridTrade);
        if (StringUtils.isBlank(jsonCache)) {
            log.error("write to json fail.");
            throw new RuntimeException();
        }
        FileUtils.writeFile(saveFilePath, jsonCache);
    }
}
