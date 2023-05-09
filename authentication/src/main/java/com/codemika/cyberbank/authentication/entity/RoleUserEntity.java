package com.codemika.cyberbank.authentication.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * Это вспомогательная таблица для связи many-to-many между сущностями ПОЛЬЗОВАТЕЛЯ и РОЛИ
 */
@Data
@Entity
@Accessors(chain = true)
@Table(schema = "cyberbank_auth", name = "role-user")
public class RoleUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private RoleEntity role;

}
