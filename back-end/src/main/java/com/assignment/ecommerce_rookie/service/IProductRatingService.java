package com.assignment.ecommerce_rookie.service;

import com.assignment.ecommerce_rookie.dto.ProductRatingDTO;

import java.util.List;

public interface IProductRatingService {
    ProductRatingDTO rateProduct(ProductRatingDTO ratingDTO);
    ProductRatingDTO updateRating(ProductRatingDTO ratingDTO);
    List<ProductRatingDTO> getRatingsByProduct(Long productId);
}
