package com.anitadaniel.fraudengine.rule;

import static org.assertj.core.api.Assertions.assertThat;

import com.anitadaniel.fraudengine.domain.FraudFlag;
import com.anitadaniel.fraudengine.domain.Severity;
import com.anitadaniel.fraudengine.domain.Transaction;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;

// checks that HighAmountRule flags the right transactions and leaves the rest alone
class HighAmountRuleTest {

    // the rule under test, with the limit set to 10000
    private final HighAmountRule rule = new HighAmountRule(new BigDecimal("10000"));

    // builds a transaction with the given amount, everything else is fixed
    private Transaction transactionOf(String amount) {
        return new Transaction("ACC-1", new BigDecimal(amount), "ZAR", "GROCERIES", "Shop", "ZA", Instant.now());
    }

    @Test
    void flagsAmountAboveLimit() {
        // one cent over the limit
        Optional<FraudFlag> result = rule.check(transactionOf("10000.01"));

        assertThat(result).isPresent();
        assertThat(result.get().getRuleCode()).isEqualTo("HIGH_AMOUNT");
        assertThat(result.get().getSeverity()).isEqualTo(Severity.HIGH);
    }

    @Test
    void doesNotFlagAmountExactlyAtLimit() {
        // the rule says "above", so the limit itself is allowed
        Optional<FraudFlag> result = rule.check(transactionOf("10000.00"));

        assertThat(result).isEmpty();
    }

    @Test
    void doesNotFlagSmallAmount() {
        Optional<FraudFlag> result = rule.check(transactionOf("50.00"));

        assertThat(result).isEmpty();
    }
}