package com.assignment.ecommerce_rookie.repository;

import com.assignment.ecommerce_rookie.model.Category;
import com.assignment.ecommerce_rookie.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    //Page<Product> findByCategoryOrderByPriceAsc(Category category, Pageable page);
    Page<Product> findByProductNameLikeIgnoreCase(String keyword, Pageable page);

    @EntityGraph(attributePaths = "categories")
    Page<Product> findAll(Specification<Product> spec, Pageable pageable);



}
