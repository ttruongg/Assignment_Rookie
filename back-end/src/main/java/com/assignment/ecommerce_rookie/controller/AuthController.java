package com.assignment.ecommerce_rookie.controller;

import com.assignment.ecommerce_rookie.security.request.LoginRequest;
import com.assignment.ecommerce_rookie.security.request.SignUpRequest;
import com.assignment.ecommerce_rookie.service.impl.AuthServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthServiceImpl authServiceImpl;

    @PostMapping("/signin")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        return authServiceImpl.login(loginRequest);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request) {
        return authServiceImpl.refreshAccessToken(request);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        return authServiceImpl.signup(signUpRequest);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return authServiceImpl.logout();
    }


}
