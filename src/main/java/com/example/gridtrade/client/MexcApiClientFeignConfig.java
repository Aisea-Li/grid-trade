package com.example.gridtrade.client;

import com.example.gridtrade.utils.HmacSHA256Utils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;


public class MexcApiClientFeignConfig implements RequestInterceptor {

    @Value("${mexc.api.key:}")
    private String apiKey;

    @Value("${mexc.api.secret:}")
    private String apiSecret;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header("Content-Type", "application/x-www-form-urlencoded");
        requestTemplate.header("X-MEXC-APIKEY", apiKey);
        requestTemplate.query("timestamp", String.valueOf(new Date().getTime()));

        Map<String, Collection<String>> queries = new TreeMap<>(requestTemplate.queries());
        StringBuilder signValue = new StringBuilder();
        for (Map.Entry<String, Collection<String>> item : queries.entrySet()) {
            for (String value : item.getValue()) {
                if (signValue.length() > 0) {
                    signValue.append("&");
                }
                signValue.append(item.getKey()).append("=").append(value);
            }
        }

        String signature = "";
        try {
            signature = HmacSHA256Utils.hmacSHA256(apiSecret, signValue.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        requestTemplate.query("signature", signature);
    }
}
