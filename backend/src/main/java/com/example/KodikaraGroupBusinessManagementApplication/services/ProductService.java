package com.example.KodikaraGroupBusinessManagementApplication.services;

import com.example.KodikaraGroupBusinessManagementApplication.DTO.ProductDTO;
import java.util.List;

public interface ProductService {
    ProductDTO create(ProductDTO dto);
    ProductDTO update(String id, ProductDTO dto);
    ProductDTO get(String id);
    List<ProductDTO> listActive();
    void softDelete(String id);
}
