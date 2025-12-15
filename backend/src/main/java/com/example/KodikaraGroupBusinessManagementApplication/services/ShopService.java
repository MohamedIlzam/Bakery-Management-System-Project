package com.example.KodikaraGroupBusinessManagementApplication.services;

import com.example.KodikaraGroupBusinessManagementApplication.DTO.ShopDTO;
import com.example.KodikaraGroupBusinessManagementApplication.Repo.ShopRepository;
import com.example.KodikaraGroupBusinessManagementApplication.exception.ResourceNotFoundException;
import com.example.KodikaraGroupBusinessManagementApplication.model.Shop;
import com.example.KodikaraGroupBusinessManagementApplication.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ShopService {

    private final ShopRepository shopRepository;

    public ShopDTO createShop(ShopDTO dto) {
        Shop shop = new Shop();
        shop.setShopId(IdGenerator.generate("SHOP"));
        shop.setShopName(dto.getShopName());
        shop.setOwnerName(dto.getOwnerName());
        shop.setContactNo(dto.getContactNo());
        shop.setAddress(dto.getAddress());
        return convertToDTO(shopRepository.save(shop));
    }

    public List<ShopDTO> getAllShops() {
        return shopRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ShopDTO updateShop(String id, ShopDTO dto) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));

        shop.setShopName(dto.getShopName());
        shop.setOwnerName(dto.getOwnerName());
        shop.setContactNo(dto.getContactNo());
        shop.setAddress(dto.getAddress());
        return convertToDTO(shopRepository.save(shop));
    }

    public void deleteShop(String id) {
        if (!shopRepository.existsById(id)) throw new ResourceNotFoundException("Shop not found");
        shopRepository.deleteById(id);
    }

    private ShopDTO convertToDTO(Shop shop) {
        return new ShopDTO(
                shop.getShopId(),
                shop.getShopName(),
                shop.getOwnerName(),
                shop.getContactNo(),
                shop.getAddress()
        );
    }
}