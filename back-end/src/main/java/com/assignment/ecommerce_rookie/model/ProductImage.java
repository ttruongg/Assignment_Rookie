package com.assignment.ecommerce_rookie.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "productImage")
@Data
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "productId")
    private Product product;

}
