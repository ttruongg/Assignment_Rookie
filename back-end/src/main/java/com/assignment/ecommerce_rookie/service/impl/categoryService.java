package com.assignment.ecommerce_rookie.service.impl;

import com.assignment.ecommerce_rookie.dto.CategoryDTO;
import com.assignment.ecommerce_rookie.dto.CategoryResponse;
import com.assignment.ecommerce_rookie.exception.APIException;
import com.assignment.ecommerce_rookie.exception.NotFoundException;
import com.assignment.ecommerce_rookie.mapper.CategoryMapper;
import com.assignment.ecommerce_rookie.model.Category;
import com.assignment.ecommerce_rookie.repository.CategoryRepository;
import com.assignment.ecommerce_rookie.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class categoryService implements ICategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public CategoryResponse getAllCategories(int pageNumber, int pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();



//        if (sortBy.equalsIgnoreCase("asc"))
//            sortByAndOrder = Sort.by(sortBy).ascending();
//        else
//            sortByAndOrder = Sort.by(sortBy).descending();


        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageable);

        List<Category> categories = categoryPage.getContent();

        if (categories.isEmpty()) {
            throw new APIException("No category was found!");
        }

        List<CategoryDTO> categoryDTOList = categories.stream()
                .map(category -> (categoryMapper.toCategoryDTO(category)))
                .toList();

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setCategories(categoryDTOList);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setLastPage(categoryPage.isLast());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());


        return categoryResponse;
    }

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

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category", "categoryId", categoryId));

        categoryRepository.deleteById(categoryId);
        return categoryMapper.toCategoryDTO(category);
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
        Category categoryDB = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category", "categoryId", categoryId));

        Category category = categoryMapper.toCategory(categoryDTO);
        category.setCategoryId(categoryId);
        categoryDB = categoryRepository.save(category);

        return categoryMapper.toCategoryDTO(categoryDB);
    }
}
