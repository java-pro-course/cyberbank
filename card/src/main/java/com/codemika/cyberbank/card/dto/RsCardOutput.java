package com.codemika.cyberbank.card.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RsCardOutput {

    private String title;
    private String accountNumber;
    private Long balance;
    private int creditTerm;
}
