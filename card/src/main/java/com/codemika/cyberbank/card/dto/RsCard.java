package com.codemika.cyberbank.card.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RsCard {
    private Long id;
    private String title;
    private String type;
}
