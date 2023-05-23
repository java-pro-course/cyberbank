package com.codemika.cyberbank.card.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Entity
@Table(schema = "cyberbank_card", name = "credit_card")
@Data
@Accessors(chain = true)
public class CreditCardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "balance")
    private Long balance;

    @Column(name = "type")
    private String type;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "pin_code")
    private String pincode;

    @Column(name = "owner_user_id")
    private Long ownerUserId;

    @Column(name = "credit_term")
    private int creditTerm;

    @Column(name = "is_active")
    private Boolean isActive = true; //активна или нет

}
