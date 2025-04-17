package com.assignment.ecommerce_rookie.service.impl;

import com.assignment.ecommerce_rookie.dto.ProductDTO;
import com.assignment.ecommerce_rookie.dto.ProductResponse;
import com.assignment.ecommerce_rookie.exception.NotFoundException;
import com.assignment.ecommerce_rookie.mapper.ProductMapper;
import com.assignment.ecommerce_rookie.model.Category;
import com.assignment.ecommerce_rookie.model.Product;
import com.assignment.ecommerce_rookie.model.ProductImage;
import com.assignment.ecommerce_rookie.repository.CategoryRepository;
import com.assignment.ecommerce_rookie.repository.ProductRepository;
import com.assignment.ecommerce_rookie.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService implements IProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public ProductDTO getAllProducts(int pageNumber, int pageSize, String sortBy, String sortOrder, String keyword, String category) {
        return null;
    }

    @Override
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId) {
        Product product = productMapper.toProduct(productDTO);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("category", "categoryId", categoryId));

        product.setCategory(category);

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
        return null;
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        return null;
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId, int pageNumber, int pageSize, String sortBy, String sortOrder) {
        return null;
    }

    @Override
    public ProductResponse searchByKeyWord(String keyword, int pageNumber, int pageSize, String sortBy, String sortOrder) {
        return null;
    }


}
