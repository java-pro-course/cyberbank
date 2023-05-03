package com.codemika.cyberbank.authentication.repository;

import com.codemika.cyberbank.authentication.entity.RoleUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleUserRepository extends JpaRepository<RoleUserEntity, Long> {
}
