package com.example.gridtrade.client;

import com.example.gridtrade.entity.response.OrderRes;
import com.example.gridtrade.entity.response.QueryOrderRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(
        name = "mexcApiClient",
        url = "${mexc.api.url:https://api.mexc.co}",
        configuration = {MexcApiClientFeignConfig.class},
        fallbackFactory = MexcApiClientFallbackFactory.class
)
public interface MexcApiClient {

    @PostMapping("/api/v3/order/test")
    OrderRes orderTest();

    @GetMapping("/api/v3/selfSymbols")
    String canTradeSymbols();

    @PostMapping("/api/v3/order")
    OrderRes placeOrder(
            @RequestParam("symbol") String symbol,
            @RequestParam("side") String side,
            @RequestParam("type") String type,
            @RequestParam(value = "quantity", required = false) String quantity,
            @RequestParam(value = "quoteOrderQty", required = false) String quoteOrderQty,
            @RequestParam(value = "price", required = false) String price,
            @RequestParam(value = "newClientOrderId", required = false) String newClientOrderId
    );

    @GetMapping("/api/v3/order")
    QueryOrderRes queryOrder(
            @RequestParam("symbol") String symbol,
            @RequestParam(value = "origClientOrderId", required = false) String origClientOrderId,
            @RequestParam(value = "orderId", required = false) String orderId

    );
}

@Slf4j
@Component
class MexcApiClientFallbackFactory implements FallbackFactory<MexcApiClient> {

    @Override
    public MexcApiClient create(Throwable cause) {
        log.warn("MexcApiClient,request fail,msg:" + cause.getMessage(), cause);
        return new MexcApiClient() {

            @Override
            public OrderRes orderTest() {
                return null;
            }

            @Override
            public String canTradeSymbols() {
                return null;
            }

            @Override
            public OrderRes placeOrder(String symbol, String side, String type, String quantity, String quoteOrderQty, String price, String newClientOrderId) {
                return null;
            }

            @Override
            public QueryOrderRes queryOrder(String symbol, String origClientOrderId, String orderId) {
                return null;
            }
        };
    }
}
