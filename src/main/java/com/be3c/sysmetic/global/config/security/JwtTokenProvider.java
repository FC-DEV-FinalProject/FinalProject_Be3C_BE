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
        jwt 토큰 생성, 검증 로직 등이 포함되는 클래스
        access/refresh Token 생성, 토큰 유효성 검증, 만료시간 설정, 토큰에 있는 사용자 정보 추출

        1. Token 생성 메서드
        2. Access Token 생성 메서드
        3. Refresh Token (Month) 생성 메서드
        0. Refresh Token (Hour) 생성 메서드
        4. 토큰 유효성 검증 메서드
        5. 재발급 필요 여부 확인 메서드
        6. Access 와 Refresh Token 재발급 메서드
        7. Access 토큰에서 사용자 정보 추출 메서드
        8. 요청 헤더에서 Jwt 토큰을 추출하는 메서드
        9. roleCode 를 role 로 명칭 변환 메서드
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
    public String generateToken(Long memberId, String email, String roleCode, String nickname, String profileImage, long expiration) {
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
                .claim("nickname", nickname)
                .claim("profileImage", profileImage)
                .issuedAt(now)
                .expiration(tokenExpires)
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    // 2. Access Token 생성 메서드
    public String generateAccessToken(Long memberId, String email, String roleCode, String nickname, String profileImage) {
        return generateToken(memberId, email, roleCode, nickname, profileImage, ACCESS_TOKEN_EXPIRE_TIME);
    }

    // 3. Refresh Token (Month) 생성 메서드
    public String generateMonthRefreshToken(Long memberId, String email, String roleCode, String nickname, String profileImage) {
        return generateToken(memberId, email, roleCode, nickname, profileImage, MONTH_REFRESH_TOKEN_EXPIRE_TIME);
    }

    // 0. Refresh Token (Hour) 생성 메서드
    public String generateHourRefreshToken(Long memberId, String email, String roleCode, String nickname, String profileImage) {
        return generateToken(memberId, email, roleCode, nickname, profileImage, HOUR_REFRESH_TOKEN_EXPIRE_TIME);
    }

    // 4. 토큰 유효성 검증 메서드
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

    // 5. 재발급 필요 여부 확인 메서드
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
            if(refreshToken != null) {
                return validateToken(refreshToken);
            }
            return false;
        }
    }

        /* 5. Token 재발급 메서드
            올바른 토큰이라고 가정
            1) Access 토큰은 만료O, Refresh 토큰은 만료X 경우
                - request에 있는 Access 토큰을 서버에서 받는다.
                - Access 토큰의 만료일자 확인
                - 1-1) 만료되지 않은 경우
                    -> 로그인 단계 진행
                - 1-2) 만료된 경우
                    - Refresh 토큰의 만료일자 확인
                        - 1-2-1) 만료된 경우
                            -> 유효하지 않은 접근으로 처리 (재로그인 안내)
                        - 1-2-2) 만료되지 않은 경우
                            -> Access 와 Refresh Token 재발급
            2) Access 와 Refresh 토큰 모두 만료된 경우

            [순서]
            1. Access 토큰을 받아서 validateToken(token) 메서드로 유효성 검증
            2. 검증 결과에 따른 처리
                2-1. 검증 결과 true인 경우 (유효)
                    -> Access 토큰이 유효하므로, 재발급 진행하지 않고 메서드 종료
                2-2. 검증 결과가 false인 경우 (만료)
                    -> 3. refresh 토큰의 유효성 검증
                2-3. 다른 예외가 발생한 경우
                    -> 예외 처리
            3. Refresh 토큰의 만료 여부 확인
                3-1. 유효한 경우
                    -> 4. Access 와 Refresh 토큰 재발급
                3-2. 만료된 경우
                    -> 재발급 불가능. 예외 발생 (재로그인 유도)
            4. Access 와 Refresh Token 재발급
        */
    // 6. Access 와 Refresh Token 재발급 메서드
    public Map<String, String> reissueToken(String token) {
        /*
            유효성 검사에서 true(Access 토큰만 만료된 상태)가 나오면,
            기존의 access 토큰에 저장된 memberId, email, role 정보를 그대로 가져와서,
            새로운 토큰 생성시에 넣어서 만들어준다.
            새롭게 만든 토큰을 반환한다.
         */
        Map<String, String> tokenMap = new HashMap<>();

        Claims claims = parseTokenClaims(token);
        Long memberId = Long.valueOf(String.valueOf(claims.get("memberId")));
        String email = (String) claims.get("email");
        String role = (String) claims.get("role");
        String nickname = (String) claims.get("nickname");
        String profileImage = (String) claims.get("profileImage");

        String accessToken = generateAccessToken(memberId, email, role, nickname, profileImage);
        String refreshToken = generateMonthRefreshToken(memberId, email, role, nickname, profileImage);
        tokenMap.put("accessToken", accessToken);
        tokenMap.put("refreshToken", refreshToken);

        return tokenMap;
    }

    // 7. Access 토큰에서 사용자 정보 추출 메서드
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

    // 8. 요청 헤더에서 Jwt 토큰을 추출하는 메서드
    public String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.split(" ")[1]; // "Bearer "를 제외한 순수 토큰만 추출
        }
        return null;
    }

    // 9. roleCode 를 role 로 명칭 변환 메서드
    /*
        RC001 (일반회원)(USER) <-> RC003 (일반회원 관리자)(MANAGER)
        RC002 (트레이더)(TRADER) <-> RC004 (트레이더 관리자)(MANAGER)
        RC005 (수퍼관리자)(ADMIN)
     */
    public String roleCodeChangeRole(String roleCode) {
        if (roleCode == null || roleCode.isEmpty()) {
            log.info("roleCode가 null이거나 빈 값입니다.");
            throw new IllegalArgumentException("roleCode가 올바르지 않습니다.");
        }

        return switch (roleCode) {
            case "RC001" -> "USER";
            case "RC002" -> "TRADER";
            case "RC003", "RC004" -> "MANAGER";
            case "RC005" -> "ADMIN";
            default -> {
                log.info("올바르지 않은 roleCode값이 입력됐습니다.");
                throw new IllegalArgumentException("Invalid roleCode: " + roleCode);
            }
        };
    }
}
