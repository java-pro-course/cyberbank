package com.codemika.cyberbank.authentication.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Образ ответа после создания пользователя
 */
@Data
@Accessors(chain = true)
public class RsInfoUser {
    private String name;
    private String surname;
    private String patronymic;
}
