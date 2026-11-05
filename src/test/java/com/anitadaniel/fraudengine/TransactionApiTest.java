package com.anitadaniel.fraudengine;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

// starts the whole service on a random free port and calls it over HTTP like a real client
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test") // use the in-memory test database
class TransactionApiTest {

    // the port Spring picked for this test run
    @Value("${local.server.port}")
    private int port;

    // Java's built-in HTTP client, the code version of curl
    private final HttpClient client = HttpClient.newHttpClient();

    // sends a POST to /api/transactions with the given JSON body
    private HttpResponse<String> postTransaction(String json) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/transactions"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    // sends a GET to the given path
    private HttpResponse<String> getPath(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void highAmountTransactionReturns201WithFlag() throws Exception {
        // daytime and a normal category, so only the high amount rule fires
        HttpResponse<String> response = postTransaction("""
                {
                  "accountId": "ACC-API-1",
                  "amount": 15000,
                  "currency": "ZAR",
                  "category": "GROCERIES",
                  "merchant": "Test Shop",
                  "country": "ZA",
                  "occurredAt": "2026-11-05T12:00:00Z"
                }
                """);

        assertThat(response.statusCode()).isEqualTo(201);
        assertThat(response.body()).contains("\"ruleCode\":\"HIGH_AMOUNT\"");
        assertThat(response.body()).contains("15000.00");
    }

    @Test
    void invalidTransactionReturns400() throws Exception {
        // a negative amount breaks the @Positive rule
        HttpResponse<String> response = postTransaction("""
                {
                  "accountId": "ACC-API-2",
                  "amount": -5,
                  "currency": "ZAR",
                  "category": "GROCERIES",
                  "merchant": "Test Shop",
                  "country": "ZA",
                  "occurredAt": "2026-11-05T12:00:00Z"
                }
                """);

        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(response.body()).contains("amount");
    }

    @Test
    void unknownTransactionReturns404() throws Exception {
        HttpResponse<String> response = getPath("/api/transactions/does-not-exist");

        assertThat(response.statusCode()).isEqualTo(404);
    }
}