package com.example.KodikaraGroupBusinessManagementApplication.Controller;

import com.example.KodikaraGroupBusinessManagementApplication.DTO.ProductDTO;
import com.example.KodikaraGroupBusinessManagementApplication.services.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping
    public ProductDTO create(@RequestBody ProductDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public ProductDTO update(@PathVariable String id, @RequestBody ProductDTO dto) {
        return service.update(id, dto);
    }

    @GetMapping("/{id}")
    public ProductDTO get(@PathVariable String id) {
        return service.get(id);
    }

    @GetMapping
    public List<ProductDTO> list() {
        return service.listActive();
    }

    @DeleteMapping("/{id}")
    public void softDelete(@PathVariable String id) {
        service.softDelete(id);
    }
}
