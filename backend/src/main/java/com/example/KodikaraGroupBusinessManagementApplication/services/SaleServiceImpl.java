package com.example.KodikaraGroupBusinessManagementApplication.services;

import com.example.KodikaraGroupBusinessManagementApplication.DTO.SaleDTO;
import com.example.KodikaraGroupBusinessManagementApplication.DTO.SaleItemResponse;
import com.example.KodikaraGroupBusinessManagementApplication.DTO.SaleRequestDTO;
import com.example.KodikaraGroupBusinessManagementApplication.DTO.SaleResponseDTO;
import com.example.KodikaraGroupBusinessManagementApplication.Repo.*;
import com.example.KodikaraGroupBusinessManagementApplication.exception.ResourceNotFoundException;
import com.example.KodikaraGroupBusinessManagementApplication.model.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

                BigDecimal totalAmount = BigDecimal.ZERO;
                List<SaleResponseDTO> responseItems = new ArrayList<>();

                for (SaleDTO item : dto.getItems()) {
                    BigDecimal subtotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                    totalAmount = totalAmount.add(subtotal);

                    responseItems.add(new SaleResponseDTO(
                            item.getProductName(),
                            item.getQuantity(),
                            subtotal
                    ));
                }
                Sale sale = new Sale();
                sale.setSaleId(IdGenerator.saleId());
                sale.setShop(shop);
                sale.setVehicle(vehicle);
                sale.setSaleDate(LocalDate.now());
                sale.setTotalAmount(totalAmount);
                sale.setPaymentMethod("CASH");


                Sale savedSale = saleRepository.save(sale);


                return new SaleResponseDTO(
                        savedSale.getSaleId(),
                        shop.getShopName(),
                        shop.getOwnerName(),
                        shop.getContactNo(),
                        vehicle.getDriverName(),
                        vehicle.getVehicleNo(),
                        totalAmount,
                        savedSale.getSaleDate()
                );

            } catch (Exception e) {
                throw new RuntimeException("Error creating sale: " + e.getMessage());
            }
        }

        @Override
        public SaleResponseDTO getSaleById(String saleId) {
            Sale sale = saleRepository.findById(saleId)
                    .orElseThrow(() -> new ResourceNotFoundException("Sale not found: " + saleId));

            try {

                return new SaleResponseDTO(
                        sale.getSaleId(),
                        sale.getShop().getShopName(),
                        sale.getShop().getOwnerName(),
                        sale.getShop().getContactNo(),
                        sale.getVehicle().getDriverName(),
                        sale.getVehicle().getVehicleNo(),
                        sale.getTotalAmount(),
                        sale.getSaleDate()
                );
            } catch (Exception e) {
                throw new RuntimeException("Error retrieving sale: " + e.getMessage());
            }
        }

        @Override
        public List<SaleResponseDTO> getAllSales() {
            List<Sale> sales = saleRepository.findAll();
            List<SaleResponseDTO> responses = new ArrayList<>();

            for (Sale sale : sales) {
                {
                    responses.add(new SaleResponseDTO(
                            sale.getSaleId(),
                            sale.getShop().getShopName(),
                            sale.getShop().getOwnerName(),
                            sale.getShop().getContactNo(),
                            sale.getVehicle().getDriverName(),
                            sale.getVehicle().getVehicleNo(),
                            sale.getTotalAmount(),
                            sale.getSaleDate()
                    ));
                }
            }

            return responses;
        }

        @Override
        public void deleteSale(String saleId) {
            if (!saleRepository.existsById(saleId)) {
                throw new ResourceNotFoundException("Sale not found: " + saleId);
            }
            saleRepository.deleteById(saleId);
        }
        @Override
        public List<SaleResponseDTO> getSaleByDate(LocalDate date){
            List<Sale> sales = saleRepository.findByDate(date);
            if(sales.isEmpty()){
                throw new ResourceNotFoundException("Sale not found: "+date);
            }
            return convertToResponseDTOList(sales);
        }
        @Override
        public List<SaleResponseDTO> getSaleByDateRange(LocalDate startDate,LocalDate endDate){
            List<Sale> sales =saleRepository.findByDateBetween(startDate,endDate);
            if(sales.isEmpty()){
                throw new ResourceNotFoundException("Sale not found between: "+startDate+" "+ endDate+" ");
            }
            return convertToResponseDTOList(sales);
        }
        @Override
        @Transactional
        public void deleteSaleByDate(LocalDate date){
            if(!saleRepository.existsByDate(date)){
                throw new ResourceNotFoundException("Sale not found: "+date);
            }
            saleRepository.deleteByDate(date);
        }
        private SaleResponseDTO convertToResponseDTO(Sale sale){
            List<SaleItemResponse> items =new ArrayList<>();
            if(sale.getSaleDetails() !=null){
                for(SaleDetail detail : sale.getSaleDetails()){
                    items.add(new SaleItemResponse(
                            detail.getProduct().getName(), /*Product.getName()*/
                            detail.getQty(),
                            detail.getSubTot()
                    ));
                }
            }
            return new SaleResponseDTO(
                    sale.getSaleId(),
                    sale.getShop().getShopName(),
                    sale.getShop().getOwnerName(),
                    sale.getShop().getContactNo(),
                    sale.getVehicle().getVehicleNo(),
                    sale.getVehicle().getDriverName(),
                    items,
                    sale.getTotalAmount(),
                    sale.getSaleDate()
            );
        }
        private List<SaleResponseDTO> convertToResponseDTOList(List<Sale> sales){
            List<SaleResponseDTO> response=new ArrayList<>();
            for(Sale sale : sales){
                response.add(convertToResponseDTO(sale));
            }
            return response;

        }
    }
