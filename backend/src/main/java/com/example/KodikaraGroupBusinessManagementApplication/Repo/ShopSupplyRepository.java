package com.example.KodikaraGroupBusinessManagementApplication.Repo;


import com.example.KodikaraGroupBusinessManagementApplication.model.ShopSupply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ShopSupplyRepository extends JpaRepository <ShopSupply,String> {
    List<ShopSupply> findBySupplyDate(LocalDate date);
    List<ShopSupply> findBySupplyDateBetween(LocalDate startDate, LocalDate endDate);
    List<ShopSupply> findBySalesman_UserId(String salesmanUserId);
}
