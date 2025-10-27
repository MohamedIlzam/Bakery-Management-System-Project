package com.example.KodikaraGroupBusinessManagementApplication.Repo;


import com.example.KodikaraGroupBusinessManagementApplication.model.Product;
import com.example.KodikaraGroupBusinessManagementApplication.model.Shop;
import com.example.KodikaraGroupBusinessManagementApplication.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Integer> {
    List<Stock> findByDateAndShop(LocalDate date, Shop shop);
    Optional<Stock> findByDateAndShopAndProduct(LocalDate date, Shop shop, Product product);
}