package com.assignment.ecommerce_rookie.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class RatingId implements Serializable {
    @Column(name="user_id")
    private Long userId;

    @Column(name = "product_id")
    private Long productId;
}
