package com.assignment.ecommerce_rookie.dto;

import com.assignment.ecommerce_rookie.model.ProductImage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long productId;
    private String productName;
    private List<ProductImage> images;
    private String brand;
    private String description;
    private int quantity;
    private double price;
    private double discount;
    private double specialPrice;
    private boolean featured;
    private LocalDateTime createdOn;
    private LocalDateTime lastUpdatedOn;

}
