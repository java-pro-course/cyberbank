package com.codemika.cyberbank.authentication.repository;

import com.codemika.cyberbank.authentication.entity.RoleUserEntity;
import com.codemika.cyberbank.authentication.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleUserRepository extends JpaRepository<RoleUserEntity, Long> {
    List<RoleUserEntity> findAllByUser(UserEntity user);
}
