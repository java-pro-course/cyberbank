package com.codemika.cyberbank.authentication.repository;

import com.codemika.cyberbank.authentication.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
        void findByEmail(String email);
        void findByPhone(String phone);
}
