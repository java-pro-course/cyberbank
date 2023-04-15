package com.codemika.cyberbank.card.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Образ для создания дебетовой карты
 */
@Data
@Accessors(chain = true)
public class RqCreateDebetCard {
    private String title;
    private String pincode;
}
