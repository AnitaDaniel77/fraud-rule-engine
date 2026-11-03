package com.anitadaniel.fraudengine.dto;

import java.util.Map;

public record ErrorResponse(
        int status, // the HTTP status code, like 400
        String message, // a short summary of what went wrong
        Map<String, String> errors) { // field name -> what is wrong with it
}