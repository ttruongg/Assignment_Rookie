package com.assignment.ecommerce_rookie.controller;

import com.assignment.ecommerce_rookie.dto.ProductDTO;
import com.assignment.ecommerce_rookie.service.IProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ProductController {
    @Autowired
    IProductService productService;


    @PostMapping("/admin/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(
            @RequestBody @Valid ProductDTO productDTO,
            @PathVariable(name = "categoryId") Long categoryId) {

        ProductDTO savedProduct = productService.addProduct(productDTO, categoryId);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

}
