package com.example.KodikaraGroupBusinessManagementApplication.Controller;

import com.example.KodikaraGroupBusinessManagementApplication.DTO.ShopSupplyDTO;
import com.example.KodikaraGroupBusinessManagementApplication.services.ShopSupplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shop-supplies") // This matches your Frontend call
@CrossOrigin
@RequiredArgsConstructor
public class ShopSupplyController {

    private final ShopSupplyService shopSupplyService;

    @PostMapping
    public ResponseEntity<ShopSupplyDTO> createShopSupply(@RequestBody ShopSupplyDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(shopSupplyService.createShopSupply(dto));
    }

    @GetMapping
    public ResponseEntity<List<ShopSupplyDTO>> getAllShopSupplies() {
        return ResponseEntity.ok(shopSupplyService.getAllShopSupplies());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShopSupplyDTO> updateShopSupply(@PathVariable String id, @RequestBody ShopSupplyDTO dto) {
        return ResponseEntity.ok(shopSupplyService.updateShopSupply(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShopSupply(@PathVariable String id) {
        shopSupplyService.deleteShopSupply(id);
        return ResponseEntity.noContent().build();
    }
}