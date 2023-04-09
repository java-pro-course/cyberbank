package com.codemika.cyberbank.card.repository;

import com.codemika.cyberbank.card.entity.CardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<CardEntity, Long> {
    List<CardEntity> findAllByOwnerUserId(Long id);
    @Modifying
    @Query("UPDATE CardEntity card SET card.balance = ?1 WHERE card.id = ?2")
    void moneyTransfer(Long value, Long id);
}
