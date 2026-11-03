package com.anitadaniel.fraudengine.controller;

import com.anitadaniel.fraudengine.domain.FraudFlag;
import com.anitadaniel.fraudengine.domain.Transaction;
import com.anitadaniel.fraudengine.dto.TransactionRequest;
import com.anitadaniel.fraudengine.dto.TransactionResponse;
import com.anitadaniel.fraudengine.service.FraudCheckService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController // this class answers web requests and replies with JSON
@RequestMapping("/api/transactions") // every URL in here starts with this
public class TransactionController {

    private final FraudCheckService fraudCheckService;

    public TransactionController(FraudCheckService fraudCheckService) {
        this.fraudCheckService = fraudCheckService;
    }

    @PostMapping // runs when someone sends a POST to /api/transactions
    public ResponseEntity<TransactionResponse> create(@Valid @RequestBody TransactionRequest request) {
        Transaction transaction = request.toEntity(); // request becomes an entity
        List<FraudFlag> flags = fraudCheckService.process(transaction); // save and run the rules
        TransactionResponse body = TransactionResponse.from(transaction, flags); // build the reply
        return ResponseEntity.status(HttpStatus.CREATED).body(body); // reply with 201 Created
    }
}