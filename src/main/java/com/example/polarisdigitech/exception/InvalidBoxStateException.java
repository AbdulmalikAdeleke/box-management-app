package com.example.polarisdigitech.exception;

public class InvalidBoxStateException
        extends RuntimeException {

    public InvalidBoxStateException(String message) {
        super(message);
    }
}