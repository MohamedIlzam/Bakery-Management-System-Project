package com.example.KodikaraGroupBusinessManagementApplication.Repo;

import com.example.KodikaraGroupBusinessManagementApplication.model.Stock;
import com.example.KodikaraGroupBusinessManagementApplication.model.Product;
import com.example.KodikaraGroupBusinessManagementApplication.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Integer> {
    List<Stock> findByDateAndShop(LocalDate date, Shop shop);
    Optional<Stock> findByDateAndShopAndProduct(LocalDate date, Shop shop, Product product);
}