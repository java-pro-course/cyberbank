package com.codemika.cyberbank.authentication.repository;

import com.codemika.cyberbank.authentication.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для пользователя.
 * Содержит методы для поиска по эл. почте и номеру телефона.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByPhone(String phone);
}
