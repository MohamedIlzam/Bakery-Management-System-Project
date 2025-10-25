package com.example.KodikaraGroupBusinessManagementApplication.services;

import com.example.KodikaraGroupBusinessManagementApplication.DTO.StockEntryDTO;
import com.example.KodikaraGroupBusinessManagementApplication.DTO.StockReportDTO;
import com.example.KodikaraGroupBusinessManagementApplication.Repo.*;
import com.example.KodikaraGroupBusinessManagementApplication.exception.ResourceNotFoundException;
import com.example.KodikaraGroupBusinessManagementApplication.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class StockService {

    private final StockRepository stockRepository;
    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;
    private final PriceListRepository priceListRepository;

    public StockService(StockRepository stockRepository, ProductRepository productRepository, ShopRepository shopRepository, PriceListRepository priceListRepository) {
        this.stockRepository = stockRepository;
        this.productRepository = productRepository;
        this.shopRepository = shopRepository;
        this.priceListRepository = priceListRepository;
    }

    public StockEntryDTO saveDailyStock(StockEntryDTO stockEntryDTO) {
        Product product = productRepository.findById(stockEntryDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + stockEntryDTO.getProductId()));
        Shop shop = shopRepository.findById(stockEntryDTO.getShopId())
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found with ID: " + stockEntryDTO.getShopId()));

        Optional<Stock> existingStock = stockRepository.findByDateAndShopAndProduct(
                stockEntryDTO.getDate(), shop, product);

        Stock stock;
        if (existingStock.isPresent()) {
            stock = existingStock.get();
            stock.setMorningQuantity(stockEntryDTO.getMorningQuantity());
            stock.setClosingQuantity(stockEntryDTO.getClosingQuantity());
        } else {
            stock = new Stock();
            stock.setDate(stockEntryDTO.getDate());
            stock.setProduct(product);
            stock.setShop(shop);
            stock.setMorningQuantity(stockEntryDTO.getMorningQuantity());
            stock.setClosingQuantity(stockEntryDTO.getClosingQuantity());
        }
        stockRepository.save(stock);
        return stockEntryDTO; // Return the DTO as is for now
    }

    @Transactional(readOnly = true)
    public List<StockReportDTO> getDailyStockReport(Integer shopId, LocalDate date) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found with ID: " + shopId));

        List<Stock> stocks = stockRepository.findByDateAndShop(date, shop);
        List<Product> allProducts = productRepository.findAll(); // Get all products to ensure all are listed

        return allProducts.stream().map(product -> {
            Optional<Stock> stockForProduct = stocks.stream()
                    .filter(s -> s.getProduct().getProId().equals(product.getProId()))
                    .findFirst();

            Integer morningQty = stockForProduct.map(Stock::getMorningQuantity).orElse(0);
            Integer closingQty = stockForProduct.map(Stock::getClosingQuantity).orElse(0);

            Integer stockSold = morningQty - closingQty;
            BigDecimal income = BigDecimal.ZERO;

            // Use unitPrice from Product table for income calculation
            BigDecimal productUnitPrice = product.getUnitPrice();
            if (productUnitPrice != null) {
                income = productUnitPrice.multiply(BigDecimal.valueOf(stockSold));
            }

            return new StockReportDTO(
                    product.getProId(),
                    product.getName(),
                    shop.getShopId(),
                    shop.getName(),
                    date,
                    morningQty,
                    closingQty,
                    stockSold,
                    income,
                    closingQty // Remaining stock is closing quantity
            );
        }).collect(Collectors.toList());
    }
}