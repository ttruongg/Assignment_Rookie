package com.assignment.ecommerce_rookie.repository;

import com.assignment.ecommerce_rookie.dto.request.TokenPair;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class TokenRepository {
    private final RedisTemplate<String, Object> redisTemplate;

    public static final String ACCESS_TOKEN_KEY_PREFIX = "user:access:";
    public static final String REFRESH_TOKEN_KEY_PREFIX = "user:refresh:";

    public static final String ACCESS_BLACKLIST_PREFIX = "blacklist:access:";
    public static final String REFRESH_BLACKLIST_PREFIX = "blacklist:refresh:";

    public void storeToken(String username,
                           TokenPair tokenPair) {

        String accessKey = ACCESS_TOKEN_KEY_PREFIX + username;
        redisTemplate.opsForValue().set(accessKey, tokenPair.getAccessToken());
        redisTemplate.expire(accessKey, tokenPair.getAccessTokenExpiration(), TimeUnit.MILLISECONDS);

        String refreshKey = REFRESH_TOKEN_KEY_PREFIX + username;
        redisTemplate.opsForValue().set(refreshKey, tokenPair.getRefreshToken());
        redisTemplate.expire(refreshKey, tokenPair.getRefreshTokenExpiration(), TimeUnit.MILLISECONDS);

    }

    public String getAccessToken(String username) {
        String accessKey = ACCESS_TOKEN_KEY_PREFIX + username;
        return getToken(accessKey);
    }

    public String getRefreshToken(String username) {
        String refreshKey = REFRESH_TOKEN_KEY_PREFIX + username;
        return getToken(refreshKey);
    }

    private String getToken(String accessKey) {
        Object accessToken = redisTemplate.opsForValue().get(accessKey);
        return accessToken != null ? accessToken.toString() : null;
    }

    public void removeAllTokens(String username) {
        String accessToken = getAccessToken(username);
        String refreshToken = getRefreshToken(username);

        String accessKey = ACCESS_TOKEN_KEY_PREFIX + username;
        String refreshKey = REFRESH_TOKEN_KEY_PREFIX + username;

        Long remainingAccessTime = redisTemplate.getExpire(accessKey, TimeUnit.MILLISECONDS);
        Long remainingRefreshTime = redisTemplate.getExpire(refreshKey, TimeUnit.MILLISECONDS);

        redisTemplate.delete(accessKey);
        redisTemplate.delete(refreshKey);

        if (accessToken != null && remainingAccessTime != null && remainingAccessTime > 0) {
            blacklistAccessToken(accessToken, remainingAccessTime);
        }

        if (refreshToken != null && remainingRefreshTime != null && remainingRefreshTime > 0) {
            blacklistRefreshToken(refreshToken, remainingRefreshTime);
        }

    }

    public void removeAccessToken(String username) {
        String accessToken = getAccessToken(username);
        String accessKey = ACCESS_TOKEN_KEY_PREFIX + username;
        Long remainingAccessTime = redisTemplate.getExpire(accessKey, TimeUnit.MILLISECONDS);

        redisTemplate.delete(accessKey);

        if (accessToken != null && remainingAccessTime != null && remainingAccessTime > 0) {
            blacklistAccessToken(accessToken, remainingAccessTime);
        }

    }

    private void blacklistRefreshToken(String refreshToken, long refreshTokenExpirationMS) {
        String key = REFRESH_BLACKLIST_PREFIX + refreshToken;
        redisTemplate.opsForValue().set(key, "blacklisted");
        redisTemplate.expire(key, refreshTokenExpirationMS, TimeUnit.MILLISECONDS);
    }

    private void blacklistAccessToken(String accessToken, long jwtExpirationMS) {
        String key = ACCESS_BLACKLIST_PREFIX + accessToken;
        redisTemplate.opsForValue().set(key, "blacklisted");
        redisTemplate.expire(key, jwtExpirationMS, TimeUnit.MILLISECONDS);
    }

    public boolean isAccessTokenBlacklisted(String accessToken) {
        String key = ACCESS_BLACKLIST_PREFIX + accessToken;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public boolean isRefreshTokenBlacklisted(String refreshToken) {
        String key = REFRESH_BLACKLIST_PREFIX + refreshToken;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

}
