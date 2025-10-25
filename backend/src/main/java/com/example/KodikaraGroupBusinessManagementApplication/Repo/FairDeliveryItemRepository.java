package com.example.KodikaraGroupBusinessManagementApplication.Repo;

import com.example.KodikaraGroupBusinessManagementApplication.model.FairDelivery;
import com.example.KodikaraGroupBusinessManagementApplication.model.FairDeliveryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FairDeliveryItemRepository extends JpaRepository<FairDeliveryItem, Long> {
    List<FairDeliveryItem> findByFairDelivery(FairDelivery fairDelivery);
}
