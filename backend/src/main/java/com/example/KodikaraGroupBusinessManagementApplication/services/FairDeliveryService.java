package com.example.KodikaraGroupBusinessManagementApplication.services;

import com.example.KodikaraGroupBusinessManagementApplication.DTO.FairDeliveryDTO;
import com.example.KodikaraGroupBusinessManagementApplication.model.FairDelivery;
import com.example.KodikaraGroupBusinessManagementApplication.model.FairDeliveryItem;
import com.example.KodikaraGroupBusinessManagementApplication.Repo.FairDeliveryRepository;
import com.example.KodikaraGroupBusinessManagementApplication.Repo.FairDeliveryItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class FairDeliveryService {

    private final FairDeliveryRepository fairDeliveryRepo;
    private final FairDeliveryItemRepository fairDeliveryItemRepo;

    public FairDeliveryService(FairDeliveryRepository fairDeliveryRepo, FairDeliveryItemRepository fairDeliveryItemRepo) {
        this.fairDeliveryRepo = fairDeliveryRepo;
        this.fairDeliveryItemRepo = fairDeliveryItemRepo;
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateProfit(Long deliveryId) {
        FairDelivery fairDelivery = fairDeliveryRepo.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("FairDelivery not found for id: " + deliveryId));

        // Get related items
        List<FairDeliveryItem> items = fairDeliveryItemRepo.findByFairDelivery(fairDelivery);

        // Calculate total income
        BigDecimal totalIncome = items.stream()
                .map(item -> item.getUnitPrice()
                        .multiply(BigDecimal.valueOf(item.getQtySent() - item.getQtyRemaining())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Total expenses (extra payments + tax + diesel)
        BigDecimal totalExpenses = safe(fairDelivery.getExtraPayments())
                .add(safe(fairDelivery.getTax()))
                .add(safe(fairDelivery.getDieselAmount()));

        // Profit = income - expenses
        BigDecimal profit = totalIncome.subtract(totalExpenses);

        // Store the profit
        fairDelivery.setProfit(profit);
        fairDeliveryRepo.save(fairDelivery);

        return profit;
    }

    private BigDecimal safe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
