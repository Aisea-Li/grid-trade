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
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class GridTradeTask {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    @Qualifier("mexcTradePlatform")
    private TradePlatform tradePlatform;

    private GridTrade gridTrade;

    private final static String START_FILE_PATH = "./grid-trade-start.json";
    private final static String CACHE_FILE_PATH = "./grid-trade-cache.json";
    private final static String SWITCH_FILE_PATH = "./switch.txt";

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

    @PostConstruct
    public void init() {
        gridTrade = loadGridTrade();
        // 启动标识
        FileUtils.writeFile(SWITCH_FILE_PATH, "1");
    }

    @Scheduled(fixedRate = 10, timeUnit = TimeUnit.SECONDS)
    public void execute() {
        // 是否继续执行
        if (!isContinue()) {
            // 停止程序
            log.info("do shut down");
            EXECUTOR.shutdown();
            System.exit(SpringApplication.exit(applicationContext));
            return;
        }
        // 执行
        gridTrade.execute();
        // 缓存
        cache();
    }

    private GridTrade loadGridTrade() {
        String cacheJson = FileUtils.readFile(CACHE_FILE_PATH);
        GridTrade gridTrade;
        if (StringUtils.isNotBlank(cacheJson)) {
            gridTrade = JacksonUtils.readValue(cacheJson, GridTrade.class);
            if (Objects.isNull(gridTrade)) {
                log.error("load cache fail,cache json:{}", cacheJson);
                throw new RuntimeException();
            }
        } else {
            String startJson = FileUtils.readFile(START_FILE_PATH);
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
        gridTrade.setExecutor(EXECUTOR);
        return gridTrade;
    }

    private boolean isContinue() {
        return new File(SWITCH_FILE_PATH).exists();
    }

    private void cache() {
        String jsonCache = JacksonUtils.writeValueAsString(gridTrade);
        if (StringUtils.isBlank(jsonCache)) {
            log.error("write to json fail.");
            throw new RuntimeException();
        }
        FileUtils.writeFile(CACHE_FILE_PATH, jsonCache);
    }
}
