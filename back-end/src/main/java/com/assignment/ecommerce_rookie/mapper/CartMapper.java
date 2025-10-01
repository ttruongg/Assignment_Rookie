package com.assignment.ecommerce_rookie.mapper;

import com.assignment.ecommerce_rookie.dto.request.CartDTO;
import com.assignment.ecommerce_rookie.model.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "products", source = "cartItems")
    CartDTO toCartDTO(Cart cart);

}
