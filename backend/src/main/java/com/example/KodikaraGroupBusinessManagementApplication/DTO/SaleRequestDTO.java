package com.example.KodikaraGroupBusinessManagementApplication.DTO;

import lombok.Data;
import java.util.List;

@Data
public class SaleRequestDTO {
    private String shopName;
    private String ownerName;
    private String contactNo;
    private String driverName;
    private String vehicleNo;
    private List<SaleDTO> items;

}
