package com.be3c.sysmetic.global.config.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtTokenProvider {
    /*
        [설명]
        jwt 토큰 생성, 검증 로직 등이 포함되는 클래스
        access/refresh Token 생성, 토큰 유효성 검증, 만료시간 설정, 토큰에 있는 사용자 정보 추출

        [메서드]
        1. Token 생성 메서드
        2. Access Token 생성 메서드
        3. Refresh Token (Month) 생성 메서드
        4. Refresh Token (Hour) 생성 메서드
        5. 토큰 유효성 검증 메서드
        6. 재발급 필요 여부 확인 메서드
        7. Access 와 Refresh Token 재발급 메서드
        8. Access 토큰에서 사용자 정보 추출 메서드
        9. 요청 헤더에서 Jwt 토큰을 추출하는 메서드
        10. roleCode 를 role 로 명칭 변환 메서드
        11. Redis에 토큰 저장
        12. Redis에 토큰 삭제

        [Token 처리 과정]
        1. Access 토큰 유효성 검증
            1-1. 유효 (검증 결과 true)
                -> 로그인 단계 진행
            1-2. 만료 (검증 결과 false)
                -> 2.로 이동 (refresh 토큰 유효성 검증)
            1-3. 예외 (올바르지 않은 토큰 등등)
                -> 예외 처리
        2. Refresh 토큰 유효성 검증
            2-1. 유효한 경우
                -> Access 와 Refresh 토큰 재발급 진행
            2-2. 만료된 경우
                -> 유효하지 않은 접근으로 처리 (재로그인 필요)
            2-3. 예외 (올바르지 않은 토큰 등등)
                -> 예외 처리
    */

    @Autowired
    RedisUtils redisUtils;

    @Value("${custom.jwt.secret.key}")
    private String SECRET_KEY;

    @Value("${access.token.expire.time}")
    private long ACCESS_TOKEN_EXPIRE_TIME;   // 30분

    @Value("${month.refresh.token.expire.time}")
    private long MONTH_REFRESH_TOKEN_EXPIRE_TIME;    // 30일

    @Value("${hour.refresh.token.expire.time}")
    private long HOUR_REFRESH_TOKEN_EXPIRE_TIME;     // 1시간

    // 1. Token 생성 메서드
    public String generateToken(Long memberId, String email, String roleCode, long expiration) {
        String role = null;
        if (roleCode.startsWith("RC")) {
            // "RC"로 시작하는 경우, roleCode를 role로 변경
            role = roleCodeChangeRole(roleCode);
        } else {
            role = roleCode;
        }

        Date now = new Date();
        Date tokenExpires = new Date(now.getTime() + expiration);
        return Jwts.builder()
                .claim("memberId", memberId)
                .claim("email", email)
                .claim("role", role)
                .issuedAt(now)
                .expiration(tokenExpires)
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    // 2. Access Token 생성 메서드
    public String generateAccessToken(Long memberId, String email, String roleCode) {
        return generateToken(memberId, email, roleCode, ACCESS_TOKEN_EXPIRE_TIME);
    }

    // 3. Refresh Token (Month) 생성 메서드
    public String generateMonthRefreshToken(Long memberId, String email, String roleCode) {
        return generateToken(memberId, email, roleCode, MONTH_REFRESH_TOKEN_EXPIRE_TIME);
    }

    // 4. Refresh Token (Hour) 생성 메서드
    public String generateHourRefreshToken(Long memberId, String email, String roleCode) {
        return generateToken(memberId, email, roleCode, HOUR_REFRESH_TOKEN_EXPIRE_TIME);
    }

    // 5. 토큰 유효성 검증 메서드
    public boolean validateToken(String token) throws AuthenticationCredentialsNotFoundException {
        try {
            Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8))) // 서명 검증용 비밀키 설정
                    .build()
                    .parseSignedClaims(token); // 서명, 구조, 만료 여부를 검증하여 Jws 객체를 반환
            return true;
        } catch (ExpiredJwtException e) {
            log.info("만료된 토큰");
            return false;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("서명 또는 형식이 잘못된 토큰입니다.", e);
            throw new AuthenticationCredentialsNotFoundException("인증에 실패했습니다.");
        } catch (SignatureException e) {
            log.info("서명이 잘못되거나 위조된 토큰입니다.", e);
            throw new AuthenticationCredentialsNotFoundException("인증에 실패했습니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 형식의 토큰입니다.", e);
            throw new AuthenticationCredentialsNotFoundException("인증에 실패했습니다.");
        } catch (IllegalArgumentException e) {
            log.info("토큰이 비어있거나 잘못된 값입니다.", e);
            throw new AuthenticationCredentialsNotFoundException("인증에 실패했습니다.");
        }
    }

    // 6. 재발급 필요 여부 확인 메서드
    public boolean needsReissueToken(String accessToken) {
        /*
            1. access 토큰 만료여부 확인
                1-1. 만료되지 않은 경우 (true)
                    -> false (로그인 유지)
                1-2. 만료된 경우 (false)
                    -> 2. 이동
            2. refresh 토큰 만료여부 확인
                2-1. 만료되지 않은 경우 (true)
                    -> true (재발급 로직 진행)
                2-2. 만료된 경우 (false)
                    -> false (재로그인)
         */
        if(validateToken(accessToken)) {
            return false;
        } else {
            String refreshToken = redisUtils.getRefreshToken(accessToken);
            boolean needsReissue = false;
            if(refreshToken != null) {
                needsReissue = validateToken(refreshToken);
            }
            return needsReissue;
        }
    }

    // 7. Access 와 Refresh Token 재발급 메서드
    public Map<String, String> reissueToken(String oldAccessToken) {
        /*
            유효성 검사에서 true(Access 만료, Refresh 유효 상태)가 나오면,
            기존의 access 토큰에 저장된 memberId, email, role 정보를 그대로 가져와서,
            새로운 토큰 생성시에 넣어서 만들어준다.
            새롭게 만든 토큰을 반환한다.
         */
        Map<String, String> tokenMap = new HashMap<>();

        Claims claims = parseTokenClaims(oldAccessToken);
        Long memberId = Long.valueOf(String.valueOf(claims.get("memberId")));
        String email = (String) claims.get("email");
        String role = (String) claims.get("role");

        String newAccessToken = generateAccessToken(memberId, email, role);
        String newRefreshToken = generateMonthRefreshToken(memberId, email, role);

        tokenMap.put("accessToken", newAccessToken);
        tokenMap.put("refreshToken", newRefreshToken);

        return tokenMap;
    }

    // 8. Access 토큰에서 사용자 정보 추출 메서드
    public Claims parseTokenClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.info("만료된 토큰");
            return e.getClaims();
        } catch (SecurityException | MalformedJwtException e) {
            log.info("서명 또는 형식이 잘못된 토큰입니다.", e);
            throw new AuthenticationCredentialsNotFoundException("인증에 실패했습니다.");
        } catch (SignatureException e) {
            log.info("서명이 잘못되거나 위조된 토큰입니다.", e);
            throw new AuthenticationCredentialsNotFoundException("인증에 실패했습니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 형식의 토큰입니다.", e);
            throw new AuthenticationCredentialsNotFoundException("인증에 실패했습니다.");
        } catch (IllegalArgumentException e) {
            log.info("토큰이 비어있거나 잘못된 값입니다.", e);
            throw new AuthenticationCredentialsNotFoundException("인증에 실패했습니다.");
        }
    }

    // 9. 요청 헤더에서 Jwt 토큰을 추출하는 메서드
    public String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.split(" ")[1]; // "Bearer "를 제외한 순수 토큰만 추출
        }
        return null;
    }

    // 10. roleCode 를 role 로 명칭 변환 메서드
    public String roleCodeChangeRole(String roleCode) {
        /*
            RC001 (일반회원)(USER) <-> RC003 (일반회원 관리자)(USER_MANAGER)
            RC002 (트레이더)(TRADER) <-> RC004 (트레이더 관리자)(TRADER_MANAGER)
            RC005 (수퍼관리자)(ADMIN)
        */
        if (roleCode == null || roleCode.isEmpty()) {
            log.info("roleCode가 null이거나 빈 값입니다.");
            throw new IllegalArgumentException("roleCode가 올바르지 않습니다.");
        }

        if(roleCode.equals("USER") || roleCode.equals("TRADER") || roleCode.equals("MANAGER") || roleCode.equals("ADMIN")) {
            return roleCode;
        }

        return switch (roleCode) {
            case "RC001" -> "USER";
            case "RC002" -> "TRADER";
            case "RC003", "RC004", "USER_MANAGER", "TRADER_MANAGER" -> "MANAGER";
            case "RC005" -> "ADMIN";
            default -> {
                log.info("올바르지 않은 roleCode값이 입력됐습니다.");
                throw new IllegalArgumentException("Invalid roleCode: " + roleCode);
            }
        };
    }

    // 11. Redis에 토큰 저장
    public void saveToken(Map<String, String> tokenMap) {
        redisUtils.saveToken(tokenMap.get("accessToken"), tokenMap.get("refreshToken"));
    }

    // 12. Redis에서 토큰 삭제
    public void deleteToken(String accessToken) {
        redisUtils.deleteToken(accessToken);
    }
}
