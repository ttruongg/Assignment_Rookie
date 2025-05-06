package com.assignment.ecommerce_rookie.service;

import com.assignment.ecommerce_rookie.dto.ProductDTO;
import com.assignment.ecommerce_rookie.dto.ProductResponse;

public interface IProductService {
    /**
     * Retrieves a paginated list of products based on the provided filters and sorting options.
     *
     * @param pageNumber the page number to retrieve (0-based index)
     * @param pageSize   the number of products per page
     * @param sortBy     the field to sort by
     * @param sortOrder  the order of sorting (asc or desc)
     * @param keyword    a keyword to filter products by name or description
     * @param category   a category to filter products by
     * @return a ProductResponse object containing the list of products and pagination information
     */
    ProductResponse getAllProducts(int pageNumber, int pageSize, String sortBy, String sortOrder, String keyword, String category);

    ProductDTO addProduct(ProductDTO productDTO);

    ProductDTO updateProduct(ProductDTO productDTO, Long productId);

    ProductDTO deleteProduct(Long productId);


}
