package com.assignment.ecommerce_rookie.service;

import com.assignment.ecommerce_rookie.dto.request.LoginRequest;
import com.assignment.ecommerce_rookie.dto.request.SignUpRequest;
import com.assignment.ecommerce_rookie.dto.request.UserDTO;
import com.assignment.ecommerce_rookie.dto.response.LoginResponse;
import com.assignment.ecommerce_rookie.dto.response.RefreshAccessTokenResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface IAuthService {
    LoginResponse login(LoginRequest loginRequest);
    UserDTO signup(SignUpRequest signUpRequest);
    void logout();
    RefreshAccessTokenResponse refreshAccessToken(HttpServletRequest request);
}
