package com.anitadaniel.fraudengine.dto;

import com.anitadaniel.fraudengine.domain.FraudFlag;
import com.anitadaniel.fraudengine.domain.Severity;

import java.time.Instant;

public record FraudFlagResponse(
        String id,
        String ruleCode,
        Severity severity,
        String reason,
        Instant createdAt) {

     // copies the fields to show from the entity
    public static FraudFlagResponse from(FraudFlag flag) {
        return new FraudFlagResponse(flag.getId(), flag.getRuleCode(), flag.getSeverity(),
                flag.getReason(), flag.getCreatedAt());
    }
}