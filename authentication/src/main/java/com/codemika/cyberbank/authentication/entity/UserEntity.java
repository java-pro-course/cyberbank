package com.codemika.cyberbank.authentication.entity;
import lombok.Data;
import lombok.experimental.Accessors;
import javax.persistence.*;
import java.util.List;

/**
 * Entity для пользователя
 */
@Entity
@Table(schema = "cyberbank_auth", name = "user")
@Data
@Accessors(chain = true)
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "patronymic")
    private String patronymic;

    /**
     * Поле "phone" (номер телефона) используется для входа, как логин
     */
    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @OneToMany(mappedBy = "user")
    private List<RoleUserEntity> userRoles;
}