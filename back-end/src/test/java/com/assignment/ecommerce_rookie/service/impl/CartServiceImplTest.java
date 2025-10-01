package com.assignment.ecommerce_rookie.service.impl;

import com.assignment.ecommerce_rookie.dto.request.CartDTO;
import com.assignment.ecommerce_rookie.dto.request.ProductDTO;
import com.assignment.ecommerce_rookie.exception.APIException;
import com.assignment.ecommerce_rookie.exception.NotFoundException;
import com.assignment.ecommerce_rookie.mapper.CartMapper;
import com.assignment.ecommerce_rookie.mapper.ProductMapper;
import com.assignment.ecommerce_rookie.model.Cart;
import com.assignment.ecommerce_rookie.model.CartItem;
import com.assignment.ecommerce_rookie.model.Product;
import com.assignment.ecommerce_rookie.model.User;
import com.assignment.ecommerce_rookie.repository.CartItemRepository;
import com.assignment.ecommerce_rookie.repository.CartRepository;
import com.assignment.ecommerce_rookie.repository.ProductRepository;
import com.assignment.ecommerce_rookie.util.AuthUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private AuthUtil authUtil;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CartMapper cartMapper;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private CartServiceImpl cartService;

    Product product;
    Cart cart;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setProductName("Laptop");
        product.setQuantity(10);
        product.setPrice(BigDecimal.valueOf(1000));
        product.setSpecialPrice(BigDecimal.valueOf(900));
        product.setDiscount(0.0);

        cart = new Cart();
        cart.setId(1L);
        cart.setTotalPrice(BigDecimal.ZERO);
        cart.setCartItems(new java.util.ArrayList<>());

    }

    @Test
    void testAddProductToCart_cartNotExisted_createNewCart() {
        when(authUtil.loggedInUserId()).thenReturn(42L);
        when(cartRepository.findByUserId(42L)).thenReturn(Optional.empty());
        when(authUtil.loggedInUser()).thenReturn(new User());
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> {
            Cart saved = inv.getArgument(0);
            saved.setId(100L);
            return saved;
        });

        when(cartMapper.toCartDTO(any(Cart.class))).thenReturn(new CartDTO());
        when(productMapper.toProductDTO(any(Product.class))).thenReturn(new ProductDTO());

        CartDTO result = cartService.addProductToCart(1L, 1);

        assertNotNull(result);
        verify(cartRepository, times(1)).save(any(Cart.class));
        verify(cartItemRepository, never()).save(any(CartItem.class));

    }

    @Test
    void testAddProductToCart_ProductNotFound_ThrowsNotFoundException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> {
            cartService.addProductToCart(1L, 1);
        });

        assertEquals("product not found with productId:1", ex.getMessage());
    }

    @Test
    void testAddProductToCart_CartItemAlreadyExists_ThrowsAPIException() {
        when(authUtil.loggedInUserId()).thenReturn(99L);
        when(cartRepository.findByUserId(99L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        when(cartItemRepository.findByProductIdAndCartId(1L, cart.getId())).thenReturn(Optional.of(new CartItem()));

        APIException ex = assertThrows(APIException.class, () -> {
            cartService.addProductToCart(1L, 1);
        });

        assertEquals("Product Laptop already exists in the cart", ex.getMessage());
    }

    @Test
    void testAddProductToCart_NotEnoughStock_ThrowsAPIException() {
        product.setQuantity(0);
        when(authUtil.loggedInUserId()).thenReturn(42L);
        when(cartRepository.findByUserId(42L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1l)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByProductIdAndCartId(1L, cart.getId())).thenReturn(Optional.empty());

        APIException ex = assertThrows(APIException.class, () -> {
            cartService.addProductToCart(1L, 1);
        });

        assertEquals("Product Laptop is out of stock", ex.getMessage());

    }

    @Test
    void addProductToCart_ExceedAvailableStock_ThrowsAPIException() {

        when(authUtil.loggedInUserId()).thenReturn(42L);
        when(cartRepository.findByUserId(42L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByProductIdAndCartId(1L, cart.getId())).thenReturn(Optional.empty());

        APIException ex = assertThrows(APIException.class, () -> {
            cartService.addProductToCart(1L, 20);
        });

        assertEquals("Please, make an order of the Laptop less than or equal to the quantity 10 in stock", ex.getMessage());
    }

    @Test
    void addProductToCart_Successful() {
        when(authUtil.loggedInUserId()).thenReturn(42L);
        when(cartRepository.findByUserId(42L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByProductIdAndCartId(1L, cart.getId())).thenReturn(Optional.empty());

        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> {
            Cart saved = inv.getArgument(0);
            saved.setId(100L);
            return saved;
        });

        when(cartMapper.toCartDTO(any(Cart.class))).thenReturn(new CartDTO());
        when(productMapper.toProductDTO(any(Product.class))).thenReturn(new ProductDTO());

        CartDTO result = cartService.addProductToCart(1L, 2);

        assertNotNull(result);

        verify(cartRepository, times(1)).findByUserId(42L);
        verify(productRepository, times(1)).findById(1L);
        verify(cartRepository, times(1)).save(any(Cart.class));
        verify(cartItemRepository, never()).save(any(CartItem.class));

        verify(cartMapper, times(1)).toCartDTO(any(Cart.class));
        verify(productMapper, atLeastOnce()).toProductDTO(any(Product.class));
    }

}