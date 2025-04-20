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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public ProductResponse getAllProducts(int pageNumber, int pageSize, String sortBy, String sortOrder) {
        Sort sortByAndSortOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable page = PageRequest.of(pageNumber, pageSize, sortByAndSortOrder);
        Page<Product> pageProducts = productRepository.findAll(page);

        List<Product> products = pageProducts.getContent();


        List<ProductDTO> productDTOList = products.stream()
                .map(product -> productMapper.toProductDTO(product))
                .toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setProducts(productDTOList);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setLastPage(pageProducts.isLast());
        productResponse.setTotalPages(pageProducts.getTotalPages());

        return productResponse;
    }

    @Override
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("category", "categoryId", categoryId));


        boolean isNotPresent = true;
        List<Product> products = category.getProducts();
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getProductName().equalsIgnoreCase(productDTO.getProductName())) {
                isNotPresent = false;
            }
        }

        if (isNotPresent) {

            Product product = productMapper.toProduct(productDTO);
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
        } else {
            throw new APIException("Product already exists");
        }
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

    @Override
    public ProductResponse searchByCategory(Long categoryId, int pageNumber, int pageSize, String sortBy, String sortOrder) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("category", "categoryId", categoryId));

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable page = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> pageProducts = productRepository.findByCategoryOrderByPriceAsc(category, page);

        List<Product> products = pageProducts.getContent();

        if (products.isEmpty()) {
            throw new APIException(category.getCategoryName() + ", there are no products in this category");
        }


        List<ProductDTO> productDTOList = products.stream().map(product -> productMapper.toProductDTO(product)).toList();


        ProductResponse response = new ProductResponse();
        response.setProducts(productDTOList);
        response.setPageNumber(pageProducts.getNumber());
        response.setPageSize(pageProducts.getSize());
        response.setTotalElements(pageProducts.getTotalElements());
        response.setLastPage(pageProducts.isLast());
        response.setTotalPages(pageProducts.getTotalPages());


        return response;
    }

    @Override
    public ProductResponse searchByKeyWord(String keyword, int pageNumber, int pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable page = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> pageProducts = productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%', page);


        List<Product> products = pageProducts.getContent();
        List<ProductDTO> productDTOList = products.stream()
                .map(product -> productMapper.toProductDTO(product))
                .toList();

        if (products.isEmpty()) {
            throw new APIException("Products not found with " + keyword);
        }

        ProductResponse response = new ProductResponse();
        response.setProducts(productDTOList);
        response.setProducts(productDTOList);
        response.setPageNumber(pageProducts.getNumber());
        response.setPageSize(pageProducts.getSize());
        response.setTotalElements(pageProducts.getTotalElements());
        response.setLastPage(pageProducts.isLast());
        response.setTotalPages(pageProducts.getTotalPages());


        return response;
    }


}
