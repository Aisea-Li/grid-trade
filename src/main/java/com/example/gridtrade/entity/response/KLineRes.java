package com.example.gridtrade.entity.response;

import lombok.Data;

import java.util.List;

@Data
public class KLineRes {

    /**
     * 数量
     */
    private Integer s;

    /**
     * 时间 小->大
     */
    private List<Long> t;

    /**
     * 开盘价
     */
    private List<Double> o;

    /**
     * 收盘价
     */
    private List<Double> c;

    /**
     * 最高价
     */
    private List<Double> h;

    /**
     * 最低价
     */
    private List<Double> l;

    /**
     * 成交量
     */
    private List<Double> q;

    /**
     * 成交额
     */
    private List<Double> v;

}
