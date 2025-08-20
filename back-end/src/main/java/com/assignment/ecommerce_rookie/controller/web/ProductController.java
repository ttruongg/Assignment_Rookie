package com.assignment.ecommerce_rookie.controller.web;

import com.assignment.ecommerce_rookie.constants.AppConstants;
import com.assignment.ecommerce_rookie.dto.request.ProductDTO;
import com.assignment.ecommerce_rookie.dto.response.ProductResponse;
import com.assignment.ecommerce_rookie.service.IProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {
    private final IProductService productService;

    @GetMapping
    public ResponseEntity<ProductResponse> getAllProduct(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name ="featured", required = false) Boolean featured,
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER) int pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE) int pageSize,
            @RequestParam(defaultValue = AppConstants.SORT_PRODUCTS_BY) String sortBy,
            @RequestParam(defaultValue = AppConstants.SORT_ORDER) String sortOrder
    ) {
        ProductResponse products = productService.getAllProducts(pageNumber, pageSize, sortBy, sortOrder, keyword, category, featured);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable("id") Long productId) {
        ProductDTO product = productService.getProductById(productId);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }


}
