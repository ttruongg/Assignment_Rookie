package com.assignment.ecommerce_rookie.dto.response;

import com.assignment.ecommerce_rookie.dto.request.ProductDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    List<ProductDTO> products;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean lastPage;
}
