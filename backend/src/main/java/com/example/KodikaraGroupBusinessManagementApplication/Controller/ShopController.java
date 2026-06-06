package com.example.KodikaraGroupBusinessManagementApplication.Controller;

import com.example.KodikaraGroupBusinessManagementApplication.DTO.ShopDTO;
import com.example.KodikaraGroupBusinessManagementApplication.services.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shops")
@CrossOrigin
@RequiredArgsConstructor
public class ShopController {
    private final ShopService shopService;

    @PostMapping
    public ResponseEntity<ShopDTO> createShop(@RequestBody ShopDTO dto) {
        return ResponseEntity.ok(shopService.createShop(dto));
    }

    @GetMapping
    public ResponseEntity<List<ShopDTO>> getAllShops() {
        return ResponseEntity.ok(shopService.getAllShops());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShopDTO> updateShop(@PathVariable String id, @RequestBody ShopDTO dto) {
        return ResponseEntity.ok(shopService.updateShop(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShop(@PathVariable String id) {
        shopService.deleteShop(id);
        return ResponseEntity.noContent().build();
    }
}