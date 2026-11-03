package com.anitadaniel.fraudengine.service;

import com.anitadaniel.fraudengine.domain.FraudFlag;
import com.anitadaniel.fraudengine.domain.Transaction;
import com.anitadaniel.fraudengine.exception.NotFoundException;
import com.anitadaniel.fraudengine.repository.FraudFlagRepository;
import com.anitadaniel.fraudengine.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true) // these methods only read, never change data
public class TransactionQueryService {

    private final TransactionRepository transactionRepository;
    private final FraudFlagRepository fraudFlagRepository;

    public TransactionQueryService(TransactionRepository transactionRepository,
                                   FraudFlagRepository fraudFlagRepository) {
        this.transactionRepository = transactionRepository;
        this.fraudFlagRepository = fraudFlagRepository;
    }

    // find one transaction or fail with 404
    public Transaction getTransaction(String id) {
        Optional<Transaction> found = transactionRepository.findById(id);
        if (found.isEmpty()) {
            throw new NotFoundException("Transaction " + id + " not found");
        }
        return found.get();
    }

    // flags for one transaction, 404 if the transaction does not exist
    public List<FraudFlag> getFlagsForTransaction(String transactionId) {
        getTransaction(transactionId); // checks it exists first
        return fraudFlagRepository.findByTransactionId(transactionId);
    }

    // search flags, using whichever filters the caller gave
    public List<FraudFlag> searchFlags(String accountId, String ruleCode) {
        if (accountId != null && ruleCode != null) {
            return fraudFlagRepository.findByTransactionAccountIdAndRuleCode(accountId, ruleCode);
        }
        if (accountId != null) {
            return fraudFlagRepository.findByTransactionAccountId(accountId);
        }
        if (ruleCode != null) {
            return fraudFlagRepository.findByRuleCode(ruleCode);
        }
        return fraudFlagRepository.findAll(); // no filters, return everything
    }
}