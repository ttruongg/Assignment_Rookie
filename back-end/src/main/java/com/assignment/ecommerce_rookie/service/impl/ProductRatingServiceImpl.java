package com.assignment.ecommerce_rookie.service.impl;

import com.assignment.ecommerce_rookie.dto.ProductRatingDTO;
import com.assignment.ecommerce_rookie.dto.ProductRatingResponse;
import com.assignment.ecommerce_rookie.exception.APIException;
import com.assignment.ecommerce_rookie.exception.NotFoundException;
import com.assignment.ecommerce_rookie.mapper.ProductRatingMapper;
import com.assignment.ecommerce_rookie.model.Product;
import com.assignment.ecommerce_rookie.model.ProductRating;
import com.assignment.ecommerce_rookie.model.RatingId;
import com.assignment.ecommerce_rookie.model.User;
import com.assignment.ecommerce_rookie.repository.ProductRatingRepository;
import com.assignment.ecommerce_rookie.repository.ProductRepository;
import com.assignment.ecommerce_rookie.repository.UserRepository;
import com.assignment.ecommerce_rookie.service.IProductRatingService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductRatingServiceImpl implements IProductRatingService {
    private final ProductRatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductRatingMapper productRatingMapper;

    @Override
    public ProductRatingDTO rateProduct(ProductRatingDTO ratingDTO) {
        RatingId ratingId = new RatingId(ratingDTO.getUserId(), ratingDTO.getProductId());

        if (ratingRepository.existsById(ratingId)) {
            throw new APIException("You have already rated this product", HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findById(ratingDTO.getUserId())
                .orElseThrow(() -> new NotFoundException("User", "UserId", ratingDTO.getUserId()));

        Product product = productRepository.findById(ratingDTO.getProductId())
                .orElseThrow(() -> new NotFoundException("Product", "productId", ratingDTO.getProductId()));

        ProductRating rating = productRatingMapper.toProductRating(ratingDTO);

        rating.setId(ratingId);
        rating.setUser(user);
        rating.setProduct(product);

        ProductRating savedRating = ratingRepository.save(rating);
        return productRatingMapper.toProductRatingDTO(savedRating);
    }

    @Override
    public ProductRatingDTO updateRating(ProductRatingDTO ratingDTO) {
        RatingId ratingId = new RatingId(ratingDTO.getUserId(), ratingDTO.getProductId());

        ProductRating existingRating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new NotFoundException("Rating", "userId=" + ratingDTO.getUserId(), "productId=" + ratingDTO.getProductId()));

        existingRating.setRating(ratingDTO.getRating());
        existingRating.setComment(ratingDTO.getComment());

        ProductRating updatedRating = ratingRepository.save(existingRating);

        return productRatingMapper.toProductRatingDTO(updatedRating);
    }

    @Override
    public List<ProductRatingResponse> getRatingsByProduct(Long productId) {
        productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product", "productId", productId));

        return ratingRepository.getRatingByProduct(productId);

    }


}
