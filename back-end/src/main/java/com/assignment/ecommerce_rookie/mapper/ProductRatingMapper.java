package com.assignment.ecommerce_rookie.mapper;

import com.assignment.ecommerce_rookie.dto.request.ProductRatingDTO;
import com.assignment.ecommerce_rookie.model.ProductRating;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductRatingMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "productId", source = "product.id")
    ProductRatingDTO toProductRatingDTO(ProductRating productRating);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "created_at", ignore = true)
    @Mapping(target = "updated_at", ignore = true)
    ProductRating toProductRating(ProductRatingDTO dto);
}
