package com.assignment.ecommerce_rookie.service.impl;

import com.assignment.ecommerce_rookie.dto.ProductDTO;
import com.assignment.ecommerce_rookie.dto.ProductResponse;
import com.assignment.ecommerce_rookie.exception.APIException;
import com.assignment.ecommerce_rookie.exception.NotFoundException;
import com.assignment.ecommerce_rookie.mapper.ProductMapper;
import com.assignment.ecommerce_rookie.model.Category;
import com.assignment.ecommerce_rookie.model.Product;
import com.assignment.ecommerce_rookie.model.ProductImage;
import com.assignment.ecommerce_rookie.repository.CategoryRepository;
import com.assignment.ecommerce_rookie.repository.ProductRepository;
import com.assignment.ecommerce_rookie.service.IProductService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements IProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public ProductResponse getAllProducts(int pageNumber, int pageSize, String sortBy, String sortOrder, String keyword, String category) {
        Sort sortByAndSortOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable page = PageRequest.of(pageNumber, pageSize, sortByAndSortOrder);

        Specification<Product> spec = Specification.where((root, query, criteriaBuilder) -> {
            root.fetch("categories", JoinType.LEFT);
            query.distinct(true);
            return criteriaBuilder.conjunction();
        });


        if (keyword != null && !keyword.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("productName")), "%" + keyword.toLowerCase() + "%")
            );

        }


        if (category != null && !category.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                Join<Product, Category> categoriesJoin = root.join("categories", JoinType.LEFT);
                return criteriaBuilder.like(criteriaBuilder.lower(categoriesJoin.get("categoryName")), "%" + category.toLowerCase() + "%");
            });

        

        Page<Product> pageProducts = productRepository.findAll(spec, page);

        List<Product> products = pageProducts.getContent();


        List<ProductDTO> productDTOS = products.stream()
                .map(product -> productMapper.toProductDTO(product))
                .toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setProducts(productDTOS);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setLastPage(pageProducts.isLast());
        productResponse.setTotalPages(pageProducts.getTotalPages());

        return productResponse;
    }

    @Override
    @Transactional
    public ProductDTO addProduct(ProductDTO productDTO) {

        Set<Long> categoryIds = productDTO.getCategoryIds();
        if (categoryIds == null || categoryIds.isEmpty()) {
            throw new APIException("Product must belong to at least one category");
        }


        Set<Category> categories = new HashSet<>(categoryRepository.findAllById(categoryIds));

        if (categories.isEmpty()) {
            throw new NotFoundException("category", "ids", categoryIds.toString());
        }


        boolean exists = categories.stream()
                .flatMap(c -> c.getProducts().stream())
                .anyMatch(p -> p.getProductName().equalsIgnoreCase(productDTO.getProductName()));

        if (exists) {
            throw new APIException("Product already exists in selected categories");
        }


        Product product = productMapper.toProduct(productDTO);
        product.setCategories(categories);

        List<ProductImage> images = productDTO.getImages().stream().map(url -> {
            ProductImage img = new ProductImage();
            img.setImageUrl(url.getImageUrl());
            img.setProduct(product);
            return img;
        }).collect(Collectors.toList());

        product.setImages(images);

        double specialPrice = product.getPrice() - (product.getDiscount() * 0.01) * product.getPrice();
        product.setSpecialPrice(specialPrice);

        Product savedProduct = productRepository.save(product);
        return productMapper.toProductDTO(savedProduct);


    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO, Long productId) {
        Product productDB = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("product", "productId", productId));

        Product product = productMapper.toProduct(productDTO);

        productDB.setProductName(product.getProductName());
        productDB.setDescription(product.getDescription());
        productDB.setQuantity(product.getQuantity());
        productDB.setDiscount(product.getDiscount());
        productDB.setPrice(product.getPrice());
        productDB.setSpecialPrice(product.getSpecialPrice());
        productDB.setBrand(product.getBrand());
        productDB.setFeatured(product.isFeatured());

        if (productDTO.getCategoryIds() != null && !productDTO.getCategoryIds().isEmpty()) {
            Set<Category> updatedCategories = new HashSet<>(categoryRepository.findAllById(productDTO.getCategoryIds()));
            productDB.setCategories(updatedCategories);
        }


        Product savedProduct = productRepository.save(productDB);
        return productMapper.toProductDTO(savedProduct);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product", "productId", productId));

        productRepository.delete(product);

        return productMapper.toProductDTO(product);
    }




}
