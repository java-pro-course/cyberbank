package com.codemika.cyberbank.card.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Образ для создания карты
 */
@Data
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RqCreateCard {
    private String title; // название карты
    private String type; // тип карты
    private String pincode; // пин-код карты
}
