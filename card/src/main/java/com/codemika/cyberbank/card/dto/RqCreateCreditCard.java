package com.codemika.cyberbank.card.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RqCreateCreditCard {
    private Long value; // количество требуемых денег
    private Long monthlyIncome; //ежемесячный доход для расчета платежеспособности
    private int creditTerm; // срок кредита в месяцах
    private String title;
    private String pincode;

}
