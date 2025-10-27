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
@RequestMapping("/api/fair-deliveries") // Use plural and consistent naming
@CrossOrigin // Consider security implications for production
@RequiredArgsConstructor // Use Lombok constructor
public class FairDeliveryController {

    private final FairDeliveryService fairDeliveryService;

    // === CREATE: Member 1 Use Case 1 - Save Initial Log ===
    @PostMapping
    public ResponseEntity<FairDeliveryDTO> createFairDeliveryLog(@RequestBody FairDeliveryDTO fairDeliveryDTO) {
        FairDeliveryDTO createdDto = fairDeliveryService.createInitialDeliveryLog(fairDeliveryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDto);
    }

    // === UPDATE: Member 1 Use Case 1 - Input Remaining Stock ===
    @PatchMapping("/{id}/return") // Use PATCH for partial updates
    public ResponseEntity<FairDeliveryDTO> updateReturnStock(
            @PathVariable String id,
            @RequestBody List<FairDeliveryItemDTO> returnedItems) { // Expect a list of items with updated qtyRemaining and itemId
        FairDeliveryDTO updatedDto = fairDeliveryService.updateReturnStock(id, returnedItems);
        return ResponseEntity.ok(updatedDto);
    }

    // === READ: Get Profit ===
    @GetMapping("/{id}/profit")
    public ResponseEntity<BigDecimal> getProfit(@PathVariable String id) {
        return ResponseEntity.ok(fairDeliveryService.getProfit(id));
    }

    // === READ: Get a specific FairDelivery by ID ===
    @GetMapping("/{id}")
    public ResponseEntity<FairDeliveryDTO> getFairDelivery(@PathVariable String id) {
        return ResponseEntity.ok(fairDeliveryService.getFairDeliveryById(id));
    }

    // === READ: Get all FairDeliveries ===
    @GetMapping
    public ResponseEntity<List<FairDeliveryDTO>> getAllFairDeliveries() {
        return ResponseEntity.ok(fairDeliveryService.getAllFairDeliveries());
    }

    // (Optional: Add DELETE endpoint if needed)
    // @DeleteMapping("/{id}")
    // public ResponseEntity<Void> deleteFairDelivery(@PathVariable Long id) {
    //     // fairDeliveryService.deleteDelivery(id); // Need to add delete method to service
    //     return ResponseEntity.noContent().build();
    // }
}