package com.assignment.ecommerce_rookie.service;

import com.assignment.ecommerce_rookie.security.request.LoginRequest;
import com.assignment.ecommerce_rookie.security.request.SignUpRequest;
import com.assignment.ecommerce_rookie.security.response.LoginResponse;

public interface IAuthService {
    LoginResponse login(LoginRequest loginRequest);
    LoginResponse refreshAccessToken(String refreshToken);
    void signup(SignUpRequest signUpRequest);
    void logout();
}
