package com.assignment.ecommerce_rookie.service;

import com.assignment.ecommerce_rookie.dto.request.CartDTO;

public interface ICartService {
    CartDTO addProductToCart(Long productId, int quantity);
}
