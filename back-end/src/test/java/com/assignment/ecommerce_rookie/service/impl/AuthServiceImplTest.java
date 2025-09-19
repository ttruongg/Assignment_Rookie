package com.assignment.ecommerce_rookie.service.impl;

import com.assignment.ecommerce_rookie.dto.request.LoginRequest;
import com.assignment.ecommerce_rookie.dto.request.SignUpRequest;
import com.assignment.ecommerce_rookie.dto.request.TokenPair;
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
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;


    private UserDetailsImpl userDetails;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    private Authentication authentication;
    private TokenPair tokenPair;


    private UserDTO userDTO;
    private SignUpRequest signUpRequest;
    private Role role;
    private User savedUser;

    @Mock
    private HttpServletRequest request;

    private String refreshToken = "valid-refresh-token";
    private String newAccessToken = "new-access-token";


    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        userDetails = new UserDetailsImpl(
                1L,
                "testuser",
                "testuser@example.com",
                "hashedPassword",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        authentication = new UsernamePasswordAuthenticationToken(userDetails, "password", userDetails.getAuthorities());
        tokenPair = new TokenPair("access-token", "refresh-token", 3600000L, 7200000L);

        signUpRequest = new SignUpRequest(
                "username",
                "newuser@example.com",
                "password",
                Set.of("USER"),
                "First",
                "Last",
                "1234567890"
        );

        role = new Role(1, AppRole.USER);

        savedUser = User.builder()
                .id(100L)
                .userName("username")
                .email("newuser@example.com")
                .firstName("First")
                .lastName("Last")
                .phoneNumber("1234567890")
                .password("hashedPassword")
                .roles(Set.of(role))
                .build();

        userDTO = new UserDTO();
        userDTO.setId(100L);
        userDTO.setUserName("username");
        userDTO.setEmail("newuser@example.com");
        userDTO.setFirstName("First");
        userDTO.setLastName("Last");
        userDTO.setPhoneNumber("1234567890");
        userDTO.setRoles(Set.of("ROLE_USER"));

    }

    @Test
    void login_successful() {

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtils.generateTokenPair(userDetails)).thenReturn(tokenPair);

        LoginResponse response = authService.login(new LoginRequest("testuser", "password"));

        assertNotNull(response);
        assertEquals(userDetails.getId(), response.getId());
        assertEquals(userDetails.getUsername(), response.getUsername());
        assertTrue(response.getRoles().contains("ROLE_USER"));
        assertEquals(tokenPair.getAccessToken(), response.getAccessToken());

        verify(authenticationManager).authenticate(any());
        verify(jwtUtils).generateTokenPair(userDetails);
        verify(tokenRepository).storeToken(userDetails.getUsername(), tokenPair);
    }

    @Test
    void login_InvalidCredentials_ShouldThrow() {
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Invalid"));

        LoginRequest loginRequest = new LoginRequest("wronguser", "wrongpass");
        assertThrows(BadCredentialsException.class, () ->
                authService.login(loginRequest)
        );

        verify(jwtUtils, never()).generateTokenPair(any());
        verify(tokenRepository, never()).storeToken(any(), any());
    }

    @Test
    void signup_success_withDefaultRole() {
        when(userRepository.existsByUserName(signUpRequest.getUserName())).thenReturn(false);
        when(userRepository.existsByEmail(signUpRequest.getEmail())).thenReturn(false);
        when(roleRepository.findByRoleName(AppRole.USER)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(signUpRequest.getPassword())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(userDTO);

        UserDTO result = authService.signup(signUpRequest);

        assertNotNull(result);
        assertEquals(userDTO.getId(), result.getId());
        assertEquals(userDTO.getUserName(), result.getUserName());
        assertEquals(userDTO.getEmail(), result.getEmail());
        assertTrue(result.getRoles().contains("ROLE_USER"));
        verify(userRepository).existsByUserName(signUpRequest.getUserName());
        verify(userRepository).existsByEmail(signUpRequest.getEmail());
        verify(roleRepository).findByRoleName(AppRole.USER);
        verify(passwordEncoder).encode(signUpRequest.getPassword());
        verify(userRepository).save(any(User.class));
        verify(userMapper).toDto(savedUser);


    }

    @Test
    void signup_UsernameExists_ShouldThrow() {
        when(userRepository.existsByUserName(signUpRequest.getUserName())).thenReturn(true);

        APIException ex = assertThrows(APIException.class, () ->
                authService.signup(signUpRequest)
        );
        assertEquals("Username is already taken", ex.getMessage());
        verify(userRepository).existsByUserName(signUpRequest.getUserName());
        verify(userRepository, never()).existsByEmail(any());

    }

    @Test
    void signUp_EmailExists_ShouldThrow() {
        when(userRepository.existsByUserName(signUpRequest.getUserName())).thenReturn(false);
        when(userRepository.existsByEmail(signUpRequest.getEmail())).thenReturn(true);

        APIException ex = assertThrows(APIException.class, () -> authService.signup(signUpRequest));

        assertEquals("Email is already in use", ex.getMessage());
        verify(userRepository).existsByUserName(signUpRequest.getUserName());
        verify(userRepository).existsByEmail(signUpRequest.getEmail());
        verify(roleRepository, never()).findByRoleName(any());
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void refreshAccessToken_success() {
        when(jwtUtils.getRefreshTokenFromCookie(request)).thenReturn(Optional.of(refreshToken));
        when(jwtUtils.validateJwtToken(refreshToken)).thenReturn(true);
        when(tokenRepository.isRefreshTokenBlacklisted(refreshToken)).thenReturn(false);
        when(jwtUtils.getUserNameFromJwtToken(refreshToken)).thenReturn("testuser");
        when(tokenRepository.getRefreshToken("testuser")).thenReturn(refreshToken);
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtUtils.generateAccessToken(userDetails)).thenReturn(newAccessToken);
        when(jwtUtils.getExpirationDate(refreshToken)).thenReturn(new Date(System.currentTimeMillis() + 60000));

        RefreshAccessTokenResponse response = authService.refreshAccessToken(request);

        assertNotNull(response);
        assertEquals(newAccessToken, response.getAccessToken());

        verify(jwtUtils).getRefreshTokenFromCookie(request);
        verify(jwtUtils).validateJwtToken(refreshToken);
        verify(tokenRepository).isRefreshTokenBlacklisted(refreshToken);
        verify(tokenRepository).getRefreshToken("testuser");
        verify(tokenRepository).removeAccessToken("testuser");
        verify(tokenRepository).storeToken(eq("testuser"), any(TokenPair.class));


    }

    @Test
    void refreshToken_missingToken_ShouldThrow() {
        when(jwtUtils.getRefreshTokenFromCookie(request)).thenReturn(Optional.empty());

        TokenMissingException ex = assertThrows(TokenMissingException.class, () -> authService.refreshAccessToken(request));

        assertEquals("Refresh token is missing", ex.getMessage());
    }

    @Test
    void refreshAccessToken_InvalidToken_ShouldThrow() {
        when(jwtUtils.getRefreshTokenFromCookie(request)).thenReturn(Optional.of(refreshToken));
        when(jwtUtils.validateJwtToken(refreshToken)).thenReturn(false);

        InvalidToken ex = assertThrows(InvalidToken.class, () -> authService.refreshAccessToken(request));
        assertEquals("Refresh token is invalid", ex.getMessage());
    }

    @Test
    void refreshAccessToken_BlacklistedToken_ShouldThrow() {
        when(jwtUtils.getRefreshTokenFromCookie(request)).thenReturn(Optional.of(refreshToken));
        when(jwtUtils.validateJwtToken(refreshToken)).thenReturn(true);
        when(tokenRepository.isRefreshTokenBlacklisted(refreshToken)).thenReturn(true);

        BlacklistedTokenException ex = assertThrows(BlacklistedTokenException.class, () ->
                authService.refreshAccessToken(request));

        assertEquals("Refresh token has been blacklisted", ex.getMessage());

    }

    @Test
    void refreshAccessToken_TokenMismatch_ShouldThrow() {
        when(jwtUtils.getRefreshTokenFromCookie(request)).thenReturn(Optional.of(refreshToken));
        when(jwtUtils.validateJwtToken(refreshToken)).thenReturn(true);
        when(tokenRepository.isRefreshTokenBlacklisted(refreshToken)).thenReturn(false);
        when(jwtUtils.getUserNameFromJwtToken(refreshToken)).thenReturn("testuser");
        when(tokenRepository.getRefreshToken("testuser")).thenReturn("different-token");

        InvalidToken ex = assertThrows(InvalidToken.class, () -> authService.refreshAccessToken(request));
        assertEquals("Refresh token does not match", ex.getMessage());
    }


}