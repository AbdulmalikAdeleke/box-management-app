package com.example.polarisdigitech.controller;

import com.example.polarisdigitech.dto.CreateBoxRequest;
import com.example.polarisdigitech.dto.LoadItemRequest;
import com.example.polarisdigitech.dto.UpdateBatteryPercentage;
import com.example.polarisdigitech.repository.BoxRepo;
import com.example.polarisdigitech.service.BoxService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/boxes")
public class BoxController {

    private final BoxService boxService;

    public BoxController(
            BoxService boxService
    ) {
        this.boxService = boxService;
    }

    @PostMapping
    public ResponseEntity<?> createBox(
            @Valid @RequestBody CreateBoxRequest request
    ) {

        long id =
                boxService.createBox(request);

        return ResponseEntity.ok(
                Map.of(
                        "id", id,
                        "txref", request.txref(),
                        "message",
                        "Box created successfully"
                )
        );
    }

    @PostMapping("/{txref}/items")
    public ResponseEntity<?> loadItem(
            @PathVariable String txref,
            @Valid @RequestBody LoadItemRequest request
    ) {

        boxService.loadItem(
                txref,
                request
        );

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Item loaded successfully"
                )
        );
    }

    @PostMapping("/{txref}/finish-loading")
    public ResponseEntity<?> finishLoading(
            @PathVariable String txref
    ) {

        boxService.finishLoading(txref);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Loading completed"
                )
        );
    }

    @PostMapping("/{txref}/start-delivery")
    public ResponseEntity<?> startDelivery(
            @PathVariable String txref
    ) {

        boxService.startDelivery(txref);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Delivery started"
                )
        );
    }

    @PostMapping("/{txref}/complete-delivery")
    public ResponseEntity<?> completeDelivery(
            @PathVariable String txref
    ) {

        boxService.completeDelivery(txref);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Delivery completed"
                )
        );
    }

    @PostMapping("/{txref}/start-return")
    public ResponseEntity<?> startReturn(
            @PathVariable String txref
    ) {

        boxService.startReturn(txref);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Return started"
                )
        );
    }

    /*
     * Tracker/device endpoint.
     */
    @PutMapping("/{txref}/battery")
    public ResponseEntity<?> updateBattery(
            @PathVariable String txref,
            @Valid @RequestBody UpdateBatteryPercentage request
    ) {

        boxService.updateBattery(
                txref,
                request
        );

        return ResponseEntity.ok(
                Map.of(
                        "txref", txref,
                        "batteryPercentage",
                        request.batteryPercentage(),
                        "message",
                        "Battery updated successfully"
                )
        );
    }

    @GetMapping("/{txref}/battery")
    public ResponseEntity<?> getBattery(
            @PathVariable String txref
    ) {

        return ResponseEntity.ok(
                Map.of(
                        "txref", txref,
                        "batteryPercentage",
                        boxService.getBatteryPercentage(txref)
                )
        );
    }

    @GetMapping("/{txref}/state")
    public ResponseEntity<?> getState(
            @PathVariable String txref
    ) {

        return ResponseEntity.ok(
                Map.of(
                        "txref", txref,
                        "state",
                        boxService.getBoxState(txref)
                )
        );
    }

    @GetMapping("/{txref}/items")
    public ResponseEntity<?> getItems(
            @PathVariable String txref
    ) {

        List<BoxRepo.ItemData> items =
                boxService.getItems(txref);

        return ResponseEntity.ok(
                Map.of(
                        "txref", txref,
                        "items", items
                )
        );
    }

    @GetMapping("/available")
    public ResponseEntity<?> getAvailableBoxes() {

        return ResponseEntity.ok(
                boxService.getAvailableBoxes()
        );
    }

    @GetMapping("/{txref}")
    public ResponseEntity<?> getBox(
            @PathVariable String txref
    ) {

        return ResponseEntity.ok(
                boxService.getBox(txref)
        );
    }
}