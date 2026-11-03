package com.anitadaniel.fraudengine.dto;

import com.anitadaniel.fraudengine.domain.FraudFlag;
import com.anitadaniel.fraudengine.domain.Transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public record TransactionResponse(
        String id,
        String accountId,
        BigDecimal amount,
        String currency,
        String category,
        String merchant,
        String country,
        Instant occurredAt,
        Instant receivedAt,
        List<FraudFlagResponse> flags) {

    // builds the response from a transaction and the flags found for it
    public static TransactionResponse from(Transaction t, List<FraudFlag> flags) {
        List<FraudFlagResponse> flagDtos = new ArrayList<>(); // empty list to fill
        for (FraudFlag flag : flags) { // go through each flag
            flagDtos.add(FraudFlagResponse.from(flag)); // convert it and add it
        }
        return new TransactionResponse(t.getId(), t.getAccountId(), t.getAmount(), t.getCurrency(),
                t.getCategory(), t.getMerchant(), t.getCountry(), t.getOccurredAt(), t.getReceivedAt(), flagDtos);
    }
}