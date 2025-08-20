package com.assignment.ecommerce_rookie.service.impl;

import com.assignment.ecommerce_rookie.dto.request.CategoryDTO;
import com.assignment.ecommerce_rookie.exception.APIException;
import com.assignment.ecommerce_rookie.exception.NotFoundException;
import com.assignment.ecommerce_rookie.mapper.CategoryMapper;
import com.assignment.ecommerce_rookie.model.Category;
import com.assignment.ecommerce_rookie.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class categoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl CategoryServiceImpl;

    @Test
    void testCreateCategory_Success_returnCategoryDTO() {

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategoryName("Electronics");

        Category category = new Category();
        category.setCategoryName("Category");

        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setCategoryName("Category");

        CategoryDTO savedCategoryDTO = new CategoryDTO();
        savedCategoryDTO.setId(1L);
        savedCategoryDTO.setCategoryName("Category");

        when(categoryMapper.toCategory(categoryDTO)).thenReturn(category);
        when(categoryRepository.findByCategoryName("Category")).thenReturn(null);
        when(categoryRepository.save(category)).thenReturn(savedCategory);
        when(categoryMapper.toCategoryDTO(savedCategory)).thenReturn(savedCategoryDTO);


        CategoryDTO result = CategoryServiceImpl.createCategory(categoryDTO);


        assertNotNull(result);
        assertEquals(1L, result.getId());
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
            CategoryServiceImpl.createCategory(categoryDTO);
        });

        assertEquals("Category already exists!", exception.getMessage());

        verify(categoryMapper).toCategory(categoryDTO);
        verify(categoryRepository).findByCategoryName("Category");
        verify(categoryRepository, never()).save(any());
        verify(categoryMapper, never()).toCategoryDTO(any());
    }

    //delete category
    @Test
    void deleteCategory_successful() {

        Long categoryId = 1L;
        Category category = new Category();
        category.setId(categoryId);
        category.setCategoryName("Electronics");

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(categoryId);
        categoryDTO.setCategoryName("Electronics");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryMapper.toCategoryDTO(category)).thenReturn(categoryDTO);


        CategoryDTO result = CategoryServiceImpl.deleteCategory(categoryId);


        assertNotNull(result);
        assertEquals(categoryId, result.getId());
        verify(categoryRepository).deleteById(categoryId);
        verify(categoryMapper).toCategoryDTO(category);
    }

    @Test
    void deleteCategory_notFound_throwsException() {

        Long categoryId = 100L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());


        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> CategoryServiceImpl.deleteCategory(categoryId));

        assertEquals("Category not found with categoryId:100", exception.getMessage());
        verify(categoryRepository, never()).deleteById(any());
        verify(categoryMapper, never()).toCategoryDTO(any());
    }

    @Test
    void updateCategory_successful() {

        Long categoryId = 1L;

        CategoryDTO inputDTO = new CategoryDTO();
        inputDTO.setCategoryName("Updated Category");

        Category existingCategory = new Category();
        existingCategory.setId(categoryId);
        existingCategory.setCategoryName("Old Category");

        Category updatedCategoryEntity = new Category();
        updatedCategoryEntity.setId(categoryId);
        updatedCategoryEntity.setCategoryName("Updated Category");

        CategoryDTO resultDTO = new CategoryDTO();
        resultDTO.setId(categoryId);
        resultDTO.setCategoryName("Updated Category");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryMapper.toCategory(inputDTO)).thenReturn(updatedCategoryEntity);
        when(categoryRepository.save(updatedCategoryEntity)).thenReturn(updatedCategoryEntity);
        when(categoryMapper.toCategoryDTO(updatedCategoryEntity)).thenReturn(resultDTO);


        CategoryDTO updated = CategoryServiceImpl.updateCategory(inputDTO, categoryId);


        assertNotNull(updated);
        assertEquals(categoryId, updated.getId());
        assertEquals("Updated Category", updated.getCategoryName());
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).save(updatedCategoryEntity);
        verify(categoryMapper).toCategory(inputDTO);
        verify(categoryMapper).toCategoryDTO(updatedCategoryEntity);
    }

    @Test
    void updateCategory_notFound_throwsException() {

        Long categoryId = 999L;
        CategoryDTO inputDTO = new CategoryDTO();
        inputDTO.setCategoryName("Should Not Matter");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());


        NotFoundException ex = assertThrows(NotFoundException.class, () ->
                CategoryServiceImpl.updateCategory(inputDTO, categoryId)
        );

        assertTrue(ex.getMessage().contains("Category not found with categoryId"));
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository, never()).save(any());
        verify(categoryMapper, never()).toCategory(any());
        verify(categoryMapper, never()).toCategoryDTO(any());
    }

}