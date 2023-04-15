package com.codemika.cyberbank.card.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Образ для создания карты
 */
@Data
@Accessors(chain = true)
public class RqCreateCard {
    private String title; // название карты
    private String type; // тип карты
    private String pincode; // пин-код карты
}
