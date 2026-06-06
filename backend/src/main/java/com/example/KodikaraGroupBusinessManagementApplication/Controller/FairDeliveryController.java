package com.example.KodikaraGroupBusinessManagementApplication.Controller;

import com.example.KodikaraGroupBusinessManagementApplication.DTO.FairDeliveryDTO;
import com.example.KodikaraGroupBusinessManagementApplication.DTO.FairDeliveryItemDTO;
import com.example.KodikaraGroupBusinessManagementApplication.services.FairDeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/fair-deliveries")
@CrossOrigin
@RequiredArgsConstructor
public class FairDeliveryController {

    private final FairDeliveryService fairDeliveryService;

    //Save Initial Log
    @PostMapping
    public ResponseEntity<FairDeliveryDTO> createFairDeliveryLog(@RequestBody FairDeliveryDTO fairDeliveryDTO) {
        FairDeliveryDTO createdDto = fairDeliveryService.createInitialDeliveryLog(fairDeliveryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDto);
    }

    //Input Remaining Stock
    @PatchMapping("/{id}/return") // Use PATCH for partial updates
    public ResponseEntity<FairDeliveryDTO> updateReturnStock(
            @PathVariable String id,
            @RequestBody List<FairDeliveryItemDTO> returnedItems) { // Expect a list of items with updated qtyRemaining and itemId
        FairDeliveryDTO updatedDto = fairDeliveryService.updateReturnStock(id, returnedItems);
        return ResponseEntity.ok(updatedDto);
    }
    @PutMapping("/{id}")
    public ResponseEntity<FairDeliveryDTO> updateFairDelivery(
            @PathVariable String id,
            @RequestBody FairDeliveryDTO dto) {
        return ResponseEntity.ok(fairDeliveryService.updateFairDelivery(id, dto));
    }
    // Get Profit
    @GetMapping("/{id}/profit")
    public ResponseEntity<BigDecimal> getProfit(@PathVariable String id) {
        return ResponseEntity.ok(fairDeliveryService.getProfit(id));
    }

    //Get a specific FairDelivery by ID
    @GetMapping("/{id}")
    public ResponseEntity<FairDeliveryDTO> getFairDelivery(@PathVariable String id) {
        return ResponseEntity.ok(fairDeliveryService.getFairDeliveryById(id));
    }

    //Get all FairDeliveries
    @GetMapping
    public ResponseEntity<List<FairDeliveryDTO>> getAllFairDeliveries() {
        return ResponseEntity.ok(fairDeliveryService.getAllFairDeliveries());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFairDelivery(@PathVariable String id) {
        fairDeliveryService.deleteDelivery(id);
        return ResponseEntity.noContent().build();
    }
}