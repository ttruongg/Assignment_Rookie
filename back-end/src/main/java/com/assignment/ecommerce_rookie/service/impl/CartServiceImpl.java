package com.assignment.ecommerce_rookie.service.impl;

import com.assignment.ecommerce_rookie.dto.request.ProductDTO;
import com.assignment.ecommerce_rookie.exception.APIException;
import com.assignment.ecommerce_rookie.exception.NotFoundException;
import com.assignment.ecommerce_rookie.mapper.CartMapper;
import com.assignment.ecommerce_rookie.mapper.ProductMapper;
import com.assignment.ecommerce_rookie.model.Cart;
import com.assignment.ecommerce_rookie.model.CartItem;
import com.assignment.ecommerce_rookie.model.Product;
import com.assignment.ecommerce_rookie.repository.CartItemRepository;
import com.assignment.ecommerce_rookie.repository.ProductRepository;
import com.assignment.ecommerce_rookie.util.AuthUtil;
import com.assignment.ecommerce_rookie.dto.request.CartDTO;
import com.assignment.ecommerce_rookie.repository.CartRepository;
import com.assignment.ecommerce_rookie.service.ICartService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class CartServiceImpl implements ICartService {

    private CartRepository cartRepository;
    private AuthUtil authUtil;
    private ProductRepository productRepository;
    private CartItemRepository cartItemRepository;
    private CartMapper cartMapper;
    private ProductMapper productMapper;

    @Override
    public CartDTO addProductToCart(Long productId, int quantity) {

        Cart cart = createCart();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("product", "productId", productId));

        validateCartItemNotExist(cart, product);
        validateStock(product, quantity);
        initializeCartItems(product, quantity, cart);
        updateProductStock(product, quantity);
        updateCartTotal(cart, product, quantity);

        cartRepository.save(cart);

        CartDTO cartDTO = cartMapper.toCartDTO(cart);

        List<CartItem> cartItems = cart.getCartItems();
        List<ProductDTO> products = mapCatItemsToProducts(cartItems);
        cartDTO.setProducts(products);

        return cartDTO;
    }

    private Cart createCart() {
        Optional<Cart> userCart = cartRepository.findByUserId(authUtil.loggedInUserId());

        if (userCart.isPresent()) {
            return userCart.get();
        }

        Cart cart = new Cart();
        cart.setTotalPrice(BigDecimal.ZERO);
        cart.setUser(authUtil.loggedInUser());
        return cart;

    }

    private void validateCartItemNotExist(Cart cart, Product product) {
        cartItemRepository.findByProductIdAndCartId(product.getId(), cart.getId())
                .ifPresent(item -> {
                    throw new APIException("Product " + product.getProductName() + " already exists in the cart");
                });
    }

    private void validateStock(Product product, int quantity) {
        if (product.getQuantity() == 0) {
            throw new APIException("Product " + product.getProductName() + " is out of stock");
        }

        if (quantity > product.getQuantity()) {
            throw new APIException("Please, make an order of the " + product.getProductName() + " less than or equal to the quantity " + product.getQuantity() + " in stock");
        }
    }

    private void initializeCartItems(Product product, int quantity, Cart cart) {
        CartItem newCartItem = new CartItem();

        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getPrice());

        cart.getCartItems().add(newCartItem);
    }

    private void updateProductStock(Product product, int quantity) {
        product.setQuantity(product.getQuantity() - quantity);
    }

    private void updateCartTotal(Cart cart, Product product, int quantity) {
        cart.setTotalPrice(cart.getTotalPrice()
                .add(product.getSpecialPrice().multiply(BigDecimal.valueOf(quantity))));
    }

    private List<ProductDTO> mapCatItemsToProducts(List<CartItem> cartItems) {
        return cartItems.stream().map(item -> {
            ProductDTO map = productMapper.toProductDTO(item.getProduct());
            map.setQuantity(item.getQuantity());
            return map;
        }).toList();
    }

}
