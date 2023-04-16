package com.codemika.cyberbank.card.repository;

import com.codemika.cyberbank.card.entity.CardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<CardEntity, Long> {
  @Modifying
    @Query("UPDATE CardEntity card SET card.title = ?1, card.type = ?2, card.account_number = ?3, card.owner_user_id = ?4, card.isFrozen = ?5 WHERE card.id = ?6")
    void updateById(String title, String type, String accountNumber, Long ownerUserId, Boolean isFrozen, Long id);
}
