package com.assignment.ecommerce_rookie.repository;

import com.assignment.ecommerce_rookie.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByCategoryName(String categoryName);

}
