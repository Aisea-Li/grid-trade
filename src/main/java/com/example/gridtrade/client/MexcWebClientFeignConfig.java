package com.example.gridtrade.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;


public class MexcWebClientFeignConfig implements RequestInterceptor {

    @Value("${mexc.web.token:}")
    private String token;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        String cookieValue = String.format("u_id=%s; uc_token=%s;", token, token);
        requestTemplate.header("cookie", cookieValue);
    }

}
