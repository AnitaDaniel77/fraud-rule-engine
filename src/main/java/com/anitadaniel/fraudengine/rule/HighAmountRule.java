package com.anitadaniel.fraudengine.rule;

import com.anitadaniel.fraudengine.domain.FraudFlag;
import com.anitadaniel.fraudengine.domain.Severity;
import com.anitadaniel.fraudengine.domain.Transaction;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// flags any transaction above a set amount
@Component
public class HighAmountRule implements FraudRule {

    // limit comes from application.properties, 10000 if it is missing
    private final BigDecimal threshold;

    public HighAmountRule(@Value("${fraud.rules.high-amount.threshold:10000}") BigDecimal threshold) {
        this.threshold = threshold;
    }

    @Override
    public Optional<FraudFlag> check(Transaction transaction) {
        // compareTo gives 1 when the amount is bigger than the threshold
        if (transaction.getAmount().compareTo(threshold) > 0) {
            return Optional.of(new FraudFlag(transaction, "HIGH_AMOUNT", Severity.HIGH,
                    "Amount " + transaction.getAmount() + " " + transaction.getCurrency()
                            + " is above the limit of " + threshold));
        }
        return Optional.empty();
    }
}