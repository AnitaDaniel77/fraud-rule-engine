package com.anitadaniel.fraudengine.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import java.math.RoundingMode;

// one card transaction received by the service, saved in the transactions table
@Entity
@Table(name = "transactions")
public class Transaction {

    // random unique id, like an ID number, so no two transactions clash
    @Id
    private String id = UUID.randomUUID().toString();

    // required: every transaction must belong to an account
    @Column(nullable = false)
    private String accountId;

    // exact decimal, never double: 19 digits in total, 2 after the point
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    // currency code like ZAR, always 3 letters
    @Column(nullable = false, length = 3)
    private String currency;

    // kind of spend, like GROCERIES or GAMBLING
    @Column(nullable = false)
    private String category;

    // optional details, may be empty
    private String merchant;
    private String country;

    // when purchase happened
    @Column(nullable = false)
    private Instant occurredAt;

    // when the service received it, filled in automatically
    @Column(nullable = false)
    private Instant receivedAt = Instant.now();

    // empty constructor, only for JPA to rebuild objects from table rows
    protected Transaction() {
    }

    // builds a new transaction, id and receivedAt fill themselves in
    public Transaction(String accountId, BigDecimal amount, String currency, String category, String merchant, String country, Instant occurredAt) {
        this.accountId = accountId;
        this.amount = amount.setScale(2, RoundingMode.HALF_UP); // always keep two decimal places
        this.currency = currency;
        this.category = category;
        this.merchant = merchant;
        this.country = country;
        this.occurredAt = occurredAt;
    }

    // getters only, so a saved transaction can't be changed by accident
    public String getId() {
        return id;
    }

    public String getAccountId() {
        return accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getCategory() {
        return category;
    }

    public String getMerchant() {
        return merchant;
    }

    public String getCountry() {
        return country;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }

}