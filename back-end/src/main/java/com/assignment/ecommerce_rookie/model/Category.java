package com.assignment.ecommerce_rookie.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "category")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @NotBlank
    @Size(min = 5, message = "Category name must contain at least 5 characters")
    private String categoryName;

    @NotBlank
    @Size(min = 5, message = "Description must be at least 5 characters")
    private String description;

    @OneToMany(mappedBy = "category", cascade = {CascadeType.ALL})
    private List<Product> products;


}
