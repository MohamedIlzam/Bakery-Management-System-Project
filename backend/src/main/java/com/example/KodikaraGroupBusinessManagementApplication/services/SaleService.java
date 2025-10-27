package com.example.KodikaraGroupBusinessManagementApplication.services;

import com.example.KodikaraGroupBusinessManagementApplication.DTO.SaleRequestDTO;
import com.example.KodikaraGroupBusinessManagementApplication.DTO.SaleResponseDTO;
import com.example.KodikaraGroupBusinessManagementApplication.DTO.SaleUpdateDTO;
import com.example.KodikaraGroupBusinessManagementApplication.model.Sale; // Import Sale model
import java.time.LocalDate;
import java.util.List;

public interface SaleService {
    SaleResponseDTO createSale(SaleRequestDTO dto);
    SaleResponseDTO getSaleById(String saleId);
    List<SaleResponseDTO> getAllSales();
    void deleteSale(String saleId);
    List<SaleResponseDTO> getSaleByDate(LocalDate date);
    List<SaleResponseDTO> getSaleByDateRange(LocalDate startDate, LocalDate endDate);
    void deleteSaleByDate(LocalDate date);

    SaleResponseDTO updateSale(String saleId, SaleUpdateDTO dto);

    // Added helpers for ReportService to access
    List<SaleResponseDTO> convertToResponseDTOList(List<Sale> sales);
    SaleResponseDTO convertToResponseDTO(Sale sale);
}