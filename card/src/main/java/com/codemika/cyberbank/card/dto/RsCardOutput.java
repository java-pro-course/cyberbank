package com.codemika.cyberbank.card.dto;

import com.codemika.cyberbank.card.entity.CreditCardEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class RsCardOutput {

    private Long amount;

    private List<CreditCardEntity> list;
}
