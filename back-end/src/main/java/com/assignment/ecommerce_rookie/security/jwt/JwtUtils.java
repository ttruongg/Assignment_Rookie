package com.assignment.ecommerce_rookie.security.jwt;


import com.assignment.ecommerce_rookie.dto.request.TokenPair;
import com.assignment.ecommerce_rookie.exception.TokenMissingException;
import com.assignment.ecommerce_rookie.security.services.UserDetailsImpl;
import com.assignment.ecommerce_rookie.security.services.UserDetailsServiceImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    private static final String CLAIM_NAME_USER_ID = "userId";
    private static final String CLAIM_NAME_ROLE = "role";

    private final UserDetailsServiceImpl userDetailsService;

    @Value("${jwtSecret}")
    private String jwtSecret;

    @Value("${jwt.accessExpirationMs}")
    private long accessExpirationMs;

    @Value("${jwt.refreshExpirationMs}")
    private long refreshExpirationMs;

    @Value("${jwt.AccessTokenCookieName}")
    private String accessTokenCookie;

    @Value("${jwt.RefreshTokenCookieName}")
    private String refreshTokenCookie;

    private static final String REFRESH_TOKEN_PATH = "/api/v1/auth/token/refresh";

    public JwtUtils(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public Optional<String> getJwtFromCookie(HttpServletRequest request, String cookieName) {
        return Optional.ofNullable(WebUtils.getCookie(request, cookieName))
                .map(Cookie::getValue);
    }

    public Optional<String> getRefreshTokenFromCookie(HttpServletRequest request) {
        return getJwtFromCookie(request, refreshTokenCookie);
    }

    public TokenPair generateTokenPair(UserDetailsImpl userDetails) {
        String accessToken = generateAccessToken(userDetails);
        String refreshToken = generateRefreshToken(userDetails);
        return new TokenPair(accessToken, refreshToken, accessExpirationMs, refreshExpirationMs);
    }

    public String generateAccessToken(UserDetailsImpl userDetails) {
        return generateToken(userDetails, accessExpirationMs);
    }

    public String generateRefreshToken(UserDetailsImpl userDetails){
        return generateToken(userDetails, refreshExpirationMs);
    }

    public ResponseCookie generateRefreshTokenCookie(UserDetailsImpl userDetails) {
        String jwt = generateToken(userDetails, refreshExpirationMs);
        return buildCookie(refreshTokenCookie, jwt, REFRESH_TOKEN_PATH, refreshExpirationMs);
    }

    private String generateToken(UserDetailsImpl user, long expirationMs) {
        List<String> roleNames = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .subject(user.getUsername())
                .claim(CLAIM_NAME_ROLE, roleNames)
                .claim(CLAIM_NAME_USER_ID, user.getId())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key())
                .compact();
    }

    private ResponseCookie buildCookie(String name, String value, String path, long maxAge) {
        return ResponseCookie.from(name, value)
                .path(path)
                .maxAge(maxAge / 1000)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .build();
    }

    public ResponseCookie cleanAccessTokenCookie() {
        return buildCookie(accessTokenCookie, "", "/", 0);
    }

    public ResponseCookie cleanRefreshTokenCookie() {
        return buildCookie(refreshTokenCookie, "", REFRESH_TOKEN_PATH, 0);
    }

    public String generateAccessTokenFromCookie(HttpServletRequest request) {
        Optional<String> refreshTokenOpt = getJwtFromCookie(request, refreshTokenCookie);
        String refreshToken = refreshTokenOpt.orElseThrow(() -> new TokenMissingException("Refresh token is missing"));
        boolean isTokenValid = validateJwtToken(refreshToken);
        if (isTokenValid) {
            String username = getUserNameFromJwtToken(refreshToken);
            UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(username);
            if (userDetails != null && userDetails.getUsername().equals(username)) {
                return generateAccessToken(userDetails);
            }
        }
        return null;

    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }


    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build().parseSignedClaims(token)
                .getPayload().getSubject();
    }

    public Date getExpirationDate(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build().parseSignedClaims(token)
                .getPayload().getExpiration();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }


}
