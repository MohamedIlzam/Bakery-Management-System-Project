package com.example.KodikaraGroupBusinessManagementApplication.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopDTO {
    private String shopId;
    private String shopName;
    private String ownerName;
    private String contactNo;
    private String address;
}