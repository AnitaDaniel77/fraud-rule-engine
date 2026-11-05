package com.anitadaniel.fraudengine.rule;

import static org.assertj.core.api.Assertions.assertThat;

import com.anitadaniel.fraudengine.domain.FraudFlag;
import com.anitadaniel.fraudengine.domain.Severity;
import com.anitadaniel.fraudengine.domain.Transaction;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;

// checks that UnusualHourRule flags big amounts spent between 00:00 and 06:00 South African time
class UnusualHourRuleTest {

    // the rule under test: night is hour 0 up to 6, minimum amount 1000
    private final UnusualHourRule rule = new UnusualHourRule(0, 6, new BigDecimal("1000"));

    // builds a transaction at the given South African time, like 2026-11-05T02:15:00+02:00
    private Transaction transactionAt(String amount, String southAfricanTime) {
        Instant when = OffsetDateTime.parse(southAfricanTime).toInstant();
        return new Transaction("ACC-1", new BigDecimal(amount), "ZAR", "GROCERIES", "Shop", "ZA", when);
    }

    @Test
    void flagsBigAmountAtNight() {
        Optional<FraudFlag> result = rule.check(transactionAt("5000.00", "2026-11-05T02:15:00+02:00"));

        assertThat(result).isPresent();
        assertThat(result.get().getRuleCode()).isEqualTo("UNUSUAL_HOUR");
        assertThat(result.get().getSeverity()).isEqualTo(Severity.LOW);
    }

    @Test
    void flagsAmountExactlyAtMinimum() {
        // the rule says "1000 or more", so 1000 itself is flagged
        Optional<FraudFlag> result = rule.check(transactionAt("1000.00", "2026-11-05T03:00:00+02:00"));

        assertThat(result).isPresent();
    }

    @Test
    void doesNotFlagSmallAmountAtNight() {
        Optional<FraudFlag> result = rule.check(transactionAt("999.99", "2026-11-05T03:00:00+02:00"));

        assertThat(result).isEmpty();
    }

    @Test
    void doesNotFlagBigAmountInTheDay() {
        Optional<FraudFlag> result = rule.check(transactionAt("5000.00", "2026-11-05T14:00:00+02:00"));

        assertThat(result).isEmpty();
    }

    @Test
    void doesNotFlagAtSixInTheMorning() {
        // the night ends at 06:00, so 06:00 itself counts as morning
        Optional<FraudFlag> result = rule.check(transactionAt("5000.00", "2026-11-05T06:00:00+02:00"));

        assertThat(result).isEmpty();
    }

    @Test
    void readsTimeAsSouthAfricanTimeNotUtc() {
        // 01:00 UTC is 03:00 in South Africa, so this is night
        Instant oneAmUtc = Instant.parse("2026-11-05T01:00:00Z");
        Transaction transaction = new Transaction("ACC-1", new BigDecimal("5000.00"), "ZAR", "GROCERIES", "Shop", "ZA", oneAmUtc);

        assertThat(rule.check(transaction)).isPresent();
    }
}