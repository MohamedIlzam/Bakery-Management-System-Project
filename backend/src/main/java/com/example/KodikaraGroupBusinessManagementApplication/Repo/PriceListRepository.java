package com.example.KodikaraGroupBusinessManagementApplication.Repo;

import com.example.KodikaraGroupBusinessManagementApplication.model.PriceList;
import com.example.KodikaraGroupBusinessManagementApplication.model.Product;
import com.example.KodikaraGroupBusinessManagementApplication.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PriceListRepository extends JpaRepository<PriceList, String> {
    Optional<PriceList> findByShopAndProduct(Shop shop, Product product);

}