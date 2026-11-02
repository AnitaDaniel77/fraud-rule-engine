package com.anitadaniel.fraudengine.rule;

import com.anitadaniel.fraudengine.domain.FraudFlag;
import com.anitadaniel.fraudengine.domain.Severity;
import com.anitadaniel.fraudengine.domain.Transaction;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// flags spending in categories on a watch list
@Component
public class HighRiskCategoryRule implements FraudRule {

    // watch list comes from application.properties
    private final List<String> riskyCategories;

    public HighRiskCategoryRule(@Value("${fraud.rules.high-risk-category.watch-list:GAMBLING,CRYPTO}") List<String> riskyCategories) {
        this.riskyCategories = riskyCategories;
    }

    @Override
    public Optional<FraudFlag> check(Transaction transaction) {
        // upper case so "gambling" and "GAMBLING" are treated the same
        String category = transaction.getCategory().toUpperCase();
        if (riskyCategories.contains(category)) {
            return Optional.of(new FraudFlag(transaction, "HIGH_RISK_CATEGORY", Severity.MEDIUM,
                    "Category " + category + " is on the high-risk list"));
        }
        return Optional.empty();
    }
}