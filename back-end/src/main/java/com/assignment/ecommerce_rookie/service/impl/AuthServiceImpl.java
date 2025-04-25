package com.assignment.ecommerce_rookie.service.impl;

import com.assignment.ecommerce_rookie.model.AppRole;
import com.assignment.ecommerce_rookie.model.Role;
import com.assignment.ecommerce_rookie.model.User;
import com.assignment.ecommerce_rookie.repository.RoleRepository;
import com.assignment.ecommerce_rookie.repository.UserRepository;
import com.assignment.ecommerce_rookie.security.jwt.JwtUtils;
import com.assignment.ecommerce_rookie.security.request.LoginRequest;
import com.assignment.ecommerce_rookie.security.request.SignUpRequest;
import com.assignment.ecommerce_rookie.security.response.MessageResponse;
import com.assignment.ecommerce_rookie.security.response.UserInfoResponse;
import com.assignment.ecommerce_rookie.security.services.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Value("${jwt.RefreshTokenCookieName}")
    private String refreshTokenCookie;

    @Autowired
    PasswordEncoder encoder;


    public ResponseEntity<?> login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            ResponseCookie accessTokenCookie = jwtUtils.generateAccessTokenCookie(userDetails);
            ResponseCookie refreshTokenCookie = jwtUtils.generateRefreshTokenCookie(userDetails);

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            UserInfoResponse response = new UserInfoResponse(
                    userDetails.getId(),
                    userDetails.getUsername(),
                    roles
            );

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                    .body(response);

        } catch (AuthenticationException exception) {
            return new ResponseEntity<>(new MessageResponse("Bad credentials"), HttpStatus.UNAUTHORIZED);
        }
    }


    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request) {
        try {

            String refreshToken = jwtUtils.getJwtFromCookie(request, refreshTokenCookie);


            if (refreshToken == null || !jwtUtils.validateJwtToken(refreshToken)) {
                return new ResponseEntity<>(new MessageResponse("Invalid refresh token"), HttpStatus.UNAUTHORIZED);
            }


            String username = jwtUtils.getUserNameFromJwtToken(refreshToken);


            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);


            ResponseCookie accessTokenCookie = jwtUtils.generateAccessTokenCookie((UserDetailsImpl) userDetails);


            return new ResponseEntity<>(new MessageResponse("Access token refreshed"), HttpStatus.OK);

        } catch (Exception exception) {
            return new ResponseEntity<>(new MessageResponse("Failed to refresh token"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> signup(SignUpRequest signUpRequest) {
        if (userRepository.existsByUserName(signUpRequest.getUserName())) {
            return new ResponseEntity<>(new MessageResponse("Username is already taken"), HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity<>(new MessageResponse("Email is already in use"), HttpStatus.BAD_REQUEST);
        }

        String strRole = signUpRequest.getRole();

        Role userRole = new Role();
        if (strRole == null) {
            userRole = roleRepository.findByRoleName(AppRole.USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        } else {
            userRole = roleRepository.findByRoleName(AppRole.ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        }

        User user = new User(
                signUpRequest.getUserName(),
                signUpRequest.getEmail(),
                signUpRequest.getFirstName(),
                signUpRequest.getLastName(),
                signUpRequest.getPhoneNumber(),
                encoder.encode(signUpRequest.getPassword()),
                userRole
        );


        userRepository.save(user);

        return new ResponseEntity<>(new MessageResponse("User registered successfully!"), HttpStatus.CREATED);
    }

    public ResponseEntity<?> logout() {
        ResponseCookie accessTokenCookie = jwtUtils.cleanAccessTokenCookie();
        ResponseCookie refreshTokenCookie = jwtUtils.cleanRefreshTokenCookie();
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(new MessageResponse("User logged out successfully!"));
    }

}
