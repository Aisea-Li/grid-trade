package com.example.gridtrade.run;

import com.example.gridtrade.entity.dto.GridTradeStart;
import com.example.gridtrade.entity.enums.GridTradeType;
import com.example.gridtrade.trade.FixedDiffGridTrade;
import com.example.gridtrade.trade.FixedRatioGridTrade;
import com.example.gridtrade.trade.platform.TradePlatform;
import com.example.gridtrade.trade.pub.GridTrade;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;
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

    private final List<GridTrade> gridTradeList = new ArrayList<>();

    private final static String START_FILE_PATH = "./grid-trade-start-%d.json";
    private final static String CACHE_FILE_PATH = "./grid-trade-cache-%d.json";

    private final static String SWITCH_FILE_PATH = "./switch.txt";

    private final static ThreadPoolTaskExecutor EXECUTOR = new ThreadPoolTaskExecutor();

    static {
        EXECUTOR.setCorePoolSize(100);
        EXECUTOR.setMaxPoolSize(100);
        EXECUTOR.setQueueCapacity(5000);
        EXECUTOR.setAllowCoreThreadTimeOut(true);
        EXECUTOR.setThreadNamePrefix("thread-pool-");
        EXECUTOR.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        EXECUTOR.initialize();
    }

    @PostConstruct
    public void init() {
        // 加载网格
        for (int i = 0; ; i++) {
            String startFilePath = String.format(START_FILE_PATH, i);
            String cacheFilePath = String.format(CACHE_FILE_PATH, i);
            GridTrade gridTrade = loadGridTrade(startFilePath, cacheFilePath, tradePlatform, EXECUTOR);
            if (Objects.isNull(gridTrade)) {
                break;
            }
            gridTrade.setCode(i);
            gridTradeList.add(gridTrade);
        }
        // 启动标识
        FileUtils.writeFile(SWITCH_FILE_PATH, "1");
    }

    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.SECONDS)
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
        List<Future<?>> futureList = new ArrayList<>();
        for (GridTrade gridTrade : gridTradeList) {
            Future<?> future = EXECUTOR.submit(() -> {
                // 执行
                gridTrade.execute();
                // 缓存
                String cacheFilePath = String.format(CACHE_FILE_PATH, gridTrade.getCode());
                cache(gridTrade, cacheFilePath);
            });
            futureList.add(future);
        }
        // 结果同步
        for (Future<?> future : futureList) {
            try {
                future.get();
            } catch (Exception e) {
                log.error("grid trade execute fail", e);
            }
        }
    }

    private GridTrade loadGridTrade(String startFilePath, String cacheFilePath, TradePlatform tradePlatform, ThreadPoolTaskExecutor executor) {
        GridTrade gridTrade;
        if (FileUtils.exists(cacheFilePath)) {
            // 由缓存json恢复运行
            String cacheJson = FileUtils.readFile(cacheFilePath);
            gridTrade = JacksonUtils.readValue(cacheJson, GridTrade.class);
            if (Objects.isNull(gridTrade)) {
                log.error("load cache fail,cache json:{}", cacheJson);
                throw new RuntimeException();
            }
        } else if (FileUtils.exists(startFilePath)) {
            // 由启动json开始运行
            String startJson = FileUtils.readFile(startFilePath);
            GridTradeStart gridTradeStart = JacksonUtils.readValue(startJson, GridTradeStart.class);
            if (Objects.isNull(gridTradeStart)) {
                log.error("GridTradeStart,is null,start json:{}", startJson);
                throw new RuntimeException();
            }
            if (StringUtils.equals(GridTradeType.FIXED_DIFF_GRID_TRADE.name(), gridTradeStart.getType())) {
                gridTrade = FixedDiffGridTrade.builder()
                        .currency(gridTradeStart.getCurrency())
                        .market(gridTradeStart.getMarket())
                        .amount(gridTradeStart.getAmount())
                        .lowPrice(gridTradeStart.getLowPrice())
                        .highPrice(gridTradeStart.getHighPrice())
                        .gridNum(gridTradeStart.getGridNum())
                        .scale(gridTradeStart.getScale())
                        .buyStart(gridTradeStart.getBuyStart())
                        .build();
            } else if (StringUtils.equals(GridTradeType.FIXED_RATIO_GRID_TRADE.name(), gridTradeStart.getType())) {
                gridTrade = FixedRatioGridTrade.builder()
                        .currency(gridTradeStart.getCurrency())
                        .market(gridTradeStart.getMarket())
                        .amount(gridTradeStart.getAmount())
                        .lowPrice(gridTradeStart.getLowPrice())
                        .fixedRatio(gridTradeStart.getFixedRatio())
                        .gridNum(gridTradeStart.getGridNum())
                        .scale(gridTradeStart.getScale())
                        .buyStart(gridTradeStart.getBuyStart())
                        .build();
            } else {
                log.error("unknown type,type:{}", gridTradeStart.getType());
                return null;
            }
            gridTrade.init();
        } else {
            return null;
        }
        gridTrade.setTradePlatform(tradePlatform);
        gridTrade.setExecutor(executor);
        return gridTrade;
    }

    private boolean isContinue() {
        return FileUtils.exists(SWITCH_FILE_PATH);
    }

    private void cache(GridTrade gridTrade, String cacheFilePath) {
        String jsonCache = JacksonUtils.writeValueAsString(gridTrade);
        if (StringUtils.isBlank(jsonCache)) {
            log.error("write to json fail.");
            throw new RuntimeException();
        }
        FileUtils.writeFile(cacheFilePath, jsonCache);
    }
}
