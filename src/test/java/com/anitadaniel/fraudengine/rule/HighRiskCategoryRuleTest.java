package com.anitadaniel.fraudengine.rule;

import static org.assertj.core.api.Assertions.assertThat;

import com.anitadaniel.fraudengine.domain.FraudFlag;
import com.anitadaniel.fraudengine.domain.Severity;
import com.anitadaniel.fraudengine.domain.Transaction;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

// checks that HighRiskCategoryRule flags only categories on the watch list
class HighRiskCategoryRuleTest {

    // the rule under test, with a watch list of two categories
    private final HighRiskCategoryRule rule = new HighRiskCategoryRule(List.of("GAMBLING", "CRYPTO"));

    // builds a transaction with the given category, everything else is fixed
    private Transaction transactionIn(String category) {
        return new Transaction("ACC-1", new BigDecimal("100.00"), "ZAR", category, "Shop", "ZA", Instant.now());
    }

    @Test
    void flagsCategoryOnWatchList() {
        Optional<FraudFlag> result = rule.check(transactionIn("GAMBLING"));

        assertThat(result).isPresent();
        assertThat(result.get().getRuleCode()).isEqualTo("HIGH_RISK_CATEGORY");
        assertThat(result.get().getSeverity()).isEqualTo(Severity.MEDIUM);
    }

    @Test
    void ignoresUpperOrLowerCase() {
        // "gambling" in small letters must still be caught
        Optional<FraudFlag> result = rule.check(transactionIn("gambling"));

        assertThat(result).isPresent();
    }

    @Test
    void doesNotFlagNormalCategory() {
        Optional<FraudFlag> result = rule.check(transactionIn("GROCERIES"));

        assertThat(result).isEmpty();
    }
}