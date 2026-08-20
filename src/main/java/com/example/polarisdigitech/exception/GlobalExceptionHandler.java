package com.example.polarisdigitech.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BoxNotFoundException.class)
    public ResponseEntity<?> handleBoxNotFound(
            BoxNotFoundException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "error",
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(DuplicateBoxException.class)
    public ResponseEntity<?> handleDuplicateBox(
            DuplicateBoxException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "error",
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(ExceededBoxCapacityException.class)
    public ResponseEntity<?> handleCapacity(
            ExceededBoxCapacityException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "error", ex.getMessage(),
                        "currentWeight",
                        ex.getCurrentWeight(),
                        "itemWeight",
                        ex.getItemWeight(),
                        "maximumCapacity",
                        ex.getMaximumCapacity(),
                        "remainingCapacity",
                        ex.getRemainingCapacity()
                ));
    }

    @ExceptionHandler(InsufficientBatteryException.class)
    public ResponseEntity<?> handleBattery(
            InsufficientBatteryException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "error",
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(InvalidBoxStateException.class)
    public ResponseEntity<?> handleInvalidState(
            InvalidBoxStateException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "error",
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(
            MethodArgumentNotValidException ex
    ) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Invalid request");

        return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "error",
                        message
                ));
    }
}