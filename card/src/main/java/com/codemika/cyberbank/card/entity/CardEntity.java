package com.codemika.cyberbank.card.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import javax.persistence.*;

/**
 * Entity для карт
 */
@Entity
@Table(schema = "cyberbank_card", name = "card")
@Data
@Accessors(chain = true)
public class CardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "balance")
    private Long balance;

    @Column(name = "type")
    private String type; // дебет / кредит

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "pin_code")
    private String pincode; //пин-код состоит из 4-х цифр.

    @Column(name = "owner_user_id")
    private Long ownerUserId;

    @Column(name = "status")
    private Boolean isFrozen = false;

}