package com.anitadaniel.fraudengine.dto;

import com.anitadaniel.fraudengine.domain.Transaction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionRequest(
        @NotBlank String accountId, // must not be empty or spaces
        @NotNull @Positive BigDecimal amount, // must be there and above zero
        @NotBlank @Pattern(regexp = "[A-Z]{3}", message = "must be 3 capital letters, like ZAR") String currency,
        @NotBlank String category,
        @NotBlank String merchant,
        @NotBlank String country,
        @NotNull Instant occurredAt) { // when the purchase happened, in UTC

    // turns the request into the entity we save
    public Transaction toEntity() {
        return new Transaction(accountId, amount, currency, category, merchant, country, occurredAt);
    }
}