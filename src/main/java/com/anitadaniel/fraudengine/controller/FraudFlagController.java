
package com.anitadaniel.fraudengine.controller;

import com.anitadaniel.fraudengine.domain.FraudFlag;
import com.anitadaniel.fraudengine.dto.FraudFlagResponse;
import com.anitadaniel.fraudengine.service.TransactionQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/flags") // all flag searches live under this address
public class FraudFlagController {

    private final TransactionQueryService queryService;

    public FraudFlagController(TransactionQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping // GET /api/flags, with optional ?accountId=...&ruleCode=...
    public List<FraudFlagResponse> search(
            @RequestParam(name = "accountId", required = false) String accountId,
            @RequestParam(name = "ruleCode", required = false) String ruleCode) {
        List<FraudFlagResponse> result = new ArrayList<>(); // empty list to fill
        for (FraudFlag flag : queryService.searchFlags(accountId, ruleCode)) { // flags matching the filters
            result.add(FraudFlagResponse.from(flag)); // convert and add
        }
        return result;
    }
}