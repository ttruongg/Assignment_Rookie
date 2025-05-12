package com.assignment.ecommerce_rookie.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.assignment.ecommerce_rookie.dto.CategoryDTO;
import com.assignment.ecommerce_rookie.mapper.CategoryMapper;
import com.assignment.ecommerce_rookie.model.Category;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class CategoryMapperTest {

    private final CategoryMapper mapper = Mappers.getMapper(CategoryMapper.class);

    @Test
    void testToCategory() {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(1L);
        dto.setCategoryName("Tablet");
        dto.setDescription("Tablet description");

        Category entity = mapper.toCategory(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(dto.getId());
        assertThat(entity.getCategoryName()).isEqualTo(dto.getCategoryName());
        assertThat(entity.getDescription()).isEqualTo(dto.getDescription());
    }

    @Test
    void testToCategoryDTO() {
        Category entity = new Category();
        entity.setId(2L);
        entity.setCategoryName("Electronics");
        entity.setDescription("Electronic items");

        CategoryDTO dto = mapper.toCategoryDTO(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(entity.getId());
        assertThat(dto.getCategoryName()).isEqualTo(entity.getCategoryName());
        assertThat(dto.getDescription()).isEqualTo(entity.getDescription());
    }
}
