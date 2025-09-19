package com.assignment.ecommerce_rookie.controller.web;

import com.assignment.ecommerce_rookie.dto.request.UserDTO;
import com.assignment.ecommerce_rookie.dto.response.RefreshAccessTokenResponse;
import com.assignment.ecommerce_rookie.security.jwt.JwtUtils;
import com.assignment.ecommerce_rookie.dto.request.LoginRequest;
import com.assignment.ecommerce_rookie.dto.request.SignUpRequest;
import com.assignment.ecommerce_rookie.security.response.MessageResponse;
import com.assignment.ecommerce_rookie.dto.response.LoginResponse;
import com.assignment.ecommerce_rookie.security.services.UserDetailsImpl;
import com.assignment.ecommerce_rookie.service.IAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final IAuthService authService;
    private final JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.login(loginRequest);

        ResponseCookie refreshTokenCookie = jwtUtils.generateRefreshTokenCookie(
                (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(response);
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<RefreshAccessTokenResponse> refreshAccessToken(HttpServletRequest request) {
        RefreshAccessTokenResponse accessToken = authService.refreshAccessToken(request);
        return ResponseEntity.ok().body(accessToken);
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        UserDTO savedUser = authService.signup(signUpRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedUser);
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout() {
        authService.logout();
        jwtUtils.cleanRefreshTokenCookie();
        return ResponseEntity.ok().body(new MessageResponse("User logged out successfully!"));
    }
}
