package com.assignment.ecommerce_rookie.repository;

import com.assignment.ecommerce_rookie.model.Product;
import com.assignment.ecommerce_rookie.model.ProductRating;
import com.assignment.ecommerce_rookie.model.RatingId;
import com.assignment.ecommerce_rookie.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRatingRepository extends JpaRepository<ProductRating, RatingId> {

    Optional<ProductRating> findByUserAndProduct(User user, Product product);

    boolean existsByUserAndProduct(User user, Product product);

    List<ProductRating> findByProductId(Long productId);
}
