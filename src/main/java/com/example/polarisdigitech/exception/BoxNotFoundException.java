package com.example.polarisdigitech.exception;

public class BoxNotFoundException extends RuntimeException {

    public BoxNotFoundException(String txref) {
        super("Box not found: " + txref);
    }
}