package com.assignment.ecommerce_rookie.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductRatingResponse {
    private Long userId;
    private Long productId;
    private int rating;
    private String comment;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private String firstName;
    private String lastName;

}
