package com.codemika.cyberbank.card.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

public class HistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne // доделать связь с CardEntity
    private CardEntity fromCard;

    @OneToOne // доделать связь с CardEntity
    private CardEntity toCard;

    private Long amount;


    // внутри сервиса
    // 1. найти fromCard с помощью CardRepository
    // 2. то же самое с toCard
    // 3. Сохрнать через новый repository (HistoryRepository)
}
