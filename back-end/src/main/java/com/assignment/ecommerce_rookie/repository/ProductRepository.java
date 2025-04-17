package com.assignment.ecommerce_rookie.repository;

import com.assignment.ecommerce_rookie.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {





}
