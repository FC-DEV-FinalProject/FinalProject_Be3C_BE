//package com.be3c.sysmetic.global.config.security;
//
//import io.jsonwebtoken.Claims;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Slf4j
////@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
//@AllArgsConstructor
//@Builder(toBuilder = true)
//public class JwtAuthenticationFilter extends OncePerRequestFilter {    // api 요청 시 한 번만 수행
//    // 들어올 땐 SecurityContext에 추가하고, 나갈 땐 SecurityContext에서 삭제
//    // => 찾아보니까 이 방법은 세션으로 시큐리티를 작동시킬 때 이렇게 하는 것 같다. JWT를 사용하면 시큐리티 세션을 사용하지 않으므로, 필터로 처리하기 보다는 Controller나 Handler로 처리하는 게 일반적이라고 하더라!
//    // 로그아웃 로직은 Controller에서 따로 하는 걸로 결정.
//
//    /*
//        [역할]
//         - Request Header에 포함된 JWT Access 토큰을 확인하고, 토큰의 유효성을 검증
//        [기본 메서드]
//         1. doFilterInternal() : jwt 토큰을 요청 헤더에서 추출, 유효성 검증, 인증 성공 시 SecurityContext에 Authentication 객체를 설정.
//         2. extractToken() : 요청 헤더에서 Jwt 토큰을 추출하는 메서드
//        [흐름]
//         - Access 토큰 받기 -> Access 토큰 추출 -> Access 토큰 유효성 검증
//           > (토큰없는경우) -> 다음 필터로 요청 전달
//           > (유효한 경우) -> Authentication 객체 생성 -> Security Context 설정 -> 다음 필터로 요청 전달
//           > (만료된 경우) -> Refresh 토큰 유효성 검증
//                > (유효한 경우) -> 재발급 여부 확인 -> Authentication 객체 생성 -> Security Context 설정 -> 다음 필터로 요청 전달
//                > (만료된 경우) -> 다음 필터로 요청 전달
//     */
//    private final JwtTokenProvider jwtTokenProvider;
//    private final RedisUtils redisUtils;
//
//    @Override
//    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
//        // 1. request header에서 JWT 토큰 추출
//        String token = extractToken(request);
//        if(token == null) {
//            log.info("jwt토큰이 비어있습니다.");
//            chain.doFilter(request, response);
//            return;
//        }
//
//        // 2. 토큰 유효성 검사 (true- 유효한 토큰, false - 만료된 토큰, exception - 예외)
//        boolean tokenValidResult = false;
//        try {
//            tokenValidResult = jwtTokenProvider.validateToken(token);
//        } catch (AuthenticationCredentialsNotFoundException e) {
//            log.info("토큰 유효성 검사 실패");
//            chain.doFilter(request, response);
//            return;
//        }
//
//        // 3. (만료시) 토큰 재발급 진행
//        Map<String,String> tokenMap = new HashMap<>();
//        if(!tokenValidResult){
//            tokenMap = jwtTokenProvider.reissueToken(token);
//        }
//
//        // 3-1. tokenMap 이 null 인 경우
//        if(tokenMap == null) {
//            log.info("토큰 재발급 실패");
//            chain.doFilter(request, response);
//            return;
//        }
//
//        // 3-2. tokenMap 이 null 이 아닌 경우 (토큰이 제대로 담긴 경우)
//        // 재발급된 access 토큰 -> 사용자에게 전달 필요 (어떻게 전달하지? respone Header로 전달해보자)
//        response.setHeader("Authorization", "Bearer " + tokenMap.get("accessToken"));
//        // 재발급된 refresh 토큰 -> Redis에 저장
//        redisUtils.saveToken(tokenMap.get("accessToken"), tokenMap.get("refreshToken"));
//        // 기존 refresh 토큰 -> Redis에서 삭제
//        redisUtils.deleteToken(token);
//
//        // 4. 사용자 정보 추출
//        Claims claims = null;
//        if(jwtTokenProvider.needsReissueToken(token)) {
//            claims = jwtTokenProvider.parseTokenClaims(tokenMap.get("accessToken"));
//        } else {
//            claims = jwtTokenProvider.parseTokenClaims(token);
//        }
//            // 경우의 수 = request 토큰으로 추출할 때 / 재발급한 토큰으로 추출할 때
//        Long memberId = claims.get("memberId", Long.class);
//        String email = claims.get("email", String.class);
//        String role = claims.get("role", String.class);
//
//        // 4-1. role을 GrantedAuthority로 변환하여 authorities 리스트 생성
//        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
//
//        // 4-2. UserDetails 객체 생성
//        UserDetails userDetails = new CustomUserDetails(memberId, email, role, authorities);
//
//        // 5. SecurityContext에 authentication 저장
//        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        // 6. 다음 필터로 요청 전달
//        chain.doFilter(request, response);
//    }
//
//    private String extractToken(HttpServletRequest request) {
//        String bearerToken = request.getHeader("Authorization");
//        if (bearerToken != null && bearerToken.startsWith("bearer ")) {
//            return bearerToken.substring(7); // "Bearer "를 제외한 순수 토큰만 추출
//        }
//        return null;
//    }
//}
