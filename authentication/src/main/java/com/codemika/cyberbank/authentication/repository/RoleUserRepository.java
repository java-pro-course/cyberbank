package com.codemika.cyberbank.authentication.repository;

import com.codemika.cyberbank.authentication.entity.RoleEntity;
import com.codemika.cyberbank.authentication.entity.RoleUserEntity;
import com.codemika.cyberbank.authentication.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RoleUserRepository extends JpaRepository<RoleUserEntity, Long> {
    Optional<RoleUserEntity> getRoleUserEntitiesByUser(UserEntity user);
    @Modifying
    @Query("UPDATE RoleUserEntity roleUser SET roleUser.role = ?1 WHERE roleUser.id = ?2")
    void updateUserRole(RoleEntity role, Long id);
}
