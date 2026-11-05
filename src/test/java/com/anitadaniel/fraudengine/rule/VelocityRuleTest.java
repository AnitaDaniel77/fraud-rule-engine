package com.anitadaniel.fraudengine.rule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.anitadaniel.fraudengine.domain.FraudFlag;
import com.anitadaniel.fraudengine.domain.Severity;
import com.anitadaniel.fraudengine.domain.Transaction;
import com.anitadaniel.fraudengine.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.junit.jupiter.api.Test;

// checks that VelocityRule flags an account with too many transactions in the time window
class VelocityRuleTest {

    // fake repository, so no database is needed
    private final TransactionRepository repository = mock(TransactionRepository.class);

    // the rule under test: more than 5 transactions in 10 minutes is flagged
    private final VelocityRule rule = new VelocityRule(repository, 5, 10);

    // a fixed moment, so the test gives the same result every time
    private final Instant now = Instant.parse("2026-11-05T10:00:00Z");

    // builds a transaction for account ACC-1 at the fixed moment
    private Transaction transaction() {
        return new Transaction("ACC-1", new BigDecimal("100.00"), "ZAR", "GROCERIES", "Shop", "ZA", now);
    }

    // tells the fake repository which count to answer with
    private void repositoryCountsTransactions(long count) {
        when(repository.countByAccountIdAndOccurredAtBetween(anyString(), any(Instant.class), any(Instant.class)))
                .thenReturn(count);
    }

    @Test
    void flagsWhenCountIsAboveLimit() {
        repositoryCountsTransactions(6);

        Optional<FraudFlag> result = rule.check(transaction());

        assertThat(result).isPresent();
        assertThat(result.get().getRuleCode()).isEqualTo("VELOCITY");
        assertThat(result.get().getSeverity()).isEqualTo(Severity.HIGH);
    }

    @Test
    void doesNotFlagWhenCountIsExactlyAtLimit() {
        // the rule says "more than 5", so 5 is allowed
        repositoryCountsTransactions(5);

        Optional<FraudFlag> result = rule.check(transaction());

        assertThat(result).isEmpty();
    }

    @Test
    void asksRepositoryForTheLastTenMinutes() {
        repositoryCountsTransactions(1);

        rule.check(transaction());

        // the window must run from 10 minutes before the transaction up to the transaction
        Instant tenMinutesEarlier = now.minus(10, ChronoUnit.MINUTES);
        verify(repository).countByAccountIdAndOccurredAtBetween("ACC-1", tenMinutesEarlier, now);
    }
}