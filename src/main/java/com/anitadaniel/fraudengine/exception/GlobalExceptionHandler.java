package com.anitadaniel.fraudengine.exception;

import com.anitadaniel.fraudengine.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice // watches every controller for errors
public class GlobalExceptionHandler {

    // runs when @Valid rejects a request
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>(); // keeps fields in order
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) { // each failed rule
            errors.put(fieldError.getField(), fieldError.getDefaultMessage()); // field -> message
        }
        ErrorResponse body = new ErrorResponse(400, "Validation failed", errors);
        return ResponseEntity.badRequest().body(body); // 400 Bad Request
    }

    // runs when the body is missing or is not valid JSON
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex) {
        ErrorResponse body = new ErrorResponse(400, "Request body is missing or not valid JSON", Map.of());
        return ResponseEntity.badRequest().body(body);
    }
}