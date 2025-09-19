package com.assignment.ecommerce_rookie.service.impl;

import com.assignment.ecommerce_rookie.dto.request.TokenPair;
import com.assignment.ecommerce_rookie.dto.request.LoginRequest;
import com.assignment.ecommerce_rookie.dto.request.SignUpRequest;
import com.assignment.ecommerce_rookie.dto.request.UserDTO;
import com.assignment.ecommerce_rookie.dto.response.LoginResponse;
import com.assignment.ecommerce_rookie.dto.response.RefreshAccessTokenResponse;
import com.assignment.ecommerce_rookie.exception.APIException;
import com.assignment.ecommerce_rookie.exception.BlacklistedTokenException;
import com.assignment.ecommerce_rookie.exception.InvalidToken;
import com.assignment.ecommerce_rookie.exception.TokenMissingException;
import com.assignment.ecommerce_rookie.mapper.UserMapper;
import com.assignment.ecommerce_rookie.model.AppRole;
import com.assignment.ecommerce_rookie.model.Role;
import com.assignment.ecommerce_rookie.model.User;
import com.assignment.ecommerce_rookie.repository.RoleRepository;
import com.assignment.ecommerce_rookie.repository.TokenRepository;
import com.assignment.ecommerce_rookie.repository.UserRepository;
import com.assignment.ecommerce_rookie.security.jwt.JwtUtils;
import com.assignment.ecommerce_rookie.security.services.UserDetailsImpl;
import com.assignment.ecommerce_rookie.security.services.UserDetailsServiceImpl;
import com.assignment.ecommerce_rookie.service.IAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements IAuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final UserMapper userMapper;
    private final TokenRepository tokenRepository;
    private final UserDetailsServiceImpl userDetailsService;
    private static final AppRole DEFAULT_ROLE = AppRole.USER;

    @Value("${jwt.accessExpirationMs}")
    private long jwtExpirationMS;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        TokenPair tokenPair = jwtUtils.generateTokenPair(userDetails);

        tokenRepository.storeToken(
                userDetails.getUsername(),
                tokenPair
        );

        return initLoginResponse(userDetails, tokenPair.getAccessToken());
    }

    private LoginResponse initLoginResponse(UserDetailsImpl userDetails, String accessToken) {
        return LoginResponse.builder()
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .roles(userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                .accessToken(accessToken)
                .build();
    }

    @Transactional
    @Override
    public UserDTO signup(SignUpRequest signUpRequest) {
        validateUniqueUser(signUpRequest);
        Set<Role> userRoles = resolveRoles(signUpRequest.getRole());

        User user = initUser(signUpRequest, userRoles);
        User savedUser = userRepository.save(user);

        return userMapper.toDto(savedUser);
    }

    private void validateUniqueUser(SignUpRequest signUpRequest) {
        if (userRepository.existsByUserName(signUpRequest.getUserName())) {
            throw new APIException("Username is already taken");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new APIException("Email is already in use");
        }
    }

    private Role findRole(AppRole roleName) {
        return roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new APIException(
                        "Role not found: " + roleName, HttpStatus.NOT_FOUND));
    }

    private Set<Role> resolveRoles(Set<String> roleNamesFromRequest) {
        if (roleNamesFromRequest == null || roleNamesFromRequest.isEmpty()) {
            return Set.of(findRole(DEFAULT_ROLE));
        }

        return roleNamesFromRequest.stream()
                .map(AppRole::valueOf)
                .map(this::findRole)
                .collect(Collectors.toSet());
    }

    private User initUser(SignUpRequest signUpRequest, Set<Role> userRoles) {
        return User.builder()
                .userName(signUpRequest.getUserName())
                .email(signUpRequest.getEmail())
                .firstName(signUpRequest.getFirstName())
                .lastName(signUpRequest.getLastName())
                .phoneNumber(signUpRequest.getPhoneNumber())
                .password(encoder.encode(signUpRequest.getPassword()))
                .roles(userRoles)
                .build();
    }

    @Override
    public void logout() {
        var userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        tokenRepository.removeAllTokens(userDetails.getUsername());
    }

    @Override
    public RefreshAccessTokenResponse refreshAccessToken(HttpServletRequest request) {

        String refreshToken = extractTokenFromRequest(request);

        validateToken(refreshToken);

        String username = jwtUtils.getUserNameFromJwtToken(refreshToken);
        verifyStoredRefreshToken(username, refreshToken);

        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(username);
        String newAccessToken = jwtUtils.generateAccessToken(userDetails);

        tokenRepository.removeAccessToken(username);

        long remainingRefreshTimeMs = calculateRemainingTime(refreshToken);

        tokenRepository.storeToken(
                username,
                new TokenPair(newAccessToken, refreshToken, jwtExpirationMS, remainingRefreshTimeMs)
        );

        return new RefreshAccessTokenResponse(newAccessToken);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        return jwtUtils.getRefreshTokenFromCookie(request)
                .orElseThrow(() -> new TokenMissingException("Refresh token is missing"));
    }

    private void validateToken(String refreshToken) {

        if (!jwtUtils.validateJwtToken(refreshToken)) {
            throw new InvalidToken("Refresh token is invalid");
        }

        if (tokenRepository.isRefreshTokenBlacklisted(refreshToken)) {
            throw new BlacklistedTokenException("Refresh token has been blacklisted");
        }
    }

    private void verifyStoredRefreshToken(String username, String refreshToken) {
        String storedRefreshToken = tokenRepository.getRefreshToken(username);

        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new InvalidToken("Refresh token does not match");
        }
    }

    private long calculateRemainingTime(String refreshToken) {
        long expirationTime = jwtUtils.getExpirationDate(refreshToken).getTime();
        long currentTime = System.currentTimeMillis();
        return expirationTime - currentTime;
    }

}
