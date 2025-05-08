package com.assignment.ecommerce_rookie.service;

import com.assignment.ecommerce_rookie.dto.ProductRatingDTO;
import com.assignment.ecommerce_rookie.dto.ProductRatingResponse;

import java.util.List;

public interface IProductRatingService {
    ProductRatingDTO rateProduct(ProductRatingDTO ratingDTO);
    ProductRatingDTO updateRating(ProductRatingDTO ratingDTO);
    List<ProductRatingResponse> getRatingsByProduct(Long productId);
}
