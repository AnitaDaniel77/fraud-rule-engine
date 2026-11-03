package com.anitadaniel.fraudengine.repository;

import com.anitadaniel.fraudengine.domain.FraudFlag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// saves and finds fraud flags
public interface FraudFlagRepository extends JpaRepository<FraudFlag, String> {

    // all flags raised for one transaction
    List<FraudFlag> findByTransactionId(String transactionId);
    List<FraudFlag> findByTransactionAccountId(String accountId); // flags for one account
    List<FraudFlag> findByRuleCode(String ruleCode); // flags from one rule
    List<FraudFlag> findByTransactionAccountIdAndRuleCode(String accountId, String ruleCode); // both
}