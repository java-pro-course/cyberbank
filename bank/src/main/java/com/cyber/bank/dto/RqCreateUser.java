package com.cyber.bank.dto;

import lombok.Data;
import lombok.experimental.Accessors;
@Data
@Accessors(chain = true)
public class RqCreateUser {
    private String name;
    private String surname;
    private String email;
    private String password;
}
