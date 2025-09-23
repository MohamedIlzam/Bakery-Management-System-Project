package com.example.KodikaraGroupBusinessManagementApplication.Repo;


import com.example.KodikaraGroupBusinessManagementApplication.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository<string> extends JpaRepository<Product,string> {
   List<Product> findByStatus(String status);
}
