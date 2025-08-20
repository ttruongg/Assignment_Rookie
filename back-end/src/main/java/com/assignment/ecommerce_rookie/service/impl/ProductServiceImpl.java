package com.assignment.ecommerce_rookie.service.impl;

import com.assignment.ecommerce_rookie.dto.request.ProductDTO;
import com.assignment.ecommerce_rookie.dto.response.ProductResponse;
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
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class ProductServiceImpl implements IProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductDTO getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product", "productId", productId));
        return productMapper.toProductDTO(product);
    }

    @Override
    public ProductResponse getAllProducts(int pageNumber, int pageSize, String sortBy, String sortOrder, String keyword, String category, Boolean featured) {
        Pageable pageable = createPageable(pageNumber, pageSize, sortBy, sortOrder);
        Specification<Product> specification = createProductSpecification(keyword, category, featured);

        Page<Product> productsPage = productRepository.findAll(specification, pageable);
        List<ProductDTO> productDTOs = convertToProductDTOs(productsPage.getContent());

        return productMapper.initProductResponse(productsPage, productDTOs);

    }

    private Pageable createPageable(int pageNumber, int pageSize, String sortBy, String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return PageRequest.of(pageNumber, pageSize, sort);
    }

    private Specification<Product> createProductSpecification(String keyword, String category, Boolean featured) {
        return Specification.where(applyKeywordFilter(keyword))
                .and(applyCategoryFilter(category))
                .and(applyFeaturedFilter(featured));
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

    @Override
    @Transactional
    public ProductDTO addProduct(ProductDTO productDTO) {

        validateCategoryIds(productDTO.getCategoryIds());
        Set<Category> categories = fetchCategories(productDTO.getCategoryIds());
        validateUniqueProduct(categories, productDTO.getProductName());
        Product product = buildProduct(productDTO, categories);
        Product savedProduct = productRepository.save(product);

        return productMapper.toProductDTO(savedProduct);

    }

    private void validateCategoryIds(Set<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            throw new APIException("Product must belong to at least one category");
        }
    }

    private Set<Category> fetchCategories(Set<Long> categoryIds) {
        Set<Category> categories = new HashSet<>(categoryRepository.findAllById(categoryIds));

        if (categories.isEmpty()) {
            throw new NotFoundException("category", "ids", categoryIds.toString());
        }
        return categories;
    }

    private void validateUniqueProduct(Set<Category> categories, String productName) {
        boolean exists = categories.stream()
                .flatMap(c -> c.getProducts().stream())
                .anyMatch(p -> p.getProductName().equalsIgnoreCase(productName));

        if (exists) {
            throw new APIException("Product already exists in selected categories");
        }

    }

    private Product buildProduct(ProductDTO productDTO, Set<Category> categories) {
        Product product = productMapper.toProduct(productDTO);
        product.setCategories(categories);
        product.setImages(mapProductImages(productDTO.getImages(), product));
        product.setSpecialPrice(calculateSpecialPrice(product.getPrice(), product.getDiscount()));
        product.setActive(true);
        return product;
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

        productMapper.updateProductFromDto(productDTO, productDB);

        if (productDTO.getCategoryIds() != null && !productDTO.getCategoryIds().isEmpty()) {
            Set<Category> updatedCategories = new HashSet<>(categoryRepository.findAllById(productDTO.getCategoryIds()));
            productDB.setCategories(updatedCategories);
        }

        Product savedProduct = productRepository.save(productDB);
        return productMapper.toProductDTO(savedProduct);
    }
    private void updateProductsFields(Product product, ProductDTO productDTO) {
        product.setProductName(productDTO.getProductName());
        product.setDescription(productDTO.getDescription());
        product.setQuantity(productDTO.getQuantity());
        product.setDiscount(productDTO.getDiscount());
        product.setPrice(productDTO.getPrice());
        product.setSpecialPrice(calculateSpecialPrice(product.getPrice(), product.getDiscount()));
        product.setBrand(productDTO.getBrand());
        product.setFeatured(productDTO.isFeatured());
    }

    @Transactional
    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product", "productId", productId));

        productRepository.delete(product);
        return productMapper.toProductDTO(product);
    }

    @Override
    public ProductDTO activateProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product", "productId", productId));

        product.setActive(!product.isActive());
        productRepository.save(product);
        return productMapper.toProductDTO(product);
    }

}