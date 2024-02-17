package com.example.gridtrade.client;

import com.example.gridtrade.entity.request.PlaceOrderReq;
import com.example.gridtrade.entity.response.CurrentOrder;
import com.example.gridtrade.entity.response.HistoryOrder;
import com.example.gridtrade.entity.response.Order;
import com.example.gridtrade.entity.response.Page;
import com.example.gridtrade.entity.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@FeignClient(
        name = "mexcWebClient",
        url = "${mexc.web.url:https://www.mexc.co}",
        configuration = {MexcWebClientFeignConfig.class},
        fallbackFactory = MexcWebClientFallbackFactory.class
)
public interface MexcWebClient {

    /**
     * 下单
     *
     * @param req
     * @return
     */
    @ResponseBody
    @PostMapping("/api/platform/spot/order/place")
    Response<String> placeOrder(@RequestBody PlaceOrderReq req);


    /**
     * 当前订单
     *
     * @return
     */
    @ResponseBody
    @GetMapping("/api/platform/spot/order/current/orders/v2")
    Response<Page<CurrentOrder>> queryCurrentOrders(
            @RequestParam("currency") String currency,
            @RequestParam("market") String market,
            @RequestParam("orderTypes") String orderTypes,
            @RequestParam(value = "pageNum") Integer pageNum,
            @RequestParam(value = "pageSize") Integer pageSize
    );

    /**
     * 历史订单
     *
     * @return
     */
    @ResponseBody
    @GetMapping("/api/platform/spot/order/history/orders/v2")
    Response<Page<HistoryOrder>> queryHistoryOrders(
            @RequestParam("states") String states,
            @RequestParam("startTime") Long startTime,
            @RequestParam("endTime") Long endTime,
            @RequestParam(value = "pageNum") Integer pageNum,
            @RequestParam(value = "pageSize") Integer pageSize
    );

    /**
     * 订单详情
     *
     * @return
     */
    @ResponseBody
    @GetMapping("/api/platform/spot/order/deal/detail")
    Response<Order> queryOrderDetail(
            @RequestParam("orderId") String orderId,
            @RequestParam("orderType") String orderType
    );


    /**
     * 全部撤单
     *
     * @return
     */
    @ResponseBody
    @DeleteMapping("/api/platform/spot/order/cancel/v2")
    Response<?> cancelOrder();


    /**
     * token校验续期
     *
     * @return
     */
    @ResponseBody
    @PostMapping("/ucenter/api/login/validation")
    Response<?> validation();
}

@Slf4j
@Component
class MexcWebClientFallbackFactory implements FallbackFactory<MexcWebClient> {

    @Override
    public MexcWebClient create(Throwable cause) {
        log.warn("MexcWebClient,request fail,msg:" + cause.getMessage(), cause);
        return new MexcWebClient() {
            @Override
            public Response<String> placeOrder(PlaceOrderReq req) {
                return null;
            }

            @Override
            public Response<Page<CurrentOrder>> queryCurrentOrders(String currency, String market, String orderTypes, Integer pageNum, Integer pageSize) {
                return null;
            }

            @Override
            public Response<Page<HistoryOrder>> queryHistoryOrders(String states, Long startTime, Long endTime, Integer pageNum, Integer pageSize) {
                return null;
            }

            @Override
            public Response<Order> queryOrderDetail(String orderId, String orderType) {
                return null;
            }

            @Override
            public Response<?> cancelOrder() {
                return null;
            }

            @Override
            public Response<?> validation() {
                return null;
            }
        };
    }
}