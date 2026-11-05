package com.anitadaniel.fraudengine.controller;

import com.anitadaniel.fraudengine.domain.FraudFlag;
import com.anitadaniel.fraudengine.dto.FraudFlagResponse;
import com.anitadaniel.fraudengine.service.TransactionQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/flags") // all flag searches live under this address
@Tag(name = "Fraud flags", description = "Search the flags raised by the fraud rules")
public class FraudFlagController {

    private final TransactionQueryService queryService;

    public FraudFlagController(TransactionQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping // GET /api/flags, with optional ?accountId=...&ruleCode=...
    @Operation(summary = "Search fraud flags",
            description = "Returns every flag, or only those for one account, one rule, or both.")
    public List<FraudFlagResponse> search(
            @Parameter(description = "Only flags on this account, for example ACC-2")
            @RequestParam(name = "accountId", required = false) String accountId,
            @Parameter(description = "Only flags from this rule: HIGH_AMOUNT, HIGH_RISK_CATEGORY, UNUSUAL_HOUR or VELOCITY")
            @RequestParam(name = "ruleCode", required = false) String ruleCode) {
        List<FraudFlagResponse> result = new ArrayList<>(); // empty list to fill
        for (FraudFlag flag : queryService.searchFlags(accountId, ruleCode)) { // flags matching the filters
            result.add(FraudFlagResponse.from(flag)); // convert and add
        }
        return result;
    }
}