package com.assignment.ecommerce_rookie.mapper;


import com.assignment.ecommerce_rookie.dto.request.CategoryDTO;
import com.assignment.ecommerce_rookie.dto.response.CategoryResponse;
import com.assignment.ecommerce_rookie.model.Category;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toCategory(CategoryDTO categoryDTO);

    CategoryDTO toCategoryDTO(Category category);

    default CategoryResponse initCategoryResponse(Page<Category> categoryPage, List<CategoryDTO> categories) {
        return CategoryResponse.builder()
                .categories(categories)
                .pageNumber(categoryPage.getNumber())
                .pageSize(categoryPage.getSize())
                .totalElements(categoryPage.getTotalElements())
                .totalPages(categoryPage.getTotalPages())
                .lastPage(categoryPage.isLast())
                .build();
    }

}
