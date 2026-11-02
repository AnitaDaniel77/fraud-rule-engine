package com.anitadaniel.fraudengine.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

// one warning a rule raised about a transaction, saved in fraud-flags
@Entity
@Table(name = "fraud_flags")
public class FraudFlag{

    //random unique id, same idea as in Transaction
    @Id
    private String id = UUID.randomUUID().toString();

    // many flags can point to one transaction, this is the foreign key
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    // which rule fired , like HIGH_AMOUNT
    @Column(nullable = false)
    private String ruleCode;

    // saved as text (HIGH), not a number, so table stays radable
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity;

    // short sentence explaining why it was flagged
    @Column(nullable = false)
    private String reason;

    // when the flag was raised
    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    // empty construct, only  for JPA
    protected FraudFlag() {
    }

    // builds a flag for one transaction
    public FraudFlag(Transaction transaction, String ruleCode, Severity severity, String reason) {
        this.transaction = transaction;
        this.ruleCode = ruleCode;
        this.severity = severity;
        this.reason = reason;
    }

    // getters only, a flag never changes once raised
    public String getId() {
        return id;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public Severity getSeverity() {
        return severity;
    }

    public String getReason() {
        return reason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

}