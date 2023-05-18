package com.codemika.cyberbank.card.repository;

import com.codemika.cyberbank.card.entity.CreditCardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCardEntity, Long> {
    List<CreditCardEntity> findAllByOwnerUserId(Long id);
    Optional<CreditCardEntity> findCardByAccountNumber(String AccountNumber);
    Optional<CreditCardEntity> findAllByAccountNumber(String AccountNumber);
    @Modifying
    @Query("UPDATE CreditCardEntity card SET card.balance = ?1 WHERE card.id = ?2")
    void moneyTransfer(Long value, Long id);
    @Modifying
    @Query("UPDATE CreditCardEntity card SET card.title = ?1 WHERE card.id = ?2")
    void updateCardTitle(String title, Long id);
    @Modifying
    @Query("UPDATE CreditCardEntity card SET card.pincode = ?1 WHERE card.id = ?2")
    void updateCardPinCode(String pincode, Long id);
}
