package com.codemika.cyberbank.authentication.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Образ запроса на логин
 */
@Data
@Accessors(chain = true)
public class RqLoginUser {
    private String phone;
    private String password;

}