package com.assignment.ecommerce_rookie.model;

import jakarta.persistence.*;

@Entity
@Table(name = "productImage")
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "productId")
    private Product product;

}
