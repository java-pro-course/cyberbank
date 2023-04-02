package com.codemika.cyberbank.card.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RqCreateCard {
    private String title;
    private String type;
    private String pincode;
    private Long ownerUserId;
}
