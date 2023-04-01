package com.codemika.cyberbank.authentication.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Класс запроса на создание пользователя
 */
@Data
@Accessors(chain = true)
public class RqCreateUser {
    private String name;
    private String surname;
    private String patronymic;
    private String phone;
    private String email;
    private String password;
}
