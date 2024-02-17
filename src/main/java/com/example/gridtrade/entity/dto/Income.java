package com.example.gridtrade.entity.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Income {

    private double cost;

    private double quantity;

    private long tradeTimes;

    private double realizedIncome;

    public void add(Income income) {
        this.cost += income.getCost();
        this.quantity += income.getQuantity();
        this.tradeTimes += income.getTradeTimes();
        this.realizedIncome += income.getRealizedIncome();
    }

}
