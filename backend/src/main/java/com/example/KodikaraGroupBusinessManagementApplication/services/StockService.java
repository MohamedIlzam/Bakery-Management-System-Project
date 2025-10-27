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
        // FIX: Use the variable 'stockEntryDTO' (lowercase 's')
        Product product = productRepository.findById(stockEntryDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + stockEntryDTO.getProductId()));
        Shop shop = shopRepository.findById(stockEntryDTO.getShopId())
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found with ID: " + stockEntryDTO.getShopId()));

        Optional<Stock> existingStock = stockRepository.findByDateAndShopAndProduct(
                stockEntryDTO.getDate(), shop, product);

        Stock stock;
        if (existingStock.isPresent()) {
            stock = existingStock.get();
            // FIX: Use the variable 'stockEntryDTO' (lowercase 's')
            stock.setMorningQuantity(stockEntryDTO.getMorningQuantity());
            stock.setClosingQuantity(stockEntryDTO.getClosingQuantity());
        } else {
            stock = new Stock();
            // FIX: Use the variable 'stockEntryDTO' (lowercase 's')
            stock.setDate(stockEntryDTO.getDate());
            stock.setProduct(product);
            stock.setShop(shop);
            stock.setMorningQuantity(stockEntryDTO.getMorningQuantity());
            stock.setClosingQuantity(stockEntryDTO.getClosingQuantity());
        }
        stockRepository.save(stock);
        // FIX: Return the variable 'stockEntryDTO', not the class 'StockEntryDTO'
        return stockEntryDTO;
    }

    @Transactional(readOnly = true)
    public List<StockReportDTO> getDailyStockReport(String shopId, LocalDate date) {
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

            // --- FIX: Correct Income Logic ---
            Optional<PriceList> priceListEntry = priceListRepository.findByShopAndProduct(shop, product);

            // Use the shop-specific price if it exists, otherwise fall back to the base product price
            BigDecimal correctPrice = priceListEntry.map(PriceList::getPrice)
                    .orElse(product.getUnitPrice());

            if (correctPrice != null) {
                income = correctPrice.multiply(BigDecimal.valueOf(stockSold));
            }
            // --- END OF FIX ---

            return new StockReportDTO(
                    product.getProId(),
                    product.getName(),
                    shop.getShopId(),
                    shop.getShopName(), // Assuming Shop entity has getShopName()
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