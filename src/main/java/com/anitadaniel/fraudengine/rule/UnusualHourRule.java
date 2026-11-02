package com.anitadaniel.fraudengine.rule;

import com.anitadaniel.fraudengine.domain.FraudFlag;
import com.anitadaniel.fraudengine.domain.Severity;
import com.anitadaniel.fraudengine.domain.Transaction;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// flags large amountsspent in the aerly hours of the morning
@Component
public class UnusualHourRule implements FraudRule {

    // the hours are read as South african time
    private static final ZoneId ZONE = ZoneId.of("Africa/Johannesburg");

    private final int startHour;
    private final int endHour;
    private final BigDecimal minAmount;

    public UnusualHourRule(@Value("${fraud.rules.unusual-hour.start-hour:0}") int startHour,
                           @Value("${fraud.rules.unusual-hour.end-hour:6}") int endHour,
                           @Value("${fraud.rules.unusual-hour.min-amount:1000}") BigDecimal minAmount) {
        this.startHour = startHour;
        this.endHour = endHour;
        this.minAmount = minAmount;
    }

    @Override
    public Optional<FraudFlag> check(Transaction transaction) {
        // hour of the day, 0 to 23, in South African time
        int hour = transaction.getOccurredAt().atZone(ZONE).getHour();
        boolean atNight = hour >= startHour && hour < endHour;
        boolean bigEnough = transaction.getAmount().compareTo(minAmount) >= 0;

        if (atNight && bigEnough) {
            return Optional.of(new FraudFlag(transaction, "UNUSUAL_HOUR", Severity.LOW, "Amount " + transaction.getAmount() + "spent at " + hour + ":00"));
        }
        return Optional.empty();
    }
}