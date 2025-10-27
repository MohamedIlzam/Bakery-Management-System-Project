package com.example.KodikaraGroupBusinessManagementApplication.services;

import com.example.KodikaraGroupBusinessManagementApplication.DTO.ProductDTO;
import com.example.KodikaraGroupBusinessManagementApplication.Repo.ProductRepository;
import com.example.KodikaraGroupBusinessManagementApplication.exception.ResourceNotFoundException;
import com.example.KodikaraGroupBusinessManagementApplication.model.Product;
import com.example.KodikaraGroupBusinessManagementApplication.util.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Collectors;
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
        if (repo.findByName(dto.getName()).isPresent()) {
            throw new IllegalArgumentException("Product already exists with name: " + dto.getName());
        }

        Product p = toEntity(dto);

        p.setProId(IdGenerator.productId());
        p.setActive(true);
        p.setStatus("Available");

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
        // FIX: Add .stream() and .collect() to convert to List
        return repo.findAllByActiveTrue().stream().map(this::toDto).collect(Collectors.toList());
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
        // FIX: Do NOT set ProId here. It's set in create()
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