package com.codemika.cyberbank.card.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Table(schema = "cyberbank_auth", name = "card")
@Data
@Accessors(chain = true)
/**
 * Entity для карт
 */
public class CardEntity {

    @Column(name = "title")
    private String title;

    @Column(name = "statement")
    private Long statement;

    @Column(name = "type")
    private String type;
}