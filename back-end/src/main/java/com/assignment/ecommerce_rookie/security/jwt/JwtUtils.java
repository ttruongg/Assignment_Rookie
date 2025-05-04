package com.assignment.ecommerce_rookie.security.jwt;


import com.assignment.ecommerce_rookie.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

    public Optional<String> getJwtFromCookie(HttpServletRequest request, String cookieName) {
        return Optional.ofNullable(WebUtils.getCookie(request, cookieName))
                .map(Cookie::getValue);
    }


    public ResponseCookie generateAccessTokenCookie(UserDetailsImpl userDetails) {
        String jwt = generateToken(userDetails.getUsername(), accessExpirationMs);
        return ResponseCookie.from(accessTokenCookie, jwt)
                .path("/api")
                .maxAge(accessExpirationMs / 1000)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .build();
    }

    public ResponseCookie generateRefreshTokenCookie(UserDetailsImpl userDetails) {
        String jwt = generateToken(userDetails.getUsername(), refreshExpirationMs);
        return ResponseCookie.from(refreshTokenCookie, jwt)
                .path("/api/auth/refresh-token")
                .maxAge(refreshExpirationMs / 1000)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .build();
    }


    private String generateToken(String username, long expirationMs) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public ResponseCookie cleanAccessTokenCookie() {
        ResponseCookie accessCookie = ResponseCookie.from(accessTokenCookie, null)
                .path("/api")
                .maxAge(0)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .build();

        return accessCookie;


    }

    public ResponseCookie cleanRefreshTokenCookie() {
        ResponseCookie refreshCookie = ResponseCookie.from(refreshTokenCookie, null)
                .path("/api/auth/refresh-token")
                .maxAge(0)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .build();

        return refreshCookie;
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
