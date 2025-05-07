package com.assignment.ecommerce_rookie.service.impl;

import com.assignment.ecommerce_rookie.exception.APIException;
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
import com.assignment.ecommerce_rookie.service.IAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements IAuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;

    @Value("${jwt.RefreshTokenCookieName}")
    private String refreshTokenCookie;

    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtUtils jwtUtils, UserDetailsService userDetailsService,
                           UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
    }

    @Override
    public UserInfoResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return new UserInfoResponse(
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public UserInfoResponse refreshAccessToken(String refreshToken) {
        return Optional.ofNullable(refreshToken)
                .filter(jwtUtils::validateJwtToken)
                .map(jwtUtils::getUserNameFromJwtToken)
                .map(userDetailsService::loadUserByUsername)
                .map(user -> {
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
                    );
                    return (UserDetailsImpl) user;
                })
                .map(user -> new UserInfoResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList())
                ))
                .orElseThrow(() -> new APIException("Invalid or missing refresh token", HttpStatus.UNAUTHORIZED));
    }

    @Transactional
    @Override
    public void signup(SignUpRequest signUpRequest) {
        if (userRepository.existsByUserName(signUpRequest.getUserName())) {
            throw new APIException("Username is already taken");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new APIException("Email is already in use");
        }

//        AppRole roleToFind = Optional.ofNullable(signUpRequest.getRole())
//                .map(role -> AppRole.valueOf(role))
//                .orElse(AppRole.USER);
//
//        Role userRole = roleRepository.findByRoleName(roleToFind)
//                .orElseThrow(() -> new APIException("Role not found: " + roleToFind, HttpStatus.NOT_FOUND));

        Set<String> roleNamesFromRequest = signUpRequest.getRole();
        Set<Role> userRoles = new HashSet<>();


        if (roleNamesFromRequest == null || roleNamesFromRequest.isEmpty()) {
            Role userRole = roleRepository.findByRoleName(AppRole.USER)
                    .orElseThrow(() -> new APIException("Error: Default USER Role not found in DB.", HttpStatus.INTERNAL_SERVER_ERROR));
            userRoles.add(userRole);
        } else {
            roleNamesFromRequest.forEach(roleName -> {

                AppRole roleToFind = AppRole.valueOf(roleName);

                Role role = roleRepository.findByRoleName(roleToFind)
                        .orElseThrow(() -> new APIException("Role not found: " + roleName, HttpStatus.NOT_FOUND));

                userRoles.add(role);
            });

        }


        User user = new User(
                signUpRequest.getUserName(),
                signUpRequest.getEmail(),
                signUpRequest.getFirstName(),
                signUpRequest.getLastName(),
                signUpRequest.getPhoneNumber(),
                encoder.encode(signUpRequest.getPassword()),
                userRoles
        );

        userRepository.save(user);
    }

    @Override
    public void logout() {
        SecurityContextHolder.clearContext();
    }


}
