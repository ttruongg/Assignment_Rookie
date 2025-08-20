package com.assignment.ecommerce_rookie.service;

import com.assignment.ecommerce_rookie.dto.request.CategoryDTO;
import com.assignment.ecommerce_rookie.dto.response.CategoryResponse;

public interface ICategoryService {

    CategoryResponse getAllCategories(int pageNumber, int pageSize, String sortBy, String sortOrder, String keyword);

    CategoryDTO createCategory(CategoryDTO categoryDTO);

    CategoryDTO deleteCategory(Long categoryId);

    CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId);

}
