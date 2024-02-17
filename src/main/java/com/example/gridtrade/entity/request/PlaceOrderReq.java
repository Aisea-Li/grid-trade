package com.example.gridtrade.entity.request;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class PlaceOrderReq {
/*
 {
 "currency":"MX",
 "market":"USDT",
 "tradeType":"BUY",
 "price":"2.8",
 "quantity":"10",
 "orderType":"LIMIT_ORDER",
 "p0":"ruuELx7BWEtr6slZKqqFYElkNKM3XTmsQYI8rSC7mZiBDl6LJE7QXxPrPUA9bO273vR9oHlg8eX316xdFZ4dWXiYEo1aEeQddh7e11sF0WcklKXVz6mVXPqRK5t9BOk0VzlhyTOfBHuucUyW261i0S/k1WP7DHjHOej9MdbNSnxWzwPv/KHkJFbABvD2sA6zrw7MYzmmCU/xEfkedxBTTtjqhZct4hIBDSeBkZ6V9EejZiIUsJCCzD6NKpfeqfZg+Zv8VHh50emz5vW2IApIqPxj/NsprA/ZyQhVSm+6S8+7JV6qUQmZr9z2tzZmT7T3wZpUOyM8RfxAlQ==",
 "k0":"lWSUfAXeFpcj/NCFAviLLkUvK5grGpXaGPbnI9ZM5hEeNp9Vao83IVPJ5QBjZnO/Fw2ZqLA7EoAAFiX6xMmFijCG99JK3LLEHiCuRGBmO8TgwqJrtt7r0rBsr7LyYKhGqkFOu/HbBSO4gsnxbpUb6zPW2uQgoufiOHnholAfDUrrXBU91exsWVvW2sK2JvK2IRXZZUxMo4xaj0s+uFV7x1J8jFJ80Xa8jNVJDqFwJKLLFhj+WE4HGEPFz9Bo+Yohwwzn5n3lM36Gt4YCzqom4vCln6lnQuyntEIoVkRpB/o8TXv5b3H9SljD7Ayq6VcA1QLsGzCHDJCji3Jlx9IuZQ==",
 "chash":"d58ada125a10b6fe52326e380988d091340278c0b1b872c354e8c5d1dad77d32",
 "mtoken":"c2e5e13ee0c50a96962cb3ef2d5d8a3d",
 "ts":1707117670430,
 "mhash":"726a98d575d35aa360228d95e6d56f63"
 }
 */

    /**
     * 交易币种
     * MX
     */
    private String currency;

    /**
     * 价值锚定币种
     * USDT
     */
    private String market;

    /**
     * 交易类型
     * BUY-买入 SELL-卖出
     */
    private String tradeType;

    /**
     * 订单类型
     * LIMIT_ORDER-限价单
     */
    private String orderType;

    /**
     * 价格
     */
    private String price;

    /**
     * 数量
     */
    private String quantity;
}
