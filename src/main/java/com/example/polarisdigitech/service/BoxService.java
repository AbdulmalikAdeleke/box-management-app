package com.example.polarisdigitech.service;

import com.example.polarisdigitech.config.BoxProperties;
import com.example.polarisdigitech.dto.CreateBoxRequest;
import com.example.polarisdigitech.dto.LoadItemRequest;
import com.example.polarisdigitech.dto.UpdateBatteryPercentage;
import com.example.polarisdigitech.exception.ExceededBoxCapacityException;
import com.example.polarisdigitech.exception.InsufficientBatteryException;
import com.example.polarisdigitech.exception.InvalidBoxStateException;
import com.example.polarisdigitech.enums.BoxState;
import com.example.polarisdigitech.repository.BoxRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BoxService {

    private static final int MIN_LOADING_BATTERY = 25;

    private final BoxRepo boxRepository;
    private final BoxProperties boxProperties;

    public BoxService(
            BoxRepo boxRepository,
            BoxProperties boxProperties
    ) {
        this.boxRepository = boxRepository;
        this.boxProperties = boxProperties;
    }

    public long createBox(
            CreateBoxRequest request
    ) {

        return boxRepository.createBox(
                request.txref(),
                request.batteryPercentage()
        );
    }

    @Transactional
    public void loadItem(
            String txref,
            LoadItemRequest request
    ) {

        BoxRepo.BoxData box =
                boxRepository.findAndLockBox(txref);

        if (box.batteryPercentage()
                < MIN_LOADING_BATTERY) {

            throw new InsufficientBatteryException();
        }

        if (box.state() != BoxState.IDLE
                && box.state() != BoxState.LOADING) {

            throw new InvalidBoxStateException(
                    "Items cannot be loaded when box is " +box.state()+ "."
            );
        }

        int newWeight = box.currentWeight() + request.weight();

        if (newWeight> boxProperties.maxWeight()) {

            throw new ExceededBoxCapacityException(
                            box.currentWeight(),
                            request.weight(),
                            boxProperties.maxWeight()
                    );
        }

        boxRepository.insertItem(
                box.id(),
                request.name(),
                request.weight(),
                request.code()
        );

        boxRepository.updateWeight(
                box.id(),
                newWeight
        );

        if (box.state() == BoxState.IDLE) {

            boxRepository.updateState(
                    box.id(),
                    BoxState.LOADING
            );
        }
    }

    @Transactional
    public void finishLoading(
            String txref
    ) {

        BoxRepo.BoxData box = boxRepository.findAndLockBox(txref);
 
        requireState(
                box,
                BoxState.LOADING,
                "Box must be LOADING before loading can be completed"
        );

        boxRepository.updateState(
                box.id(),
                BoxState.LOADED
        );
    }

    @Transactional
    public void startDelivery(
            String txref
    ) {

        BoxRepo.BoxData box = boxRepository.findAndLockBox(txref);

        requireState(
                box,
                BoxState.LOADED,
                "Box must be LOADED before delivery can start"
        );

        boxRepository.updateState(
                box.id(),
                BoxState.DELIVERING
        );
    }

    @Transactional
    public void completeDelivery(
            String txref
    ) {

        BoxRepo.BoxData box = boxRepository.findAndLockBox(txref);

        requireState(
                box,
                BoxState.DELIVERING,
                "Box must be DELIVERING before delivery can be completed"
        );

        boxRepository.updateState(
                box.id(),
                BoxState.DELIVERED
        );
    }

    @Transactional
    public void startReturn(
            String txref
    ) {

        BoxRepo.BoxData box = boxRepository.findAndLockBox(txref);

        requireState(
                box,
                BoxState.DELIVERED,
                "Box must be DELIVERED before return can start"
        );

        boxRepository.updateState(
                box.id(),
                BoxState.RETURNING
        );
    }

    @Transactional
    public void updateBattery(
            String txref,
            UpdateBatteryPercentage request
    ) {

        BoxRepo.BoxData box = boxRepository.findAndLockBox(txref);

        boxRepository.updateBattery(
                box.id(),
                request.batteryPercentage()
        );

        if (request.batteryPercentage() < 25){
            boxRepository.updateState(
                 box.id(),
                 BoxState.LOW_BATTERY
            );
        }
      
    }

    public BoxState getBoxState(
            String txref
    ) {

        return boxRepository
                .findBox(txref)
                .state();
    }

    public int getBatteryPercentage(
            String txref
    ) {

        return boxRepository
                .findBox(txref)
                .batteryPercentage();
    }

    public List<BoxRepo.ItemData> getItems(
            String txref
    ) {

        return boxRepository.getItems(txref);
    }

    public List<BoxRepo.BoxData> getAvailableBoxes() {

        return boxRepository.findAvailableBoxes();
    }

    public BoxRepo.BoxData getBox(
            String txref
    ) {

        return boxRepository.findBox(txref);
    }

    private void requireState(
            BoxRepo.BoxData box,
            BoxState expected,
            String message
    ) {

        if (box.state() != expected) {
            throw new InvalidBoxStateException(message);
        }
    }
}