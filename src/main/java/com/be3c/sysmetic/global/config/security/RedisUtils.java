package com.be3c.sysmetic.global.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisUtils {
    /*
        1. Redis에 토큰 저장 메서드
        2. Redis에서 토큰 조회 메서드
        3. Redis에 저장된 토큰 삭제 메서드
     */

    private final RedisTemplate<String, String> redisTemplate0;


    // 1. Redis에 토큰 저장 메서드 (성공 여부를 확인하기 위해 boolean으로 바꾸고 싶다)
    public void saveToken(String accessToken, String refreshToken) {
        redisTemplate0.opsForValue().set("refreshToken:" + accessToken, refreshToken);
    }
    // 2. Redis에서 토큰 조회 메서드
    public String getRefreshToken(String accessToken) {
        return redisTemplate0.opsForValue().get("refreshToken:" + accessToken);
    }
    // 3. Redis에 저장된 토큰 삭제 메서드 (성공 여부를 확인하기 위해 boolean으로 바꾸고 싶다)
    public void deleteToken(String accessToken) {
        redisTemplate0.delete("refreshToken:" + accessToken);
    }
}
