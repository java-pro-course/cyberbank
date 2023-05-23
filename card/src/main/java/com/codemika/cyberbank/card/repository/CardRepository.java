package com.codemika.cyberbank.card.repository;

import com.codemika.cyberbank.card.entity.CardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для карт
 */
@Repository
public interface CardRepository extends JpaRepository<CardEntity, Long> {

  @Modifying
    @Query("UPDATE CardEntity card SET card.isActive = ?1 WHERE card.id = ?2")
    void updateById(Boolean isActive, Long id);

    List<CardEntity> findAllByOwnerUserId(Long id);

    Optional<CardEntity> findCardByAccountNumber(String AccountNumber);

    Optional<CardEntity> findAllByAccountNumber(String AccountNumber);

    @Modifying
    @Query("UPDATE CardEntity card SET card.balance = ?1 WHERE card.id = ?2")
    void moneyTransfer(Long value, Long id);

    @Modifying
    @Query("UPDATE CardEntity card SET card.title = ?1 WHERE card.id = ?2")
    void updateCardTitle(String title, Long id);
    //Прога не запускается если писать как в БД pin_code. Надо pincode
    @Modifying
    @Query("UPDATE CardEntity card SET card.pincode = ?1 WHERE card.id = ?2")
    void updateCardPinCode(String pincode, Long id);
}
