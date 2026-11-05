package com.anitadaniel.fraudengine.controller;

import com.anitadaniel.fraudengine.domain.FraudFlag;
import com.anitadaniel.fraudengine.domain.Transaction;
import com.anitadaniel.fraudengine.dto.FraudFlagResponse;
import com.anitadaniel.fraudengine.dto.TransactionRequest;
import com.anitadaniel.fraudengine.dto.TransactionResponse;
import com.anitadaniel.fraudengine.service.FraudCheckService;
import com.anitadaniel.fraudengine.service.TransactionQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController // this class answers web requests and replies with JSON
@RequestMapping("/api/transactions") // every URL in here starts with this
@Tag(name = "Transactions", description = "Submit transactions and look them up with their fraud flags")
public class TransactionController {

    private final FraudCheckService fraudCheckService;
    private final TransactionQueryService queryService;

    public TransactionController(FraudCheckService fraudCheckService, TransactionQueryService queryService) {
        this.fraudCheckService = fraudCheckService;
        this.queryService = queryService;
    }

    @PostMapping // runs when someone sends a POST to /api/transactions
    @Operation(summary = "Submit a transaction",
            description = "Saves the transaction, runs every fraud rule on it, stores any flags raised, and returns both.")
    public ResponseEntity<TransactionResponse> create(@Valid @RequestBody TransactionRequest request) {
        Transaction transaction = request.toEntity(); // request becomes an entity
        List<FraudFlag> flags = fraudCheckService.process(transaction); // save and run the rules
        TransactionResponse body = TransactionResponse.from(transaction, flags); // build the reply
        return ResponseEntity.status(HttpStatus.CREATED).body(body); // reply with 201 Created
    }

    @GetMapping("/{id}") // GET /api/transactions/some-id
    @Operation(summary = "Get a transaction",
            description = "Returns one transaction with its flags. Replies 404 if the id does not exist.")
    public TransactionResponse getOne(@PathVariable("id") String id) {
        Transaction transaction = queryService.getTransaction(id); // 404 if it does not exist
        List<FraudFlag> flags = queryService.getFlagsForTransaction(id);
        return TransactionResponse.from(transaction, flags);
    }

    @GetMapping("/{id}/flags") // GET /api/transactions/some-id/flags
    @Operation(summary = "List the flags for a transaction",
            description = "Returns only the flags raised for one transaction. Replies 404 if the id does not exist.")
    public List<FraudFlagResponse> getFlags(@PathVariable("id") String id) {
        List<FraudFlagResponse> result = new ArrayList<>(); // empty list to fill
        for (FraudFlag flag : queryService.getFlagsForTransaction(id)) { // each flag found
            result.add(FraudFlagResponse.from(flag)); // convert and add
        }
        return result;
    }
}