package com.codemika.cyberbank.card.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RqCreateCreditCard {
    private Long value;
    private Long monthlyIncome; //ежемесячный доход для расчета платежеспособности
    private int creditTerm; // В месяцах
    private String title;
    private String pincode;

}
