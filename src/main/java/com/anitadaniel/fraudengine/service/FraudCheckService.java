package com.anitadaniel.fraudengine.service;

import com.anitadaniel.fraudengine.domain.FraudFlag;
import com.anitadaniel.fraudengine.domain.Transaction;
import com.anitadaniel.fraudengine.repository.FraudFlagRepository;
import com.anitadaniel.fraudengine.repository.TransactionRepository;
import com.anitadaniel.fraudengine.rule.FraudRule;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service // Spring creates one of these and manages it
public class FraudCheckService {

    private final TransactionRepository transactionRepository;
    private final FraudFlagRepository fraudFlagRepository;
    private final List<FraudRule> rules; // Spring fills this with all four rules

    public FraudCheckService(TransactionRepository transactionRepository,
                             FraudFlagRepository fraudFlagRepository,
                             List<FraudRule> rules) {
        this.transactionRepository = transactionRepository;
        this.fraudFlagRepository = fraudFlagRepository;
        this.rules = rules;
    }

    @Transactional // all the saves below succeed together or not at all
    public List<FraudFlag> process(Transaction transaction) {
        transactionRepository.save(transaction); // save first so VelocityRule counts it
        List<FraudFlag> flags = new ArrayList<>();
        for (FraudRule rule : rules) {
            rule.check(transaction).ifPresent(flags::add); // keep a flag only if the rule found one
        }
        fraudFlagRepository.saveAll(flags); // store every flag in one go
        return flags;
    }
}