package com.example.KodikaraGroupBusinessManagementApplication.Controller;

import com.example.KodikaraGroupBusinessManagementApplication.DTO.StockEntryDTO;
import com.example.KodikaraGroupBusinessManagementApplication.DTO.StockReportDTO;
import com.example.KodikaraGroupBusinessManagementApplication.services.StockService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/stock")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping
    public ResponseEntity<StockEntryDTO> saveDailyStock(@RequestBody StockEntryDTO stockEntryDTO) {
        StockEntryDTO savedStock = stockService.saveDailyStock(stockEntryDTO);
        return new ResponseEntity<>(savedStock, HttpStatus.CREATED);
    }

    @GetMapping("/{shopId}/{date}")
    public ResponseEntity<List<StockReportDTO>> getDailyStockReport(
            @PathVariable String shopId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<StockReportDTO> report = stockService.getDailyStockReport(shopId, date);
        return ResponseEntity.ok(report);
    }
}