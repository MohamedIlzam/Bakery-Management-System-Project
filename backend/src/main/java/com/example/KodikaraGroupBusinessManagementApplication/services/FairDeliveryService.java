package com.example.KodikaraGroupBusinessManagementApplication.services;

import com.example.KodikaraGroupBusinessManagementApplication.DTO.FairDeliveryDTO;
import com.example.KodikaraGroupBusinessManagementApplication.DTO.FairDeliveryItemDTO;
import com.example.KodikaraGroupBusinessManagementApplication.Repo.*; // Import required Repos
import com.example.KodikaraGroupBusinessManagementApplication.exception.ResourceNotFoundException;
import com.example.KodikaraGroupBusinessManagementApplication.model.*;
import com.example.KodikaraGroupBusinessManagementApplication.util.IdGenerator;
import lombok.RequiredArgsConstructor; // Use Lombok
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FairDeliveryService {

    private final FairDeliveryRepository fairDeliveryRepo;
    private final FairDeliveryItemRepository fairDeliveryItemRepo;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final ProductRepository productRepository;


    @Transactional
    public FairDeliveryDTO createInitialDeliveryLog(FairDeliveryDTO dto) {
        // Find related entities By IDs
        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + dto.getVehicleId()));
        Driver driver = driverRepository.findById(dto.getDriverId())
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + dto.getDriverId()));

        FairDelivery fairDelivery = new FairDelivery();
        fairDelivery.setDeliveryId(IdGenerator.generate("FDEL"));
        fairDelivery.setFairName(dto.getFairName());
        fairDelivery.setDeliveryDate(dto.getDeliveryDate() != null ? dto.getDeliveryDate() : LocalDate.now());
        fairDelivery.setVehicle(vehicle);
        fairDelivery.setDriver(driver);
        fairDelivery.setStatus("OUT");
        fairDelivery.setExtraPayments(dto.getExtraPayments());
        fairDelivery.setTax(dto.getTax());
        fairDelivery.setDieselAmount(dto.getDieselAmount());
        fairDelivery.setProfit(BigDecimal.ZERO);

        List<FairDeliveryItem> items = new ArrayList<>();
        if (dto.getItems() != null) {
            for (FairDeliveryItemDTO itemDto : dto.getItems()) {
                Product product = productRepository.findById(itemDto.getProductId()) // Assuming Product ID is passed
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemDto.getProductId()));

                FairDeliveryItem item = new FairDeliveryItem();
                item.setItemId(IdGenerator.generate("FITE"));
                item.setFairDelivery(fairDelivery);
                item.setProduct(product);
                item.setQtySent(itemDto.getQtySent());
                item.setUnitPrice(itemDto.getUnitPrice() != null ? itemDto.getUnitPrice() : product.getUnitPrice());
                item.setQtyRemaining(itemDto.getQtyRemaining()); // Uses 0 by default, or the user provided quantity
                item.setQtyExpired(itemDto.getQtyExpired());
                items.add(item);
            }
        }
        fairDelivery.setItems(items);

        FairDelivery savedDelivery = fairDeliveryRepo.save(fairDelivery);
        return convertToDTO(savedDelivery);
    }

    //Input Remaining Stock & Calculate Profit
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
            if(returnedItemDto.getQtyRemaining() < 0 || returnedItemDto.getQtyExpired() < 0 || (returnedItemDto.getQtyRemaining() + returnedItemDto.getQtyExpired()) > itemToUpdate.getQtySent()){
                throw new IllegalArgumentException("Invalid quantities for item: " + itemToUpdate.getProduct().getName());
            }
            itemToUpdate.setQtyRemaining(returnedItemDto.getQtyRemaining());
            itemToUpdate.setQtyExpired(returnedItemDto.getQtyExpired());
            // Changes tracked by Hibernate
        }

        // Update status and calculate/save profit
        fairDelivery.setStatus("RETURNED");
        BigDecimal profit = calculateProfitInternal(fairDelivery); // Use internal calculation
        fairDelivery.setProfit(profit);

        FairDelivery updatedDelivery = fairDeliveryRepo.save(fairDelivery); // Save changes
        return convertToDTO(updatedDelivery);
    }

    //Method to get a specific FairDelivery by ID
    @Transactional(readOnly = true)
    public FairDeliveryDTO getFairDeliveryById(String deliveryId) {
        FairDelivery fairDelivery = fairDeliveryRepo.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("FairDelivery not found for id: " + deliveryId));
        return convertToDTO(fairDelivery);
    }

    //Method to get all FairDeliveries
    @Transactional(readOnly = true)
    public List<FairDeliveryDTO> getAllFairDeliveries() {
        return fairDeliveryRepo.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    public void deleteDelivery(String saleId) {
        if (!fairDeliveryRepo.existsById(saleId)) {
            throw new ResourceNotFoundException("Fair Delivery not found with id: " + saleId);
        }
        fairDeliveryRepo.deleteById(saleId);
    }

    // Existing profit calculation logic (made private)
    private BigDecimal calculateProfitInternal(FairDelivery fairDelivery) {
        List<FairDeliveryItem> items = fairDelivery.getItems();
        if (items == null) { items = new ArrayList<>(); }

        BigDecimal totalIncome = items.stream()
                .map(item -> safe(item.getUnitPrice())
                        .multiply(BigDecimal.valueOf(item.getQtySent() - item.getQtyRemaining() - item.getQtyExpired())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpenses = safe(fairDelivery.getExtraPayments())
                .add(safe(fairDelivery.getTax()))
                .add(safe(fairDelivery.getDieselAmount()));

        return totalIncome.subtract(totalExpenses);
    }


    @Transactional(readOnly = true)
    public BigDecimal getProfit(String deliveryId) {
        FairDelivery fairDelivery = fairDeliveryRepo.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("FairDelivery not found for id: " + deliveryId));
        // Optionally return saved profit if already calculated and RETURNED
        // if ("RETURNED".equalsIgnoreCase(fairDelivery.getStatus()) && fairDelivery.getProfit() != null) {
        //     return fairDelivery.getProfit();
        // }
        return calculateProfitInternal(fairDelivery);
    }

    @Transactional
    public FairDeliveryDTO updateFairDelivery(String id, FairDeliveryDTO dto) {
        FairDelivery delivery = fairDeliveryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found: " + id));


        if(dto.getFairName() != null) delivery.setFairName(dto.getFairName());
        if(dto.getDeliveryDate() != null) delivery.setDeliveryDate(dto.getDeliveryDate());
        if(dto.getTax() != null) delivery.setTax(dto.getTax());
        if(dto.getExtraPayments() != null) delivery.setExtraPayments(dto.getExtraPayments());
        if(dto.getDieselAmount() != null) delivery.setDieselAmount(dto.getDieselAmount());
        if(dto.getStatus() != null) delivery.setStatus(dto.getStatus());


        if (dto.getDriverId() != null) {
            Driver driver = driverRepository.findById(dto.getDriverId())
                    .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
            delivery.setDriver(driver);
        }
        if (dto.getVehicleId() != null) {
            Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
            delivery.setVehicle(vehicle);
        }

        if (dto.getItems() != null) {
            // Remove items that are no longer in the DTO
            List<String> incomingProductIds = dto.getItems().stream()
                    .map(FairDeliveryItemDTO::getProductId)
                    .collect(Collectors.toList());
            delivery.getItems().removeIf(existingItem -> !incomingProductIds.contains(existingItem.getProduct().getProId()));

            for (FairDeliveryItemDTO itemDto : dto.getItems()) {
                Optional<FairDeliveryItem> existingItemOpt = delivery.getItems().stream()
                        .filter(existingItem -> existingItem.getProduct().getProId().equals(itemDto.getProductId()))
                        .findFirst();

                if (existingItemOpt.isPresent()) {
                    FairDeliveryItem existingItem = existingItemOpt.get();
                    existingItem.setQtySent(itemDto.getQtySent());
                    existingItem.setQtyRemaining(itemDto.getQtyRemaining());
                    existingItem.setQtyExpired(itemDto.getQtyExpired());
                    existingItem.setUnitPrice(itemDto.getUnitPrice() != null ? itemDto.getUnitPrice() : existingItem.getProduct().getUnitPrice());
                } else {
                    Product product = productRepository.findById(itemDto.getProductId())
                            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemDto.getProductId()));
                    FairDeliveryItem newItem = new FairDeliveryItem();
                    newItem.setItemId(IdGenerator.generate("FITE"));
                    newItem.setFairDelivery(delivery);
                    newItem.setProduct(product);
                    newItem.setQtySent(itemDto.getQtySent());
                    newItem.setUnitPrice(itemDto.getUnitPrice() != null ? itemDto.getUnitPrice() : product.getUnitPrice());
                    newItem.setQtyRemaining(itemDto.getQtyRemaining());
                    newItem.setQtyExpired(itemDto.getQtyExpired());
                    delivery.getItems().add(newItem);
                }
            }
        }
        BigDecimal newProfit = calculateProfitInternal(delivery);
        delivery.setProfit(newProfit);

        return convertToDTO(fairDeliveryRepo.save(delivery));
    }


    // Helper for null safety
    private BigDecimal safe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    //DTO Conversion
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
        if (entity.getProduct() != null) dto.setProductId(entity.getProduct().getProId());
        dto.setQtySent(entity.getQtySent());
        dto.setQtyRemaining(entity.getQtyRemaining());
        dto.setQtyExpired(entity.getQtyExpired());
        dto.setUnitPrice(safe(entity.getUnitPrice()));
        return dto;
    }
}