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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.assignment.ecommerce_rookie.constants.AppConstants.DISCOUNT_PERCENT_DIVISOR;

@Service
public class ProductServiceImpl implements IProductService {

    private final ProductRepository productRepository;


    private final CategoryRepository categoryRepository;


    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
    }

    @Override
    public ProductResponse getAllProducts(int pageNumber, int pageSize, String sortBy, String sortOrder, String keyword, String category, Boolean featured) {
        Pageable pageable = createPageable(pageNumber, pageSize, sortBy, sortOrder);
        Specification<Product> specification = createProductSpecification(keyword, category, featured);

        Page<Product> productsPage = productRepository.findAll(specification, pageable);
        List<ProductDTO> productDTOs = convertToProductDTOs(productsPage.getContent());

        return buildProductResponse(productsPage, productDTOs);

    }

    @Override
    public ProductDTO getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product", "productId", productId));
        return productMapper.toProductDTO(product);
    }

    private Pageable createPageable(int pageNumber, int pageSize, String sortBy, String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return PageRequest.of(pageNumber, pageSize, sort);
    }

    private Specification<Product> createProductSpecification(String keyword, String category, Boolean featured) {
        return Specification.where(fetchCategories())
                .and(applyKeywordFilter(keyword))
                .and(applyCategoryFilter(category))
                .and(applyFeaturedFilter(featured));
    }

    private Specification<Product> fetchCategories() {
        return (root, query, cb) -> {
            root.fetch("categories", JoinType.LEFT);
            query.distinct(true);
            return cb.conjunction();
        };
    }

    private Specification<Product> applyKeywordFilter(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return null;
        }
        return (root, query, cb) -> cb.like(cb.lower(root.get("productName")), "%" + keyword.toLowerCase() + "%");
    }

    private Specification<Product> applyFeaturedFilter(Boolean featured) {
        if (featured == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("featured"), featured);
    }


    private List<ProductDTO> convertToProductDTOs(List<Product> products) {
        return products.stream()
                .map(productMapper::toProductDTO)
                .toList();
    }

    private Specification<Product> applyCategoryFilter(String category) {
        if (category == null || category.isEmpty()) {
            return null;
        }
        return (root, query, cb) -> {
            Join<Product, Category> categoriesJoin = root.join("categories", JoinType.LEFT);
            return cb.like(cb.lower(categoriesJoin.get("categoryName")), "%" + category.toLowerCase() + "%");
        };
    }

    private ProductResponse buildProductResponse(Page<Product> productsPage, List<ProductDTO> productDTOs) {
        ProductResponse response = new ProductResponse();
        response.setProducts(productDTOs);
        response.setPageNumber(productsPage.getNumber());
        response.setPageSize(productsPage.getSize());
        response.setTotalElements(productsPage.getTotalElements());
        response.setLastPage(productsPage.isLast());
        response.setTotalPages(productsPage.getTotalPages());
        return response;
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

        List<ProductImage> images = mapProductImages(productDTO.getImages(), product);

        product.setImages(images);

        BigDecimal specialPrice = calculateSpecialPrice(product.getPrice(), product.getDiscount());
        product.setSpecialPrice(specialPrice);

        Product savedProduct = productRepository.save(product);
        return productMapper.toProductDTO(savedProduct);

    }

    private List<ProductImage> mapProductImages(List<ProductImage> images, Product product) {
        return images.stream()
                .map(dto -> {
                    ProductImage img = new ProductImage();
                    img.setImageUrl(dto.getImageUrl());
                    img.setProduct(product);
                    return img;
                })
                .collect(Collectors.toList());
    }

    private BigDecimal calculateSpecialPrice(BigDecimal price, double discountPercentage) {
        BigDecimal priceBD = price;
        BigDecimal discountBD = BigDecimal.valueOf(discountPercentage).divide(DISCOUNT_PERCENT_DIVISOR, 2, RoundingMode.HALF_UP);
        return priceBD.subtract(priceBD.multiply(discountBD)).setScale(2, RoundingMode.HALF_UP);
    }

    @Transactional
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

    @Transactional
    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product", "productId", productId));

        productRepository.delete(product);

        return productMapper.toProductDTO(product);
    }




}
