package com.assignment.ecommerce_rookie.mapper;


import com.assignment.ecommerce_rookie.dto.CategoryDTO;
import com.assignment.ecommerce_rookie.model.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toCategory(CategoryDTO categoryDTO);

    CategoryDTO toCategoryDTO(Category category);

}
