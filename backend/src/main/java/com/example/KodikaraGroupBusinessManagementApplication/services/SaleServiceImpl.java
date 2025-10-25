package com.example.KodikaraGroupBusinessManagementApplication.services;

import com.example.KodikaraGroupBusinessManagementApplication.DTO.*;
import com.example.KodikaraGroupBusinessManagementApplication.Repo.*;
import com.example.KodikaraGroupBusinessManagementApplication.exception.ResourceNotFoundException;
import com.example.KodikaraGroupBusinessManagementApplication.model.*;
import com.example.KodikaraGroupBusinessManagementApplication.util.IdGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
    @RequiredArgsConstructor
    public class SaleServiceImpl implements SaleService {

        private final SaleRepository saleRepository;
        private final ShopRepository shopRepository;
        private final SaleDetailRepository saleDetailRepository;
        private final VehicleRepository vehicleRepository;
        private final DriverRepository driverRepository;
        private final ProductRepository productRepository;


        @Override
        @Transactional
        public SaleResponseDTO createSale(SaleRequestDTO dto) {
            try {
                Shop shop = shopRepository.findByShopName(dto.getShopName())
                        .orElseGet(() -> {
                            Shop newShop = new Shop();
                            newShop.setShopId(IdGenerator.generate("SHOP"));
                            newShop.setShopName(dto.getShopName());
                            newShop.setOwnerName(dto.getOwnerName());
                            newShop.setContactNo(dto.getContactNo());
                            newShop.setAddress("Default Address");
                            return shopRepository.save(newShop);
                        });

                Vehicle vehicle = vehicleRepository.findByVehicleNo(dto.getVehicleNo())
                        .orElseGet(() -> {
                            Vehicle newVehicle = new Vehicle();
                            newVehicle.setVehicleId(IdGenerator.generate("VEH"));
                            newVehicle.setVehicleNo(dto.getVehicleNo());
                            newVehicle.setDriverName(dto.getDriverName());
                            newVehicle.setVehicleType("DELIVERY");
                            return vehicleRepository.save(newVehicle);
                        });
                Driver driver=driverRepository.findByName(dto.getDriverName()).orElseGet(() -> {
                   Driver newDriver = new Driver();
                   newDriver.setId(IdGenerator.generate("DRV"));
                   newDriver.setName(dto.getDriverName());
                   return driverRepository.save(newDriver);
                });
                Sale sale = new Sale();
                sale.setSaleId(IdGenerator.saleId());
                sale.setShop(shop);
                sale.setVehicle(vehicle);
                sale.setDriver(driver);
                sale.setSaleDate(LocalDate.now());
                sale.setPaymentMethod("CASH");

                BigDecimal totalAmount = BigDecimal.ZERO;
                List<SaleDetail> saleDetails = new ArrayList<>();

                for (SaleDTO item : dto.getItems()) {
                    Product product = productRepository.findByName(item.getProductName())
                            .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + item.getProductName()));
//                    BigDecimal subtotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
//                    totalAmount = totalAmount.add(subtotal);
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
                sale.setTotalAmount(totalAmount);
                sale.setSaleDetails(saleDetails);

                Sale savedSale = saleRepository.save(sale);
                List<SaleItemResponse> responseItems = new ArrayList<>();
                for (SaleDetail detail : savedSale.getSaleDetails()) {
                    responseItems.add(new SaleItemResponse(
                            detail.getProduct().getName(),
                            detail.getQty(),
                            detail.getSubTot()
                    ));
                }


                return new SaleResponseDTO(
                        savedSale.getSaleId(),
                        shop.getShopName(),
                        shop.getOwnerName(),
                        shop.getContactNo(),
                        driver.getName(),
                        vehicle.getVehicleNo(),
                        responseItems,
                        totalAmount,
                        (LocalDate) savedSale.getSaleDate()
                );

            } catch (Exception e) {
                throw new RuntimeException("Error creating sale: " + e.getMessage());
            }
        }

        @Override
        public SaleResponseDTO getSaleById(String saleId) {
            Sale sale = saleRepository.findById(saleId)
                    .orElseThrow(() -> new ResourceNotFoundException("Sale not found: " + saleId));
            return convertToResponseDTO(sale);
//            try {
//
////                return new SaleResponseDTO(
////                        sale.getSaleId(),
////                        sale.getShop().getShopName(),
////                        sale.getShop().getOwnerName(),
////                        sale.getShop().getContactNo(),
////                        sale.getVehicle().getDriverName(),
////                        sale.getVehicle().getVehicleNo(),
////                        sale.getTotalAmount(),
////                        sale.getSaleDate()
////                );
//                return convertToResponseDTO(sale);
//            } catch (Exception e) {
//                throw new RuntimeException("Error retrieving sale: " + e.getMessage());
//            }
        }

        @Override
        public List<SaleResponseDTO> getAllSales() {
            List<Sale> sales = saleRepository.findAll();
//            List<SaleResponseDTO> responses = new ArrayList<>();
//
//            for (Sale sale : sales) {
//                {
//                    responses.add(new SaleResponseDTO(
//                            sale.getSaleId(),
//                            sale.getShop().getShopName(),
//                            sale.getShop().getOwnerName(),
//                            sale.getShop().getContactNo(),
//                            sale.getVehicle().getDriverName(),
//                            sale.getVehicle().getVehicleNo(),
//                            sale.getTotalAmount(),
//                            sale.getSaleDate()
//                    ));
//                }
//            }
//
//            return responses;
            return convertToResponseDTOList(sales);
        }

        @Override
        public void deleteSale(String saleId) {
            if (!saleRepository.existsById(saleId)) {
                throw new ResourceNotFoundException("Sale not found: " + saleId);
            }
            saleRepository.deleteById(saleId);
        }
        @Override
        public List<SaleResponseDTO> getSaleByDate(LocalDate saleDate){
            List<Sale> sales = saleRepository.findBySaleDate(saleDate);
            if(sales.isEmpty()){
                throw new ResourceNotFoundException("Sale not found: "+saleDate);
            }
            return convertToResponseDTOList(sales);
        }
        @Override
        public List<SaleResponseDTO> getSaleByDateRange(LocalDate startDate,LocalDate endDate){
            List<Sale> sales =saleRepository.findBySaleDateBetween(startDate,endDate);
            if(sales.isEmpty()){
                throw new ResourceNotFoundException("Sale not found between: "+startDate+" "+ endDate+" ");
            }
            return convertToResponseDTOList(sales);
        }
        @Override
        @Transactional
        public void deleteSaleByDate(LocalDate saleDate){
            if(!saleRepository.existsBySaleDate(saleDate)){
                throw new ResourceNotFoundException("Sale not found: "+saleDate);
            }
            saleRepository.deleteBySaleDate(saleDate);
        }
        private SaleResponseDTO convertToResponseDTO(Sale sale){
            List<SaleItemResponse> items =new ArrayList<>();
            if(sale.getSaleDetails() !=null){
                for(SaleDetail detail : sale.getSaleDetails()) {
                    if (detail != null && detail.getProduct() != null) {
                        items.add(new SaleItemResponse(
                                detail.getProduct().getName(), /*Product.getName()*/
                                detail.getQty(),
                                detail.getSubTot()
                        ));
                    }
                }
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
                    sale.getTotalAmount(),
                    (LocalDate) sale.getSaleDate()
            );
        }
        public List<SaleResponseDTO> convertToResponseDTOList(List<Sale> sales){
            List<SaleResponseDTO> response=new ArrayList<>();
            for(Sale sale : sales){
                response.add(convertToResponseDTO(sale));
            }
            return response;
        }

        @Transactional
        @Override
        public SaleResponseDTO updateSale(String saleId, SaleUpdateDTO dto){
            Sale sale = saleRepository.findById(saleId)
                    .orElseThrow(() -> new ResourceNotFoundException("Sale not found: " + saleId));
            dto.getPaymentMethod().ifPresent(sale::setPaymentMethod);
            dto.getSaleDate().ifPresent(sale::setSaleDate);

            dto.getShopName().ifPresent(shopName -> {
                Shop shop = shopRepository.findByShopName(shopName)
                        .orElseThrow(() -> new ResourceNotFoundException("Shop not found: "+ shopName));
                sale.setShop(shop);
            });

            dto.getVehicleNo().ifPresent(vehicleNo -> {
                Vehicle vehicle =vehicleRepository.findByVehicleNo(vehicleNo)
                        .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found: "+ vehicleNo));
                sale.setVehicle(vehicle);
            });
            dto.getDriverName().ifPresent(driverName -> {
                Driver driver =driverRepository.findByName(driverName)
                        .orElseThrow(() -> new ResourceNotFoundException("Driver not found: "+ driverName));
                sale.setDriver(driver);
            });
            dto.getItems().ifPresent(newItemsList -> {
                BigDecimal newTotalAmount = BigDecimal.ZERO;
                saleDetailRepository.deleteAll(sale.getSaleDetails());
                List<SaleDetail> newDetails = new ArrayList<>();
                for(SaleDTO item : newItemsList){
                    Product product = productRepository.findByName(item.getProductName())
                            .orElseThrow(() -> new ResourceNotFoundException("Product not found: "+ item.getProductName()));

                    SaleDetail detail = new SaleDetail();
                    detail.setSdetailId(IdGenerator.saleDetailId());
                    detail.setSale(sale);
                    detail.setProduct(product);
                    detail.setQty(item.getQuantity());
                    detail.setUnitPrice(item.getPrice());

                    BigDecimal subtotal= item.getPrice().multiply(new BigDecimal(item.getQuantity()));
                    detail.setSubTot(subtotal);
                    newTotalAmount = newTotalAmount.add(subtotal);

                    newDetails.add(detail);
                }

                sale.setSaleDetails(newDetails);
                sale.setTotalAmount(newTotalAmount);
            });

            Sale updatedSale = saleRepository.save(sale);

            return convertToResponseDTO(updatedSale);

        }
    }
