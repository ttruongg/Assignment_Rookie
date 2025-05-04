package com.assignment.ecommerce_rookie.service;

import com.assignment.ecommerce_rookie.security.request.LoginRequest;
import com.assignment.ecommerce_rookie.security.request.SignUpRequest;
import com.assignment.ecommerce_rookie.security.response.UserInfoResponse;

public interface IAuthService {
    UserInfoResponse login(LoginRequest loginRequest);
    UserInfoResponse refreshAccessToken(String refreshToken);
    void signup(SignUpRequest signUpRequest);
    void logout();
}
