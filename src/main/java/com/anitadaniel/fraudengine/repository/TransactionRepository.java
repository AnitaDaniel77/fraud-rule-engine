package com.anitadaniel.fraudengine.repository;

import com.anitadaniel.fraudengine.domain.Transaction;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;

// saves and finds transactions
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    // how many transactions an account made in a time window, for the velocity rule
    long countByAccountIdAndOccurredAtBetween(String accountId, Instant from, Instant to);
}