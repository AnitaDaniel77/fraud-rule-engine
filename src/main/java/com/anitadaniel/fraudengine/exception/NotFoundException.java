package com.anitadaniel.fraudengine.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
       super(message); // keep the message to show the caller
    }
}