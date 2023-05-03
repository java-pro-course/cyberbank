package com.codemika.cyberbank.authentication.entity;
import lombok.Data;
import lombok.experimental.Accessors;
import javax.persistence.*;
import java.util.List;

/**
 * Entity для роли
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
