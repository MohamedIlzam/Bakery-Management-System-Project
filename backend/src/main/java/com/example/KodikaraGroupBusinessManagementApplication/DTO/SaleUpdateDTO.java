package com.example.KodikaraGroupBusinessManagementApplication.DTO;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Data
public class SaleUpdateDTO {

    private Optional<String> paymentMethod = Optional.empty();
    private Optional<LocalDate> saleDate = Optional.empty();
    private Optional<String> shopName = Optional.empty();
    private Optional<String> vehicleNo = Optional.empty();
    private Optional<String> driverName = Optional.empty();
    private Optional<List<SaleDTO>> items = Optional.empty();
}