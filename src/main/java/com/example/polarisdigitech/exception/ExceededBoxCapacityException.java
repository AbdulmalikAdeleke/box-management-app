package com.example.polarisdigitech.exception;

import lombok.Getter;

@Getter
public class ExceededBoxCapacityException
        extends RuntimeException {

    private final int currentWeight;
    private final int itemWeight;
    private final int maximumCapacity;
    private final int remainingCapacity;

    public ExceededBoxCapacityException(
            int currentWeight,
            int itemWeight,
            int maximumCapacity
    ) {
        super("Item exceeds box capacity");

        this.currentWeight = currentWeight;
        this.itemWeight = itemWeight;
        this.maximumCapacity = maximumCapacity;
        this.remainingCapacity = maximumCapacity - currentWeight;
    }
}