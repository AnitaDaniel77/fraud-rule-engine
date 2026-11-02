package com.anitadaniel.fraudengine.rule;

import com.anitadaniel.fraudengine.domain.FraudFlag;
import com.anitadaniel.fraudengine.domain.Severity;
import com.anitadaniel.fraudengine.domain.Transaction;
import com.anitadaniel.fraudengine.repository.TransactionRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// flags an account that makes too many transactions in a short time
@Component
public class VelocityRule implements FraudRule {

    private final TransactionRepository transactionRepository;
    private final int maxTransactions;
    private final long windowMinutes;

    // Spring passes in the repository, the limit and the window
    public VelocityRule(TransactionRepository transactionRepository,
                        @Value("${fraud.rules.velocity.max-transactions:5}") int maxTransactions,
                        @Value("${fraud.rules.velocity.window-minutes:10}") long windowMinutes) {
        this.transactionRepository = transactionRepository;
        this.maxTransactions = maxTransactions;
        this.windowMinutes = windowMinutes;
    }

    @Override
    public Optional<FraudFlag> check(Transaction transaction) {
        // the window ends at this transaction and starts windowMinutes earlier
        Instant to = transaction.getOccurredAt();
        Instant from = to.minus(windowMinutes, ChronoUnit.MINUTES);

        // includes this transaction too, because it is saved before the rules run
        long count = transactionRepository.countByAccountIdAndOccurredAtBetween(
                transaction.getAccountId(), from, to);

        if (count > maxTransactions) {
            return Optional.of(new FraudFlag(transaction, "VELOCITY", Severity.HIGH,
                    count + " transactions on this account within " + windowMinutes + " minutes"));
        }
        return Optional.empty();
    }
}