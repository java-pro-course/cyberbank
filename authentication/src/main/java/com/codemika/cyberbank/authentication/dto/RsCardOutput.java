package com.codemika.cyberbank.authentication.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RsCardOutput {

    private String title;
    private Long balance;
    private int creditTerm;
}
