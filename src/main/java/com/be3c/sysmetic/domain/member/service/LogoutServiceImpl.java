package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.global.config.security.JwtTokenProvider;
import com.be3c.sysmetic.global.config.security.RedisUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LogoutServiceImpl implements LogoutService {

    /*
        [로그아웃 해야 하는 것]
        - 요청 헤더에서 토큰 추출
        - 해당 회원의 기존 토큰을 Redis 에서 삭제
        - SecurityContext에서 회원의 인증정보 삭제
        - 필요하다면 블랙 리스트를 만들어서 새로운 Redis Template에 저장하여 관리되도록 하기
     */

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisUtils redisUtils;

    @Override
    public void logout(HttpServletRequest request) {
        // 1. 토큰 추출
        String accessToken = jwtTokenProvider.extractToken(request);

        // 2. Redis에서 토큰 삭제
        redisUtils.deleteToken(accessToken);

        // 3. SecurityContext에서 인증정보 삭제
        SecurityContextHolder.clearContext();
    }

}
