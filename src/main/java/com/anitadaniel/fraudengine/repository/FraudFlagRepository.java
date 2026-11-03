package com.anitadaniel.fraudengine.repository;

import com.anitadaniel.fraudengine.domain.FraudFlag;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FraudFlagRepository extends JpaRepository<FraudFlag, String> {

    @EntityGraph(attributePaths = "transaction") // load the transaction together with the flags
    List<FraudFlag> findByTransactionId(String transactionId);

    @EntityGraph(attributePaths = "transaction")
    List<FraudFlag> findByTransactionAccountId(String accountId); // flags for one account

    @EntityGraph(attributePaths = "transaction")
    List<FraudFlag> findByRuleCode(String ruleCode); // flags from one rule

    @EntityGraph(attributePaths = "transaction")
    List<FraudFlag> findByTransactionAccountIdAndRuleCode(String accountId, String ruleCode); // both

    @Override
    @EntityGraph(attributePaths = "transaction")
    List<FraudFlag> findAll(); // same loading rule for the no-filter case
}