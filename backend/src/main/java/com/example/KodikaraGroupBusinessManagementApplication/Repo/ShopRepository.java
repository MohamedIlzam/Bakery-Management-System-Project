package com.example.KodikaraGroupBusinessManagementApplication.Repo;

import com.example.KodikaraGroupBusinessManagementApplication.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, String> {

    Optional<Shop> findByShopName(String shopName);


}