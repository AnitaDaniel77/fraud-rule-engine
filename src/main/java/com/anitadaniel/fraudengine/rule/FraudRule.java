package com.anitadaniel.fraudengine.rule;

import com.anitadaniel.fraudengine.domain.FraudFlag;
import com.anitadaniel.fraudengine.domain.Transaction;
import java.util.Optional;

// every fraud rule must be able to check one transaction
public interface FraudRule {

    // returns a flag if the rule is broken, or empty if the transaction is fine
    Optional<FraudFlag> check(Transaction transaction);
}