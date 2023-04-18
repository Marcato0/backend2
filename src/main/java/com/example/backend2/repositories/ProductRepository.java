package com.example.backend2.repositories;

import com.example.backend2.models.ProductModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<ProductModel, UUID> {

    List<ProductModel> findByNameContainingIgnoreCase(String name);

    Optional<ProductModel> findByNameIgnoreCase(String name);
}
