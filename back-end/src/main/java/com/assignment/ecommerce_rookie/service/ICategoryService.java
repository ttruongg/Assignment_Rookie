package com.assignment.ecommerce_rookie.service;

import com.assignment.ecommerce_rookie.dto.CategoryDTO;
import com.assignment.ecommerce_rookie.dto.CategoryResponse;

public interface ICategoryService {

    CategoryResponse getAllCategories(int pageNumber, int pageSize, String sortBy, String sortOrder);

    CategoryDTO createCategory(CategoryDTO categoryDTO);

    CategoryDTO deleteCategory(Long categoryId);

    CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId);

}
