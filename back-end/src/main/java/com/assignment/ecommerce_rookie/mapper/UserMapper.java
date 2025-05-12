package com.assignment.ecommerce_rookie.mapper;

import com.assignment.ecommerce_rookie.dto.UserDTO;
import com.assignment.ecommerce_rookie.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(
            target = "roles",
            expression = "java(user.getRoles().stream().map(role -> role.getRoleName().name()).collect(java.util.stream.Collectors.toSet()))"
    )
    UserDTO toDto(User user);

    List<UserDTO> toDtoList(List<User> users);
}
