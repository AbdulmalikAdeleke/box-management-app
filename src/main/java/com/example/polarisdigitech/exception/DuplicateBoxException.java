package com.example.polarisdigitech.exception;

public class DuplicateBoxException extends RuntimeException {

    public DuplicateBoxException(String txref) {
        super("A box with txref already exists: " + txref);
    }
}