package com.assignment.ecommerce_rookie.mapper;

import com.assignment.ecommerce_rookie.dto.ProductDTO;
import com.assignment.ecommerce_rookie.model.Category;
import com.assignment.ecommerce_rookie.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "categories", ignore = true)
    Product toProduct(ProductDTO productDTO);

    @Mapping(target = "categoryIds", source = "categories")
    ProductDTO toProductDTO(Product product);

    default Set<Long> mapCategoriesToIds(Set<Category> categories) {
        if (categories == null) {
            return new HashSet<>();
        }
        return categories.stream()
                .map(Category::getId)
                .collect(Collectors.toSet());
    }
}
