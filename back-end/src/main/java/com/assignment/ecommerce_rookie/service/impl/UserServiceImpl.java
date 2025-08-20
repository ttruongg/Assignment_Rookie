package com.assignment.ecommerce_rookie.service.impl;

import com.assignment.ecommerce_rookie.dto.request.UserDTO;
import com.assignment.ecommerce_rookie.mapper.UserMapper;
import com.assignment.ecommerce_rookie.repository.UserRepository;
import com.assignment.ecommerce_rookie.service.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDTO> getAllUsers() {
        return userMapper.toDtoList(userRepository.findAll());
    }
}
