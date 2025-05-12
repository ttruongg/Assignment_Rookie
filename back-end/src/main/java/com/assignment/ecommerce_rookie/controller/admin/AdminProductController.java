package com.assignment.ecommerce_rookie.controller.admin;

import com.assignment.ecommerce_rookie.dto.ProductDTO;
import com.assignment.ecommerce_rookie.service.IProductService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/products")
public class AdminProductController {

    private final IProductService productService;

    public AdminProductController(IProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductDTO> addProduct(@RequestBody @Valid ProductDTO productDTO) {

        ProductDTO savedProduct = productService.addProduct(productDTO);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }


    @PutMapping("/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long productId, @RequestBody ProductDTO productDTO) {
        ProductDTO updatedProduct = productService.updateProduct(productDTO, productId);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId) {
        ProductDTO productDTO = productService.deleteProduct(productId);
        return new ResponseEntity<>(productDTO, HttpStatus.OK);
    }

    @PatchMapping("/{productId}")
    @Transactional
    public ResponseEntity<ProductDTO> activateProduct(@PathVariable Long productId) {
        ProductDTO productDTO = productService.activateProduct(productId);
        return new ResponseEntity<>(productDTO, HttpStatus.OK);
    }
}
