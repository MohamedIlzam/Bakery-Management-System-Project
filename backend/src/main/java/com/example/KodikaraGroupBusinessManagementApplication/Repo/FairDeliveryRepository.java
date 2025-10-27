package com.example.KodikaraGroupBusinessManagementApplication.Repo;

import com.example.KodikaraGroupBusinessManagementApplication.model.FairDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FairDeliveryRepository extends JpaRepository<FairDelivery, String> {


    List<FairDelivery> findByDeliveryDateAndStatus(LocalDate date, String status);
    List<FairDelivery> findByDeliveryDateBetweenAndStatus(LocalDate startDate, LocalDate endDate, String status);
}