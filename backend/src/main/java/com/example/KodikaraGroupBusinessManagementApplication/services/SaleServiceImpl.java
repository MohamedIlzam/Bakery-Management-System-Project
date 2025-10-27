package com.example.KodikaraGroupBusinessManagementApplication.services;

import com.example.KodikaraGroupBusinessManagementApplication.DTO.*;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional // Apply default transactionality
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final ShopRepository shopRepository;
    private final SaleDetailRepository saleDetailRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final ProductRepository productRepository;

    @Override
    public SaleResponseDTO createSale(SaleRequestDTO dto) {
        Shop shop = shopRepository.findByShopName(dto.getShopName())
                .orElseGet(() -> {
                    Shop newShop = new Shop();
                    newShop.setShopId(IdGenerator.shopId());
                    newShop.setShopName(dto.getShopName());
                    newShop.setOwnerName(dto.getOwnerName());
                    newShop.setContactNo(dto.getContactNo());
                    return shopRepository.save(newShop);
                });

        Vehicle vehicle = vehicleRepository.findByVehicleNo(dto.getVehicleNo())
                .orElseGet(() -> {
                    Vehicle newVehicle = new Vehicle();
                    newVehicle.setVehicleId(IdGenerator.vehicleId());
                    newVehicle.setVehicleNo(dto.getVehicleNo());
                    return vehicleRepository.save(newVehicle);
                });

        Driver driver = driverRepository.findByName(dto.getDriverName()).orElseGet(() -> {
            Driver newDriver = new Driver();
            newDriver.setId(IdGenerator.driverId());
            newDriver.setName(dto.getDriverName());
            return driverRepository.save(newDriver);
        });

        Sale sale = new Sale();
        sale.setSaleId(IdGenerator.saleId());
        sale.setShop(shop);
        sale.setVehicle(vehicle);
        sale.setDriver(driver); // Set the driver
        sale.setSaleDate(LocalDate.now()); // Set the correct field
        sale.setPaymentMethod("CASH"); // Set default

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<SaleDetail> saleDetails = new ArrayList<>();

        if (dto.getItems() != null) {
            for (SaleDTO item : dto.getItems()) {
                Product product = productRepository.findByName(item.getProductName())
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + item.getProductName()));

                SaleDetail detail = new SaleDetail();
                detail.setSdetailId(IdGenerator.saleDetailId());
                detail.setSale(sale);
                detail.setProduct(product);
                detail.setQty(item.getQuantity());
                detail.setUnitPrice(item.getPrice());

                BigDecimal subtotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                detail.setSubTot(subtotal);
                totalAmount = totalAmount.add(subtotal);

                saleDetails.add(detail);
            }
        }
        sale.setTotalAmount(totalAmount);
        sale.setSaleDetails(saleDetails);

        Sale savedSale = saleRepository.save(sale);
        return convertToResponseDTO(savedSale);
    }

    @Override
    @Transactional(readOnly = true)
    public SaleResponseDTO getSaleById(String saleId) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with id: " + saleId));
        return convertToResponseDTO(sale);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaleResponseDTO> getAllSales() {
        List<Sale> sales = saleRepository.findAll();
        return convertToResponseDTOList(sales);
    }

    @Override
    public void deleteSale(String saleId) {
        if (!saleRepository.existsById(saleId)) {
            throw new ResourceNotFoundException("Sale not found with id: " + saleId);
        }
        saleRepository.deleteById(saleId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaleResponseDTO> getSaleByDate(LocalDate saleDate) {
        List<Sale> sales = saleRepository.findBySaleDate(saleDate);
        if (sales.isEmpty()) {
            throw new ResourceNotFoundException("Sale not found: " + saleDate);
        }
        return convertToResponseDTOList(sales);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaleResponseDTO> getSaleByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Sale> sales = saleRepository.findBySaleDateBetween(startDate, endDate);
        if (sales.isEmpty()) {
            throw new ResourceNotFoundException("Sale not found between: " + startDate + " " + endDate + " ");
        }
        return convertToResponseDTOList(sales);
    }

    @Override
    public void deleteSaleByDate(LocalDate saleDate) {
        if (!saleRepository.existsBySaleDate(saleDate)) {
            throw new ResourceNotFoundException("Sale not found: " + saleDate);
        }
        saleRepository.deleteBySaleDate(saleDate);
    }

    @Override
    public SaleResponseDTO updateSale(String saleId, SaleUpdateDTO dto) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found: " + saleId));

        dto.getPaymentMethod().ifPresent(sale::setPaymentMethod);
        dto.getSaleDate().ifPresent(sale::setSaleDate);

        dto.getShopName().ifPresent(shopName -> {
            Shop shop = shopRepository.findByShopName(shopName) // Use correct method
                    .orElseThrow(() -> new ResourceNotFoundException("Shop not found: " + shopName));
            sale.setShop(shop);
        });
        dto.getVehicleNo().ifPresent(vehicleNo -> {
            Vehicle vehicle = vehicleRepository.findByVehicleNo(vehicleNo)
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found: " + vehicleNo));
            sale.setVehicle(vehicle);
        });
        dto.getDriverName().ifPresent(driverName -> {
            Driver driver = driverRepository.findByName(driverName)
                    .orElseThrow(() -> new ResourceNotFoundException("Driver not found: " + driverName));
            sale.setDriver(driver);
        });

        // Update items (Replace logic)
        dto.getItems().ifPresent(newItemsList -> {
            BigDecimal newTotalAmount = BigDecimal.ZERO;

            // 1. Manually delete old details
            List<SaleDetail> oldDetails = new ArrayList<>(sale.getSaleDetails());
            if (!oldDetails.isEmpty()) {
                saleDetailRepository.deleteAll(oldDetails);
                saleDetailRepository.flush(); // Force execution
            }

            // 2. Create and add new details
            List<SaleDetail> newDetails = new ArrayList<>();
            for (SaleDTO item : newItemsList) {
                Product product = productRepository.findByName(item.getProductName())
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + item.getProductName()));

                SaleDetail detail = new SaleDetail();
                detail.setSdetailId(IdGenerator.saleDetailId());
                detail.setSale(sale);
                detail.setProduct(product);
                detail.setQty(item.getQuantity());
                detail.setUnitPrice(item.getPrice());
                BigDecimal subtotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                detail.setSubTot(subtotal);
                newTotalAmount = newTotalAmount.add(subtotal);
                newDetails.add(detail);
            }
            // 3. Set new list and total
            sale.setSaleDetails(newDetails);
            sale.setTotalAmount(newTotalAmount);
        });

        Sale updatedSale = saleRepository.save(sale);
        return convertToResponseDTO(updatedSale);
    }

    // --- Public Helper Methods (as declared in SaleService interface) ---

    @Override
    public SaleResponseDTO convertToResponseDTO(Sale sale) {
        if (sale == null) return null;

        List<SaleItemResponse> items = new ArrayList<>();
        if (sale.getSaleDetails() != null) {
            items = sale.getSaleDetails().stream()
                    .filter(detail -> detail != null && detail.getProduct() != null)
                    .map(detail -> new SaleItemResponse(
                            detail.getProduct().getName(),
                            detail.getQty(),
                            detail.getSubTot() != null ? detail.getSubTot() : BigDecimal.ZERO
                    ))
                    .collect(Collectors.toList());
        }

        String shopName = (sale.getShop() != null) ? sale.getShop().getShopName() : null;
        String ownerName = (sale.getShop() != null) ? sale.getShop().getOwnerName() : null;
        String contactNo = (sale.getShop() != null) ? sale.getShop().getContactNo() : null;
        String vehicleNo = (sale.getVehicle() != null) ? sale.getVehicle().getVehicleNo() : null;
        String driverName = (sale.getDriver() != null) ? sale.getDriver().getName() : null;

        return new SaleResponseDTO(
                sale.getSaleId(),
                shopName,
                ownerName,
                contactNo,
                driverName,
                vehicleNo,
                items,
                sale.getTotalAmount() != null ? sale.getTotalAmount() : BigDecimal.ZERO,
                sale.getSaleDate()
        );
    }

    @Override
    public List<SaleResponseDTO> convertToResponseDTOList(List<Sale> sales) {
        if (sales == null) return new ArrayList<>();
        return sales.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
}