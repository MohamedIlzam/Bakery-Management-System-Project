package com.example.KodikaraGroupBusinessManagementApplication.services;

import com.example.KodikaraGroupBusinessManagementApplication.DTO.ShopSupplyDTO;
import com.example.KodikaraGroupBusinessManagementApplication.DTO.ShopSupplyItemDTO;
import com.example.KodikaraGroupBusinessManagementApplication.Repo.*;
import com.example.KodikaraGroupBusinessManagementApplication.exception.ResourceNotFoundException;
import com.example.KodikaraGroupBusinessManagementApplication.model.*;
import com.example.KodikaraGroupBusinessManagementApplication.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ShopSupplyService {

    private final ShopSupplyRepository shopSupplyRepository;
    private final VehicleRepository vehicleRepository;
    private final ShopRepository shopRepository;
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final ProductRepository productRepository;

    // CREATE
    public ShopSupplyDTO createShopSupply(ShopSupplyDTO supplyDto) {
        Vehicle vehicle = vehicleRepository.findById(supplyDto.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found: " + supplyDto.getVehicleId()));

        User salesman = userRepository.findById(supplyDto.getSalesmanId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Salesman (User) not found: " + supplyDto.getSalesmanId()));

        ShopSupply shopSupply = new ShopSupply();
        shopSupply.setSupplyId(IdGenerator.generate("SUPP"));
        shopSupply.setSupplyDate(supplyDto.getSupplyDate() != null ? supplyDto.getSupplyDate() : LocalDate.now());
        shopSupply.setVehicle(vehicle);
        shopSupply.setSalesman(salesman);

        if (supplyDto.getDriverId() != null && !supplyDto.getDriverId().isEmpty()) {
            Driver driver = driverRepository.findById(supplyDto.getDriverId())
                    .orElseThrow(() -> new ResourceNotFoundException("Driver not found: " + supplyDto.getDriverId()));
            shopSupply.setDriver(driver);
        }

        List<ShopSupplyItem> supplyItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        if (supplyDto.getItems() != null) {
            for (ShopSupplyItemDTO itemDTO : supplyDto.getItems()) {
                ShopSupplyItem itemEntity = mapItemDTOToEntity(itemDTO, shopSupply);
                supplyItems.add(itemEntity);
                BigDecimal price = itemEntity.getUnitPrice() != null ? itemEntity.getUnitPrice() : BigDecimal.ZERO;
                totalAmount = totalAmount.add(price.multiply(BigDecimal.valueOf(itemEntity.getQtySupplied() - itemEntity.getQtyReturned() - itemEntity.getQtyExpired())));
            }
        }

        shopSupply.setItems(supplyItems);
        
        // Initial setup for payment lifecycle
        shopSupply.setPaidAmount(BigDecimal.ZERO);
        shopSupply.setOutstandingAmount(totalAmount);
        shopSupply.setPaymentStatus(totalAmount.compareTo(BigDecimal.ZERO) <= 0 && supplyDto.getItems() != null && !supplyDto.getItems().isEmpty() ? "COMPLETED" : "UNCOMPLETED");

        ShopSupply savedSupply = shopSupplyRepository.save(shopSupply);
        return convertToDTO(savedSupply);
    }

    // READ
    @Transactional(readOnly = true)
    public List<ShopSupplyDTO> getAllShopSupplies() {
        return shopSupplyRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // UPDATE
    public ShopSupplyDTO updateShopSupply(String id, ShopSupplyDTO dto) {
        ShopSupply supply = shopSupplyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supply not found: " + id));

        if (dto.getSupplyDate() != null)
            supply.setSupplyDate(dto.getSupplyDate());

        if (dto.getVehicleId() != null) {
            Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
            supply.setVehicle(vehicle);
        }

        if (dto.getDriverId() != null) {
            Driver driver = driverRepository.findById(dto.getDriverId())
                    .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
            supply.setDriver(driver);
        }

        if (dto.getItems() != null) {
            supply.getItems().clear();
            for (ShopSupplyItemDTO itemDto : dto.getItems()) {
                supply.getItems().add(mapItemDTOToEntity(itemDto, supply));
            }
        }

        // Recalculate outstanding amount
        BigDecimal totalAmount = BigDecimal.ZERO;
        if (supply.getItems() != null) {
            for (ShopSupplyItem item : supply.getItems()) {
                BigDecimal price = item.getUnitPrice() != null ? item.getUnitPrice() : BigDecimal.ZERO;
                totalAmount = totalAmount.add(price.multiply(BigDecimal.valueOf(item.getQtySupplied() - item.getQtyReturned() - item.getQtyExpired())));
            }
        }
        
        BigDecimal paid = supply.getPaidAmount() != null ? supply.getPaidAmount() : BigDecimal.ZERO;
        BigDecimal newOutstanding = totalAmount.subtract(paid);
        if (newOutstanding.compareTo(BigDecimal.ZERO) < 0) {
            newOutstanding = BigDecimal.ZERO;
        }
        supply.setOutstandingAmount(newOutstanding);

        if (newOutstanding.compareTo(BigDecimal.ZERO) <= 0 && supply.getItems() != null && !supply.getItems().isEmpty()) {
            supply.setPaymentStatus("COMPLETED");
        } else {
            supply.setPaymentStatus("UNCOMPLETED");
        }

        ShopSupply updated = shopSupplyRepository.save(supply);
        return convertToDTO(updated);
    }

    // DELETE
    public void deleteShopSupply(String id) {
        if (!shopSupplyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Supply not found: " + id);
        }
        shopSupplyRepository.deleteById(id);
    }

    // PAYMENT
    public ShopSupplyDTO addPayment(String id, BigDecimal amount) {
        ShopSupply supply = shopSupplyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supply not found: " + id));

        BigDecimal currentPaid = supply.getPaidAmount() != null ? supply.getPaidAmount() : BigDecimal.ZERO;
        supply.setPaidAmount(currentPaid.add(amount));

        // Recalculate total amount from items to avoid legacy data issues
        BigDecimal totalAmount = BigDecimal.ZERO;
        if (supply.getItems() != null) {
            for (ShopSupplyItem item : supply.getItems()) {
                BigDecimal price = item.getUnitPrice() != null ? item.getUnitPrice() : BigDecimal.ZERO;
                totalAmount = totalAmount.add(price.multiply(BigDecimal.valueOf(item.getQtySupplied() - item.getQtyReturned() - item.getQtyExpired())));
            }
        }

        BigDecimal newOutstanding = totalAmount.subtract(supply.getPaidAmount());
        if (newOutstanding.compareTo(BigDecimal.ZERO) < 0) {
            newOutstanding = BigDecimal.ZERO;
        }
        supply.setOutstandingAmount(newOutstanding);

        if (newOutstanding.compareTo(BigDecimal.ZERO) <= 0 && supply.getItems() != null && !supply.getItems().isEmpty()) {
            supply.setPaymentStatus("COMPLETED");
        } else {
            supply.setPaymentStatus("UNCOMPLETED");
        }

        ShopSupply updated = shopSupplyRepository.save(supply);
        return convertToDTO(updated);
    }

    // MAP ENTITY
    private ShopSupplyItem mapItemDTOToEntity(ShopSupplyItemDTO itemDTO, ShopSupply parent) {
        ShopSupplyItem item = new ShopSupplyItem();
        item.setItemId(IdGenerator.generate("SITE"));
        item.setShopSupply(parent);

        String shopId = itemDTO.getShopId();
        if (shopId == null) {
            throw new ResourceNotFoundException("Shop ID is missing for item: " + itemDTO.getProductName());
        }

        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found: " + shopId));
        item.setShop(shop);

        Product product = productRepository.findById(itemDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + itemDTO.getProductId()));
        item.setProduct(product);

        item.setQtySupplied(itemDTO.getQuantity());
        item.setQtyReturned(itemDTO.getReturnQuantity());
        item.setQtyExpired(itemDTO.getExpiredQuantity());
        item.setUnitPrice(itemDTO.getPrice() != null ? itemDTO.getPrice() : product.getUnitPrice());
        return item;
    }

    // CONVERT DTO
    private ShopSupplyDTO convertToDTO(ShopSupply supply) {
        ShopSupplyDTO dto = new ShopSupplyDTO();
        dto.setSupplyId(supply.getSupplyId());
        dto.setSupplyDate(supply.getSupplyDate());

        if (supply.getVehicle() != null) {
            dto.setVehicleId(supply.getVehicle().getVehicleId());
            dto.setVehicleNo(supply.getVehicle().getVehicleNo());
        }
        if (supply.getSalesman() != null) {
            dto.setSalesmanId(supply.getSalesman().getUserId());
            dto.setSalesmanName(supply.getSalesman().getUsername());
        }
        if (supply.getDriver() != null) {
            dto.setDriverId(supply.getDriver().getDriverId());
            dto.setDriverName(supply.getDriver().getName());
        }

        BigDecimal total = BigDecimal.ZERO;
        List<ShopSupplyItemDTO> itemDTOs = new ArrayList<>();

        if (supply.getItems() != null) {
            for (ShopSupplyItem item : supply.getItems()) {
                ShopSupplyItemDTO itemDto = new ShopSupplyItemDTO();
                if (item.getShop() != null) {
                    itemDto.setShopId(item.getShop().getShopId());
                    if (dto.getShopName() == null) {
                        dto.setShopName(item.getShop().getShopName());
                        dto.setShopId(item.getShop().getShopId());
                    }
                }
                if (item.getProduct() != null) {
                    itemDto.setProductId(item.getProduct().getProId());
                    itemDto.setProductName(item.getProduct().getName());
                }
                itemDto.setQuantity(item.getQtySupplied());
                itemDto.setReturnQuantity(item.getQtyReturned());
                itemDto.setExpiredQuantity(item.getQtyExpired());

                BigDecimal price = item.getUnitPrice() != null ? item.getUnitPrice() : BigDecimal.ZERO;
                itemDto.setPrice(price);

                total = total.add(price.multiply(BigDecimal.valueOf(item.getQtySupplied() - item.getQtyReturned() - item.getQtyExpired())));
                itemDTOs.add(itemDto);
            }
        }

        dto.setItems(itemDTOs);
        dto.setTotalAmount(total);
        dto.setPaidAmount(supply.getPaidAmount());
        dto.setOutstandingAmount(supply.getOutstandingAmount());
        dto.setPaymentStatus(supply.getPaymentStatus());
        return dto;
    }
}