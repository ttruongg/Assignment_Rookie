package com.assignment.ecommerce_rookie.controller.web;

import com.assignment.ecommerce_rookie.constants.AppConstants;
import com.assignment.ecommerce_rookie.dto.CategoryDTO;
import com.assignment.ecommerce_rookie.dto.CategoryResponse;
import com.assignment.ecommerce_rookie.service.ICategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final ICategoryService categoryService;

    public CategoryController(ICategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<CategoryResponse> getCategories(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) int pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) int pageSize,
            @RequestParam(defaultValue = AppConstants.SORT_CATEGORIES_BY, required = false) String sortBy,
            @RequestParam(defaultValue = AppConstants.SORT_ORDER, required = false) String sortOrder
    ) {

        CategoryResponse categories = categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder, keyword);
        return new ResponseEntity<>(categories, HttpStatus.OK);

    }




}
