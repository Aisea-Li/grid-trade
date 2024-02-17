package com.example.gridtrade.entity.response;

import lombok.Data;

import java.util.Objects;


@Data
public class Response<DATA> {

    private Integer code;

    private String msg;

    private DATA data;

    private Long timestamp;

    public boolean isSuccess() {
        return Objects.nonNull(code) && (code.equals(0) || code.equals(200));
    }

    public boolean hasData() {
        return isSuccess() && Objects.nonNull(data);
    }
}
