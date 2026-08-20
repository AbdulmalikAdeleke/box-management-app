package com.example.polarisdigitech.exception;

public class ExceededBoxCapacityException extends RuntimeException {

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

    public int getCurrentWeight() {
        return currentWeight;
    }

    public int getItemWeight() {
        return itemWeight;
    }

    public int getMaximumCapacity() {
        return maximumCapacity;
    }

    public int getRemainingCapacity() {
        return remainingCapacity;
    }
}