package com.assignment.ecommerce_rookie.service;

import com.assignment.ecommerce_rookie.dto.UserDTO;

import java.util.List;

public interface IUserService {
    List<UserDTO> getAllUsers();
}
