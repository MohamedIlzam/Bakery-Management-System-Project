package com.example.KodikaraGroupBusinessManagementApplication.Repo;


import com.example.KodikaraGroupBusinessManagementApplication.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    default Optional<Product> findByName(String name) // Fixed method name
    {
        return null;
    }

//    default Optional<Object> findByName(String name) {
//        return null;
//    }
}