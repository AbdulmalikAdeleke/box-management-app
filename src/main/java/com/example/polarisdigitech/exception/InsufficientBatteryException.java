package com.example.polarisdigitech.exception;

public class InsufficientBatteryException
        extends RuntimeException {

    public InsufficientBatteryException() {
        super("Box battery must be at least 25% for loading operations");
    }
}