package com.assignment.ecommerce_rookie.mapper;

import com.assignment.ecommerce_rookie.dto.request.ProductDTO;
import com.assignment.ecommerce_rookie.dto.response.ProductResponse;
import com.assignment.ecommerce_rookie.model.Category;
import com.assignment.ecommerce_rookie.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

import java.util.HashSet;
import java.util.List;
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

    default ProductResponse initProductResponse(Page<Product> productsPage, List<ProductDTO> productDTOs) {
        return ProductResponse.builder()
                .products(productDTOs)
                .pageNumber(productsPage.getNumber())
                .pageSize(productsPage.getSize())
                .totalElements(productsPage.getTotalElements())
                .lastPage(productsPage.isLast())
                .totalPages(productsPage.getTotalPages())
                .build();
    }

    void updateProductFromDto(ProductDTO dto, @MappingTarget Product product);
}
