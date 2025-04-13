package com.assignment.ecommerce_rookie.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
public class RatingId implements Serializable {
    private Long userId;
    private Long productId;
}
