package com.example.KodikaraGroupBusinessManagementApplication.services;

import com.example.KodikaraGroupBusinessManagementApplication.DTO.ProductDTO;
import com.example.KodikaraGroupBusinessManagementApplication.Repo.ProductRepository;
import com.example.KodikaraGroupBusinessManagementApplication.exception.ResourceNotFoundException;
import com.example.KodikaraGroupBusinessManagementApplication.model.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repo;

    public ProductServiceImpl(ProductRepository repo) {
        this.repo = repo;
    }

    @Override
    public ProductDTO create(ProductDTO dto) {
        if (repo.existsById(dto.getProId())) {
            throw new IllegalArgumentException("Product already exists: " + dto.getProId());
        }
        Product p = toEntity(dto);
        p.setActive(true); // soft-delete default
        return toDto(repo.save(p));
    }

    @Override
    public ProductDTO update(String id, ProductDTO dto) {
        Product p = repo.findById(id).orElseThrow(() -> notFound(id));
        // update allowed fields only
        p.setName(dto.getName());
        p.setCategory(dto.getCategory());
        p.setUnitPrice(dto.getUnitPrice());
        return toDto(repo.save(p));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO get(String id) {
        return toDto(repo.findById(id).orElseThrow(() -> notFound(id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> listActive() {
        return repo.findAllByActiveTrue().stream().map(this::toDto).toList();
    }

    @Override
    public void softDelete(String id) {
        Product p = repo.findById(id).orElseThrow(() -> notFound(id));
        p.setActive(false);
        repo.save(p);
    }

    // -------- mapping helpers --------
    private Product toEntity(ProductDTO d) {
        Product p = new Product();
        p.setProId(d.getProId());
        p.setName(d.getName());
        p.setCategory(d.getCategory());
        p.setUnitPrice(d.getUnitPrice());
        return p;
    }

    private ProductDTO toDto(Product p) {
        ProductDTO d = new ProductDTO();
        d.setProId(p.getProId());
        d.setName(p.getName());
        d.setCategory(p.getCategory());
        d.setUnitPrice(p.getUnitPrice());
        return d;
    }

    private ResourceNotFoundException notFound(String id) {
        return new ResourceNotFoundException("Product not found: " + id);
    }
}
