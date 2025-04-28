package com.assignment.ecommerce_rookie.service;

import com.assignment.ecommerce_rookie.dto.ProductDTO;
import com.assignment.ecommerce_rookie.dto.ProductResponse;

public interface IProductService {

    ProductResponse getAllProducts(int pageNumber, int pageSize, String sortBy, String sortOrder, String keyword, String category);

    ProductDTO addProduct(ProductDTO productDTO);

    ProductDTO updateProduct(ProductDTO productDTO, Long productId);

    ProductDTO deleteProduct(Long productId);


}
