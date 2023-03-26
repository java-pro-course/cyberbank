package com.codemika.cyberbank.authentication.repository;

import com.codemika.cyberbank.authentication.entity.UserEntity;
import lombok.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Это репозиторий для пользователя.
 * Содержит методы для поиска по эл. почте и номеру телефона.
 */
@Repository

public interface UserRepository extends JpaRepository<UserEntity, Long> {
        boolean existByEmail(String email);
        UserEntity findByEmail(String email);
        UserEntity findByPhone(String phone);
}
