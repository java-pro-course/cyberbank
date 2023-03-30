package com.codemika.cyberbank.authentication.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Класс запроса на логин, но вход реализован через токен
 */
@Data
@Accessors(chain = true)
public class RqLoginUser {
    private String phone;
    private String password;

}