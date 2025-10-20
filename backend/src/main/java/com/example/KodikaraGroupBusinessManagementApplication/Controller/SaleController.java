package com.example.KodikaraGroupBusinessManagementApplication.Controller;

import com.example.KodikaraGroupBusinessManagementApplication.DTO.SaleRequestDTO;
import com.example.KodikaraGroupBusinessManagementApplication.DTO.SaleResponseDTO;
import com.example.KodikaraGroupBusinessManagementApplication.services.SaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SaleController {

    @Qualifier("saleServiceImpl")
    private final SaleService saleService;

    @PostMapping("")
    public ResponseEntity<SaleResponseDTO> createSale(@Valid @RequestBody SaleRequestDTO dto) {
        SaleResponseDTO response = saleService.createSale(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleResponseDTO> getSale(@PathVariable String id) {
        return ResponseEntity.ok(saleService.getSaleById(id));
    }

    @GetMapping
    public ResponseEntity<List<SaleResponseDTO>> getAllSales() {
        return ResponseEntity.ok(saleService.getAllSales());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSale(@PathVariable String id) {
        saleService.deleteSale(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/by-date")
    public ResponseEntity<List<SaleResponseDTO>> getSaleByDate(@RequestParam @DateTimeFormat (iso= DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(saleService.getSaleByDate(date));
    }
    @GetMapping("/by-date-range")
    public ResponseEntity<List<SaleResponseDTO>> getSaleByDateRange(@RequestParam @DateTimeFormat(iso =DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                    @RequestParam @DateTimeFormat(iso =DateTimeFormat.ISO.DATE) LocalDate endDate){
        return ResponseEntity.ok(saleService.getSaleByDateRange(startDate, endDate));
    }
    @DeleteMapping("/by-date")
    public ResponseEntity<Void> deleteSaleByDate(@RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)LocalDate date) {
        saleService.deleteSaleByDate(date);
        return ResponseEntity.noContent().build();
    }
}