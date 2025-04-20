package com.assignment.ecommerce_rookie.service;

import com.assignment.ecommerce_rookie.dto.ProductDTO;
import com.assignment.ecommerce_rookie.dto.ProductResponse;

public interface IProductService {

    ProductResponse getAllProducts(int pageNumber, int pageSize, String sortBy, String sortOrder);

    ProductDTO addProduct(ProductDTO productDTO, Long categoryId);

    ProductDTO updateProduct(ProductDTO productDTO, Long productId);

    ProductDTO deleteProduct(Long productId);

    ProductResponse searchByCategory(Long categoryId, int pageNumber, int pageSize, String sortBy, String sortOrder);

    ProductResponse searchByKeyWord(String keyword, int pageNumber, int pageSize, String sortBy, String sortOrder);

}
