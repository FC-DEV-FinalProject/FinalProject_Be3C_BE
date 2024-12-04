package com.be3c.sysmetic.global.config.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Builder(toBuilder = true)
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    /*
        [설명]
            Request Header에 포함된 JWT Access 토큰을 확인하고, 토큰의 유효성을 검증
        [순서]
         1. Access 토큰 추출
            1-1. 토큰X -> 다음 필터로 요청 전달
            1-2. 토큰O -> 2.으로 이동
         2. Access 토큰 유효성 검증
            2-1. Access 유효
                2-1-1. Authentication 객체 생성 -> Security Context 설정 -> 다음 필터로 요청 전달
            2-2. Access 만료
                -> 3.으로 이동
            2-3. 예외
                -> 다음 필터로 요청 전달
         3. Refresh 토큰 유효성 검증
            3-1. Refresh 유효
                -> 재발급(+ Redis 저장) -> Authentication 객체 생성 -> Security Context 설정 -> 다음 필터로 요청 전달
            3-2. Refresh 만료
                -> 로그아웃 처리
            3-3. 예외
                -> 다음 필터로 요청 전달
     */

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 1. Access 토큰 추출
        String accessToken = jwtTokenProvider.extractToken(request);
        if(accessToken == null) {
            log.info("Access 토큰이 비어있습니다.");
            chain.doFilter(request, response);
            return;
        }

        // 2. Access 토큰 유효성 검증 (true- 유효한 토큰, false - 만료된 토큰, exception - 예외)
        boolean accessTokenValidResult;
        try {
            accessTokenValidResult = jwtTokenProvider.validateToken(accessToken);
        } catch (AuthenticationCredentialsNotFoundException e) {
            log.info("예외 발생으로 토큰 유효성 검사 실패");
            chain.doFilter(request, response);
            return;
        }

        /*
            Access 토큰이 유효한 경우, Authentication 객체 생성 -> Security Context 설정 -> 다음 필터로 요청 전달
            Access 토큰이 만료된 경우, Refresh 토큰 유효성 검증
            예외 발생한 경우, 다음 필터로 요청 전달
        */

        // 2.1 Access 토큰이 유효한 경우
        if(accessTokenValidResult) {
            // 사용자 정보 추출
            Claims claims = jwtTokenProvider.parseTokenClaims(accessToken);

            Long memberId = claims.get("memberId", Long.class);
            String role = claims.get("role", String.class);
            String email = claims.get("email", String.class);

            // 4-1. role을 GrantedAuthority로 변환하여 authorities 리스트 생성
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

            // 4-2. UserDetails 객체 생성
            UserDetails userDetails = new CustomUserDetails(memberId, role, email, authorities);

            // 5. SecurityContext에 authentication 저장
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 6. 다음 필터로 요청 전달
            chain.doFilter(request, response);
            return;
        }

        // 2.2 Access 토큰이 만료된 경우
        // 재발급 필요한 경우 = (jwtTokenProvider.needsReissueToken(accessToken) = true)
        if(jwtTokenProvider.needsReissueToken(accessToken)) {
            Map<String,String> tokenMap = jwtTokenProvider.reissueToken(accessToken); // 재발급 메서드

            // 재발급 실패한 경우
            if(tokenMap == null) {
                log.error("토큰 재발급 실패");
                chain.doFilter(request, response);
                return;
            }

            // 재발급 성공한 경우
            // 재발급된 access 토큰 -> response에 담아서 전달
            response.setHeader("Authorization", "Bearer " + tokenMap.get("accessToken"));

            // 재발급된 refresh 토큰 -> Redis에 저장
            jwtTokenProvider.saveToken(tokenMap);
            // 기존 refresh 토큰 -> Redis에서 삭제
            jwtTokenProvider.deleteToken(accessToken);


            // 사용자 정보 추출
            Claims claims = jwtTokenProvider.parseTokenClaims(tokenMap.get("accessToken"));

            Long memberId = claims.get("memberId", Long.class);
            String role = claims.get("role", String.class);
            String email = claims.get("email", String.class);

            // 4-1. role을 GrantedAuthority로 변환하여 authorities 리스트 생성
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

            // 4-2. UserDetails 객체 생성
            UserDetails userDetails = new CustomUserDetails(memberId, role, email, authorities);

            // 5. SecurityContext에 authentication 저장
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 6. 다음 필터로 요청 전달
            chain.doFilter(request, response);
        } else {
            // 재발급 필요 없는 경우 = (jwtTokenProvider.needsReissueToken(accessToken) = false)
            chain.doFilter(request, response);
        }
    }




}
