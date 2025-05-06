package com.assignment.ecommerce_rookie.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private Long id;
    private String categoryName;
    private String description;

}
