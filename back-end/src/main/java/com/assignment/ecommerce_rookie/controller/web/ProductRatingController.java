package com.assignment.ecommerce_rookie.controller.web;


import com.assignment.ecommerce_rookie.dto.ProductRatingDTO;
import com.assignment.ecommerce_rookie.dto.ProductRatingResponse;
import com.assignment.ecommerce_rookie.service.IProductRatingService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ratings")
@AllArgsConstructor
public class ProductRatingController {

    private final IProductRatingService productRatingService;

    @PostMapping
    @Transactional
    public ResponseEntity<ProductRatingDTO> rateProduct(@Valid @RequestBody ProductRatingDTO ratingDTO) {
        ProductRatingDTO createdRating = productRatingService.rateProduct(ratingDTO);
        return new ResponseEntity<>(createdRating, HttpStatus.CREATED);
    }

    @PutMapping("/{userId}/{productId}")
    @Transactional
    public ResponseEntity<ProductRatingDTO> updateRating(
            @PathVariable Long userId,
            @PathVariable Long productId,
            @Valid @RequestBody ProductRatingDTO ratingDTO) {

        ratingDTO.setUserId(userId);
        ratingDTO.setProductId(productId);

        ProductRatingDTO updatedRating = productRatingService.updateRating(ratingDTO);
        return new ResponseEntity<>(updatedRating, HttpStatus.OK);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<List<ProductRatingResponse>> getRatingsByProduct(@PathVariable Long productId) {
        List<ProductRatingResponse> ratings = productRatingService.getRatingsByProduct(productId);
        return new ResponseEntity<>(ratings, HttpStatus.OK);
    }


}
