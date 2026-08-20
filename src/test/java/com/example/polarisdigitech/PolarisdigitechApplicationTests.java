package com.example.polarisdigitech;

import com.example.polarisdigitech.enums.BoxState;
import com.example.polarisdigitech.repository.BoxRepo;
import com.example.polarisdigitech.service.BoxService;
import com.example.polarisdigitech.exception.InvalidBoxStateException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class PolarisDigitechApplicationTests {

    @Autowired
    private BoxRepo boxRepo;

    @Autowired
    private BoxService boxService;


    @Test
    void contextLoads() {
    }


    @Test
    @Transactional
    void shouldStartDeliveryWhenBoxIsLoaded() {

        // Create the box.
        long boxId = boxRepo.createBox(
                "TX-TEST-001",
                80
        );

        // Put the box into the state required
        // for starting delivery.
        boxRepo.updateState(
                boxId,
                BoxState.LOADED
        );

        // Start delivery.
        boxService.startDelivery("TX-TEST-001");

        // Read the box from the database.
        BoxRepo.BoxData box =
                boxRepo.findBox("TX-TEST-001");

        // Verify the state changed.
        assertEquals(
                BoxState.DELIVERING,
                box.state()
        );
    }


    @Test
    @Transactional
    void shouldCompleteDeliveryWhenBoxIsDelivering() {

        long boxId = boxRepo.createBox(
                "TX-TEST-002",
                80
        );

        boxRepo.updateState(
                boxId,
                BoxState.DELIVERING
        );

        boxService.completeDelivery("TX-TEST-002");

        BoxRepo.BoxData box =
                boxRepo.findBox("TX-TEST-002");

        assertEquals(
                BoxState.DELIVERED,
                box.state()
        );
    }


    @Test
    @Transactional
    void shouldStartReturnWhenBoxIsDelivered() {

        long boxId = boxRepo.createBox(
                "TX-TEST-003",
                80
        );

        boxRepo.updateState(
                boxId,
                BoxState.DELIVERED
        );

        boxService.startReturn("TX-TEST-003");

        BoxRepo.BoxData box =
                boxRepo.findBox("TX-TEST-003");

        assertEquals(
                BoxState.RETURNING,
                box.state()
        );
    }


    @Test
    @Transactional
    void shouldRejectStartingDeliveryWhenBoxIsNotLoaded() {

        long boxId = boxRepo.createBox(
                "TX-TEST-004",
                80
        );

        // Box is IDLE because createBox()
        // creates it as IDLE.

        assertThrows(
                InvalidBoxStateException.class,
                () -> boxService.startDelivery("TX-TEST-004")
        );

        BoxRepo.BoxData box =
                boxRepo.findBox("TX-TEST-004");

        // State must remain unchanged.
        assertEquals(
                BoxState.IDLE,
                box.state()
        );
    }


    @Test
    @Transactional
    void shouldRejectCompletingDeliveryWhenBoxIsNotDelivering() {

        long boxId = boxRepo.createBox(
                "TX-TEST-005",
                80
        );

        // createBox() starts as IDLE.
        // Change it to LOADED to test an invalid
        // completion transition.
        boxRepo.updateState(
                boxId,
                BoxState.LOADED
        );

        assertThrows(
                InvalidBoxStateException.class,
                () -> boxService.completeDelivery("TX-TEST-005")
        );

        BoxRepo.BoxData box =
                boxRepo.findBox("TX-TEST-005");

        assertEquals(
                BoxState.LOADED,
                box.state()
        );
    }


    @Test
    @Transactional
    void shouldRejectStartingReturnWhenBoxIsNotDelivered() {

        long boxId = boxRepo.createBox(
                "TX-TEST-006",
                80
        );

        boxRepo.updateState(
                boxId,
                BoxState.DELIVERING
        );

        assertThrows(
                InvalidBoxStateException.class,
                () -> boxService.startReturn("TX-TEST-006")
        );

        BoxRepo.BoxData box =
                boxRepo.findBox("TX-TEST-006");

        assertEquals(
                BoxState.DELIVERING,
                box.state()
        );
    }
}