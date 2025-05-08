package com.assignment.ecommerce_rookie.dto;

import com.assignment.ecommerce_rookie.model.ProductImage;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;
    private String productName;
    private List<ProductImage> images;
    private String brand;
    private String description;
    private int quantity;
    private BigDecimal price;
    private double discount;
    private BigDecimal specialPrice;
    private boolean featured;
    private boolean active;
    private LocalDateTime createdOn;
    private LocalDateTime lastUpdatedOn;
    private Set<Long> categoryIds = new HashSet<>();

}
