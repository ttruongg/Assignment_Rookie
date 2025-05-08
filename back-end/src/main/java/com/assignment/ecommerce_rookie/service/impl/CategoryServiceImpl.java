package com.assignment.ecommerce_rookie.service.impl;

import com.assignment.ecommerce_rookie.dto.CategoryDTO;
import com.assignment.ecommerce_rookie.dto.CategoryResponse;
import com.assignment.ecommerce_rookie.exception.APIException;
import com.assignment.ecommerce_rookie.exception.NotFoundException;
import com.assignment.ecommerce_rookie.mapper.CategoryMapper;
import com.assignment.ecommerce_rookie.model.Category;
import com.assignment.ecommerce_rookie.model.Product;
import com.assignment.ecommerce_rookie.repository.CategoryRepository;
import com.assignment.ecommerce_rookie.service.ICategoryService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public CategoryResponse getAllCategories(int pageNumber, int pageSize, String sortBy, String sortOrder, String keyword) {

        Pageable pageable = createPageable(pageNumber, pageSize, sortBy, sortOrder);
        Specification<Category> specification = createKeywordOnlySpecification(keyword);

        Page<Category> categoryPage = categoryRepository.findAll(specification, pageable);
        List<CategoryDTO> categoryDTOs = convertToCategoryDTOs(categoryPage.getContent());

        return buildCategoryResponse(categoryPage, categoryDTOs);
    }

    private Pageable createPageable(int pageNumber, int pageSize, String sortBy, String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        return PageRequest.of(pageNumber, pageSize, sort);
    }

    private Specification<Category> createKeywordOnlySpecification(String keyword) {
        return applyKeywordFilter(keyword);
    }

    private Specification<Category> applyKeywordFilter(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("categoryName")), "%" + keyword.toLowerCase() + "%");
    }

    private List<CategoryDTO> convertToCategoryDTOs(List<Category> categories) {
        return categories.stream()
                .map(categoryMapper::toCategoryDTO)
                .toList();
    }

    private CategoryResponse buildCategoryResponse(Page<Category> categoryPage, List<CategoryDTO> categoryDTOList) {
        CategoryResponse response = new CategoryResponse();
        response.setCategories(categoryDTOList);
        response.setPageNumber(categoryPage.getNumber());
        response.setPageSize(categoryPage.getSize());
        response.setTotalElements(categoryPage.getTotalElements());
        response.setTotalPages(categoryPage.getTotalPages());
        response.setLastPage(categoryPage.isLast());
        return response;
    }


    @Transactional
    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = categoryMapper.toCategory(categoryDTO);
        Category categoryFromDb = categoryRepository.findByCategoryName(category.getCategoryName());

        if (categoryFromDb != null) {
            throw new APIException(category.getCategoryName() + " already exists!");
        }

        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toCategoryDTO(savedCategory);
    }

    @Transactional
    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category", "categoryId", categoryId));

        categoryRepository.deleteById(categoryId);
        return categoryMapper.toCategoryDTO(category);
    }

    @Transactional
    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
        Category categoryDB = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category", "categoryId", categoryId));

        Category category = categoryMapper.toCategory(categoryDTO);
        category.setId(categoryId);
        categoryDB = categoryRepository.save(category);

        return categoryMapper.toCategoryDTO(categoryDB);
    }
}
