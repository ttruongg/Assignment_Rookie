package com.assignment.ecommerce_rookie.service.impl;

import com.assignment.ecommerce_rookie.dto.CategoryDTO;
import com.assignment.ecommerce_rookie.exception.APIException;
import com.assignment.ecommerce_rookie.mapper.CategoryMapper;
import com.assignment.ecommerce_rookie.model.Category;
import com.assignment.ecommerce_rookie.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class categoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private categoryService categoryService;

    @Test
    void testCreateCategory_Success_returnCategoryDTO() {

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategoryName("Electronics");

        Category category = new Category();
        category.setCategoryName("Category");

        Category savedCategory = new Category();
        savedCategory.setCategoryId(1L);
        savedCategory.setCategoryName("Category");

        CategoryDTO savedCategoryDTO = new CategoryDTO();
        savedCategoryDTO.setCategoryId(1L);
        savedCategoryDTO.setCategoryName("Category");

        when(categoryMapper.toCategory(categoryDTO)).thenReturn(category);
        when(categoryRepository.findByCategoryName("Category")).thenReturn(null);
        when(categoryRepository.save(category)).thenReturn(savedCategory);
        when(categoryMapper.toCategoryDTO(savedCategory)).thenReturn(savedCategoryDTO);


        CategoryDTO result = categoryService.createCategory(categoryDTO);


        assertNotNull(result);
        assertEquals(1L, result.getCategoryId());
        assertEquals("Category", result.getCategoryName());

        verify(categoryMapper).toCategory(categoryDTO);
        verify(categoryRepository).findByCategoryName("Category");
        verify(categoryRepository).save(category);
        verify(categoryMapper).toCategoryDTO(savedCategory);
    }

    @Test
    void testCreateCategory_AlreadyExists() {

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategoryName("Electronics");

        Category existingCategory = new Category();
        existingCategory.setCategoryName("Category");

        when(categoryMapper.toCategory(categoryDTO)).thenReturn(existingCategory);
        when(categoryRepository.findByCategoryName("Category")).thenReturn(existingCategory);


        APIException exception = assertThrows(APIException.class, () -> {
            categoryService.createCategory(categoryDTO);
        });

        assertEquals("Category already exists!", exception.getMessage());

        verify(categoryMapper).toCategory(categoryDTO);
        verify(categoryRepository).findByCategoryName("Category");
        verify(categoryRepository, never()).save(any());
        verify(categoryMapper, never()).toCategoryDTO(any());
    }


}