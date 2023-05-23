package com.codemika.cyberbank.authentication.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Класс для выдачи информации модерам.
 * ВСЕГДА ИСПОЛЬЗОВАТЬ ЭТОТ! При использовании Entity будет StackOverFlow + выводятся пароли, а это не безопасно
 */
@Data
@Accessors(chain = true)
public class RsInfoUserPro {
    private Long id;
    private String name;
    private String surname;
    private String patronymic;
    private String phone;
    private String email;
}
