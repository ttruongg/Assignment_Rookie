package com.assignment.ecommerce_rookie.controller.web;

import com.assignment.ecommerce_rookie.security.jwt.JwtUtils;
import com.assignment.ecommerce_rookie.security.request.LoginRequest;
import com.assignment.ecommerce_rookie.security.request.SignUpRequest;
import com.assignment.ecommerce_rookie.security.response.MessageResponse;
import com.assignment.ecommerce_rookie.security.response.UserInfoResponse;
import com.assignment.ecommerce_rookie.security.services.UserDetailsImpl;
import com.assignment.ecommerce_rookie.service.IAuthService;
import com.assignment.ecommerce_rookie.service.impl.AuthServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final IAuthService authService;
    private final JwtUtils jwtUtils;

    @Value("${jwt.RefreshTokenCookieName}")
    private String refreshTokenCookie;

    public AuthController(IAuthService authService, JwtUtils jwtUtils) {
        this.authService = authService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        UserInfoResponse response = authService.login(loginRequest);
        ResponseCookie accessTokenCookie = jwtUtils.generateAccessTokenCookie(
                (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        ResponseCookie refreshTokenCookie = jwtUtils.generateRefreshTokenCookie(
                (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request) {
        return jwtUtils.getJwtFromCookie(request, refreshTokenCookie)
                .map(refreshToken -> {
                    try {
                        UserInfoResponse response = authService.refreshAccessToken(refreshToken);
                        ResponseCookie accessTokenCookie = jwtUtils.generateAccessTokenCookie(
                                (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
                        return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                                .body(response);
                    } catch (RuntimeException e) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(new MessageResponse(e.getMessage()));
                    }
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new MessageResponse("Refresh token is missing")));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        authService.signup(signUpRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        jwtUtils.cleanAccessTokenCookie();
        jwtUtils.cleanRefreshTokenCookie();
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().body(new MessageResponse("User logged out successfully!"));
    }


}
