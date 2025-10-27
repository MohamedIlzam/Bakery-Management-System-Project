package com.example.KodikaraGroupBusinessManagementApplication.services;

import com.example.KodikaraGroupBusinessManagementApplication.DTO.FairDeliveryDTO;
import com.example.KodikaraGroupBusinessManagementApplication.DTO.FairDeliveryItemDTO;
import com.example.KodikaraGroupBusinessManagementApplication.Repo.*; // Import required Repos
import com.example.KodikaraGroupBusinessManagementApplication.exception.ResourceNotFoundException;
import com.example.KodikaraGroupBusinessManagementApplication.model.*;
import lombok.RequiredArgsConstructor; // Use Lombok
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Use Lombok for constructor injection
public class FairDeliveryService {

    private final FairDeliveryRepository fairDeliveryRepo;
    private final FairDeliveryItemRepository fairDeliveryItemRepo;
    // --- Inject repositories needed for Create/Update ---
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final ProductRepository productRepository;
    // --- END ---

    // === CREATE: Member 1 Use Case 1 - Save Initial Log ===
    @Transactional
    public FairDeliveryDTO createInitialDeliveryLog(FairDeliveryDTO dto) {
        // Find related entities (Assuming IDs are passed in DTO)
        // Adjust if names are passed (e.g., use vehicleRepository.findByVehicleNo)
        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + dto.getVehicleId()));
        Driver driver = driverRepository.findById(dto.getDriverId())
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + dto.getDriverId()));

        FairDelivery fairDelivery = new FairDelivery();
        fairDelivery.setDeliveryId(IdGenerator.generate("FDEL"));        fairDelivery.setFairName(dto.getFairName());
        fairDelivery.setDeliveryDate(dto.getDeliveryDate() != null ? dto.getDeliveryDate() : LocalDate.now());
        fairDelivery.setVehicle(vehicle);
        fairDelivery.setDriver(driver);
        fairDelivery.setStatus("OUT"); // Initial status
        fairDelivery.setExtraPayments(dto.getExtraPayments());
        fairDelivery.setTax(dto.getTax());
        fairDelivery.setDieselAmount(dto.getDieselAmount());
        fairDelivery.setProfit(BigDecimal.ZERO); // Initial profit

        List<FairDeliveryItem> items = new ArrayList<>();
        if (dto.getItems() != null) {
            for (FairDeliveryItemDTO itemDto : dto.getItems()) {
                Product product = productRepository.findById(itemDto.getProductId()) // Assuming Product ID is passed
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemDto.getProductId()));

                FairDeliveryItem item = new FairDeliveryItem();
                item.setItemId(IdGenerator.generate("FITE"));                item.setFairDelivery(fairDelivery); // Link to parent
                item.setProduct(product);
                item.setQtySent(itemDto.getQtySent());
                item.setUnitPrice(itemDto.getUnitPrice() != null ? itemDto.getUnitPrice() : BigDecimal.ZERO);
                item.setQtyRemaining(itemDto.getQtySent()); // Initially, remaining = sent
                items.add(item);
            }
        }
        fairDelivery.setItems(items);

        FairDelivery savedDelivery = fairDeliveryRepo.save(fairDelivery); // Cascade saves items
        return convertToDTO(savedDelivery); // Convert to DTO for response
    }

    // === UPDATE: Member 1 Use Case 1 - Input Remaining Stock & Calculate Profit ===
    @Transactional
    public FairDeliveryDTO updateReturnStock(String deliveryId, List<FairDeliveryItemDTO> returnedItemsDto) {
        FairDelivery fairDelivery = fairDeliveryRepo.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("FairDelivery not found for id: " + deliveryId));

        if (!"OUT".equalsIgnoreCase(fairDelivery.getStatus())) {
            throw new IllegalStateException("Delivery status is not 'OUT', cannot update remaining stock.");
        }

        // Update remaining quantities for each item
        for (FairDeliveryItemDTO returnedItemDto : returnedItemsDto) {
            FairDeliveryItem itemToUpdate = fairDelivery.getItems().stream()
                    .filter(item -> item.getItemId().equals(returnedItemDto.getItemId())) // Match by item ID
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Item ID " + returnedItemDto.getItemId() + " not found in this delivery."));

            // Validate remaining quantity
            if(returnedItemDto.getQtyRemaining() < 0 || returnedItemDto.getQtyRemaining() > itemToUpdate.getQtySent()){
                throw new IllegalArgumentException("Invalid quantity remaining (" + returnedItemDto.getQtyRemaining()
                        + ") for item: " + itemToUpdate.getProduct().getName()
                        + ". Must be between 0 and " + itemToUpdate.getQtySent());
            }
            itemToUpdate.setQtyRemaining(returnedItemDto.getQtyRemaining());
            // Changes tracked by Hibernate
        }

        // Update status and calculate/save profit
        fairDelivery.setStatus("RETURNED");
        BigDecimal profit = calculateProfitInternal(fairDelivery); // Use internal calculation
        fairDelivery.setProfit(profit);

        FairDelivery updatedDelivery = fairDeliveryRepo.save(fairDelivery); // Save changes
        return convertToDTO(updatedDelivery);
    }

    // === READ: Method to get a specific FairDelivery by ID ===
    @Transactional(readOnly = true)
    public FairDeliveryDTO getFairDeliveryById(String deliveryId) {
        FairDelivery fairDelivery = fairDeliveryRepo.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("FairDelivery not found for id: " + deliveryId));
        return convertToDTO(fairDelivery);
    }

    // === READ: Method to get all FairDeliveries ===
    @Transactional(readOnly = true)
    public List<FairDeliveryDTO> getAllFairDeliveries() {
        return fairDeliveryRepo.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    // Existing profit calculation logic (made private)
    private BigDecimal calculateProfitInternal(FairDelivery fairDelivery) {
        List<FairDeliveryItem> items = fairDelivery.getItems();
        if (items == null) { items = new ArrayList<>(); }

        BigDecimal totalIncome = items.stream()
                .map(item -> safe(item.getUnitPrice())
                        .multiply(BigDecimal.valueOf(item.getQtySent() - item.getQtyRemaining())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpenses = safe(fairDelivery.getExtraPayments())
                .add(safe(fairDelivery.getTax()))
                .add(safe(fairDelivery.getDieselAmount()));

        return totalIncome.subtract(totalExpenses);
    }

    // Public method to just get the calculated profit (READ operation)
    @Transactional(readOnly = true)
    public BigDecimal getProfit(String deliveryId) {
        FairDelivery fairDelivery = fairDeliveryRepo.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("FairDelivery not found for id: " + deliveryId));
        // Optionally return saved profit if already calculated and RETURNED
        // if ("RETURNED".equalsIgnoreCase(fairDelivery.getStatus()) && fairDelivery.getProfit() != null) {
        //     return fairDelivery.getProfit();
        // }
        return calculateProfitInternal(fairDelivery); // Calculate fresh value
    }


    // Helper for null safety
    private BigDecimal safe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    // === DTO Conversion Helpers ===
    private FairDeliveryDTO convertToDTO(FairDelivery entity) {
        FairDeliveryDTO dto = new FairDeliveryDTO();
        dto.setDeliveryId(entity.getDeliveryId());
        dto.setFairName(entity.getFairName());
        dto.setDeliveryDate(entity.getDeliveryDate());
        dto.setExtraPayments(safe(entity.getExtraPayments()));
        dto.setTax(safe(entity.getTax()));
        dto.setDieselAmount(safe(entity.getDieselAmount()));
        dto.setProfit(safe(entity.getProfit()));
        dto.setStatus(entity.getStatus());
        // Ensure Vehicle and Driver entities have getId() methods returning Long/String as appropriate
        if (entity.getVehicle() != null) dto.setVehicleId(entity.getVehicle().getVehicleId());
        if (entity.getDriver() != null) dto.setDriverId(entity.getDriver().getDriverId());

        if (entity.getItems() != null) {
            dto.setItems(entity.getItems().stream()
                    .map(this::convertItemToDTO)
                    .collect(Collectors.toList()));
        } else {
            dto.setItems(new ArrayList<>());
        }
        return dto;
    }

    private FairDeliveryItemDTO convertItemToDTO(FairDeliveryItem entity) {
        FairDeliveryItemDTO dto = new FairDeliveryItemDTO();
        dto.setItemId(entity.getItemId());
        if (entity.getFairDelivery() != null) dto.setDeliveryId(entity.getFairDelivery().getDeliveryId());
        // Ensure Product entity has getId() returning Long/String as appropriate
        if (entity.getProduct() != null) dto.setProductId(entity.getProduct().getProId());
        dto.setQtySent(entity.getQtySent());
        dto.setQtyRemaining(entity.getQtyRemaining());
        dto.setUnitPrice(safe(entity.getUnitPrice()));
        return dto;
    }
}