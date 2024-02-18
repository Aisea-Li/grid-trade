package com.example.gridtrade.utils;


import com.example.gridtrade.entity.response.Response;

import java.util.Objects;

public class ResponseUtils {

    public static boolean hasData(Response<?> response) {
        return Objects.nonNull(response) && response.hasData();
    }

    public static boolean isSuccess(Response<?> response) {
        return Objects.nonNull(response) && response.isSuccess();
    }
}
