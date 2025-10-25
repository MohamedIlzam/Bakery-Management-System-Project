package com.example.KodikaraGroupBusinessManagementApplication.Controller;

import com.example.KodikaraGroupBusinessManagementApplication.DTO.FairDeliveryDTO;
import com.example.KodikaraGroupBusinessManagementApplication.services.FairDeliveryService;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/fair-delivery")
@CrossOrigin
public class FairDeliveryController {

    private final FairDeliveryService fairDeliveryService;

    public FairDeliveryController(FairDeliveryService fairDeliveryService) {
        this.fairDeliveryService = fairDeliveryService;
    }

    @GetMapping("/{id}/profit")
    public BigDecimal calculateProfit(@PathVariable Long id) {
        return fairDeliveryService.calculateProfit(id);
    }

    // Example endpoint if you want to post DTOs
    @PostMapping
    public FairDeliveryDTO createFairDelivery(@RequestBody FairDeliveryDTO fairDeliveryDTO) {
        // (Assuming you’ll add a mapping method to convert DTO -> Entity)
        // This is just a placeholder for future integration
        return fairDeliveryDTO;
    }
}
