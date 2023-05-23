package com.codemika.cyberbank.authentication.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.List;

/**
 * Это entity для РОЛЕЙ.
 * Все роли:
 * USER - основная роль, присваивается всем зарегистрированным пользователям.
 * MODER - роль для сотрудников банка, есть доступ к списку пользователей и карт сайта.
 * TESTER - роль ТОЛЬКО для тестировщиков, имеет доступ ко ВСЕМУ.
 */
@Data
@Entity
@Accessors(chain = true)
@Table(schema = "cyberbank_auth", name = "role")
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role")
    private String role;

    @OneToMany(mappedBy = "role")
    private List<RoleUserEntity> userRoles;
}
