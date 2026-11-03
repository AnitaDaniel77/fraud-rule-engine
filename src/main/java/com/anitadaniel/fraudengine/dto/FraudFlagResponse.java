package com.anitadaniel.fraudengine.dto;

import com.anitadaniel.fraudengine.domain.FraudFlag;
import com.anitadaniel.fraudengine.domain.Severity;

import java.time.Instant;

public record FraudFlagResponse(
        String id,
        String transactionId, // which transaction this flag belongs to
        String accountId, // which account that transaction was on
        String ruleCode,
        Severity severity,
        String reason,
        Instant createdAt) {

    // copies the fields to show from the entity
    public static FraudFlagResponse from(FraudFlag flag) {
        return new FraudFlagResponse(flag.getId(), flag.getTransaction().getId(),
                flag.getTransaction().getAccountId(), flag.getRuleCode(), flag.getSeverity(),
                flag.getReason(), flag.getCreatedAt());
    }
}