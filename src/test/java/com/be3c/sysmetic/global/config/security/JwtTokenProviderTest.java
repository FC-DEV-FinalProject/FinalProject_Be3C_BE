package com.be3c.sysmetic.global.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.test.context.TestPropertySource;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest
class JwtTokenProviderTest {
    /*
        테스트 경우의 수
        1
        - 토큰 검증 - 토큰이 제대로 생성됐는지 확인
        - 생성된 토큰의 값 확인 - 생성된 토큰을 디코딩하여 memberId, email, role, expiration이 제대로 설정됐는지 확인
        2
        - access 토큰 생성 후 유효성 확인 - 형식이 올바른지, 만료시간(30분)이 제대로 설정됐는지
        3
        - refresh 토큰 생성 후 유효성 확인 - 형식이 올바른지, 만료시간(30일)이 제대로 설정됐는지
        4
        - true / false / 예외 - 세 경우로 나눠서 확인
        - 1) 유효한 토큰일 때, true가 나오는지
        - 2) 만료된 토큰일 때, false가 나오는지
        - 3) 잘못됐거나 위조됐거나 비어있는 토큰일 때, 예외가 발생하는지
        5
        - 1) Access 유효, Refresh 유효 => false
        - 2) Access 만료, Refresh 유효 => true
        - 3) Access 만료, Refresh 만료 => false (재로그인 필요)
        - 4) 유효하지 않은 Access 토큰  => 예외처리
        6
        - access토큰만 만료된 토큰을 입력했을 때, 새로운 토큰이 제대로 생성되는지
        - 재발급 전과 후의 Claims 값이 동일하게 유지되는지
        7
        - access 토큰을 넣었을 때, memberId, email, role 정보가 제대로 추출되는지 확인
        - 잘못된 토큰을 입력했을 때, 예외 처리나 오류 응답 확인
     */

    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Autowired
    RedisUtils redisUtils;

    @Value("${custom.jwt.secret.key}")
    private String SECRET_KEY;

    @Value("${access.token.expire.time}")
    private static long ACCESS_TOKEN_EXPIRE_TIME;   // 30분

    @Value("${month.refresh.token.expire.time}")
    private static long MONTH_REFRESH_TOKEN_EXPIRE_TIME;    // 30일

    @Value("${hour.refresh.token.expire.time}")
    private static long HOUR_REFRESH_TOKEN_EXPIRE_TIME;     // 1시간

    // 테스트 공통 사용 메서드
    private Claims testMethodTokenParser(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(token) // 서명, 구조, 만료 여부를 검증
                .getPayload();
    }

    // 토큰 검증(토큰이 제대로 생성됐는지 확인), 생성된 토큰값 확인(memberId, email, role, expiration이 제대로 설정됐는지 확인)
    @Test
    @DisplayName("토큰 생성 테스트")
    public void generateTokenTest () {
        String accessToken = jwtTokenProvider.generateToken(001L, "test@gmail.com", "ADMIN", 30 * 60 * 1000L);

        // accessToken을 검증 후 claims 객체 받기
        Claims claims = testMethodTokenParser(accessToken);

        // 입력한 값(memberId, email, role, expiration)이 제대로 설정됐는지 확인
        assertEquals(String.valueOf(001L), claims.get("memberId").toString());
        // Assertions.assertTrue("OO1".equals(claims.get("memberId")));  // memberId=1 로 저장되기 때문에 "001" 과는 다름
        assertEquals("test@gmail.com", claims.get("email"));
        assertEquals("ADMIN", claims.get("role"));

        // Assertions.assertEquals(expiration.getTime(), claims.getExpiration().getTime()); // 테스트 실패 (밀리초단위 차이)
        // 해결 방법) 만료일자에서 30분을 빼면, 생성일자와 동일해야 한다. -> 이 과정을 통해서 내가 설정한 만료일시가 제대로 반영됐음을 확인할 수 있다.
        long compareValue = (claims.getExpiration().getTime()) - (30 * 60 * 1000L);
        long issuedAt = Long.parseLong(String.valueOf(claims.getIssuedAt().getTime()));
        assertEquals(compareValue, issuedAt);
    }

    // access 토큰 생성 후 유효성 확인 - 형식이 올바른지, 만료시간(30분)이 제대로 설정됐는지
    @Test
    @DisplayName("Access 토큰 생성 테스트 ")
    public void generateAccessTokenTest () {
        String accessToken = jwtTokenProvider.generateAccessToken(001L, "test@gmail.com", "ADMIN");
        Claims claims = testMethodTokenParser(accessToken);

        assertEquals(String.valueOf(001L), claims.get("memberId").toString());
        assertEquals("test@gmail.com", claims.get("email"));
        assertEquals("ADMIN", claims.get("role"));
        long compareValue = (claims.getExpiration().getTime()) - (30 * 60 * 1000L);
        long issuedAt = Long.parseLong(String.valueOf(claims.getIssuedAt().getTime()));
        assertEquals(compareValue, issuedAt);
    }

    // refresh 토큰 생성 후 유효성 확인 - 형식이 올바른지, 만료시간(30일)이 제대로 설정됐는지
    @Test
    @DisplayName("Refresh 토큰 생성 메서드")
    public void generateRefreshTokenTest () {
        String refreshToken = jwtTokenProvider.generateMonthRefreshToken(001L, "test@gmail.com", "ADMIN");
        Claims claims = testMethodTokenParser(refreshToken);

        assertEquals(String.valueOf(001L), claims.get("memberId").toString());
        assertEquals("test@gmail.com", claims.get("email"));
        assertEquals("ADMIN", claims.get("role"));
        long compareValue = (claims.getExpiration().getTime()) - (30 * 24 * 60 * 60 * 1000L);
        long issuedAt = Long.parseLong(String.valueOf(claims.getIssuedAt().getTime()));
        assertEquals(compareValue, issuedAt);
    }

    /*
        true / false / 예외 - 3가지 경우로 나눠서 확인
        1) 유효한 토큰일 때, true가 나오는지
        2) 만료된 토큰일 때, false가 나오는지
        3) 잘못됐거나 위조됐거나 비어있는 토큰일 때, 예외가 발생하는지
     */
    @Test
    @DisplayName("토큰 유효성 검증 테스트")
    public void validateToken1Test1() throws InterruptedException {
        // 1) 유효한 토큰
        String accessToken = jwtTokenProvider.generateAccessToken(001L, "test@gmail.com", "ADMIN");
        String refreshToken = jwtTokenProvider.generateMonthRefreshToken(001L, "test@gmail.com", "ADMIN");
        Assertions.assertTrue(jwtTokenProvider.validateToken(accessToken));
        Assertions.assertTrue(jwtTokenProvider.validateToken(refreshToken));

        // 2) 만료된 토큰 - 만료일자를 짧게 설정하여 유효한 토큰인지 체크 후 -> 만료되면 만료된 토큰인지 체크
        accessToken = jwtTokenProvider.generateToken(001L, "test@gmail.com", "ADMIN", 5000);
        Assertions.assertTrue(jwtTokenProvider.validateToken(accessToken));
        refreshToken = jwtTokenProvider.generateToken(001L, "test@gmail.com", "ADMIN", 5000);
        Assertions.assertTrue(jwtTokenProvider.validateToken(refreshToken));

        Thread.sleep(10000); // 10초 대기
        Assertions.assertFalse(jwtTokenProvider.validateToken(accessToken));
        Assertions.assertFalse(jwtTokenProvider.validateToken(refreshToken));

        // 3) 예외 토큰
        // 잘못된/지원되지 않는 형식
        String invalidToken = "This.is.an.invalid.token";
        Assertions.assertThrows(AuthenticationCredentialsNotFoundException.class, () -> jwtTokenProvider.validateToken(invalidToken));
        // 서명키 위조 (변경된 토큰)
        String validToken = jwtTokenProvider.generateAccessToken(001L, "test@gmail.com", "ADMIN");
        String tamperedToken = validToken + "errer";
        Assertions.assertThrows(AuthenticationCredentialsNotFoundException.class, () -> jwtTokenProvider.validateToken(tamperedToken));
        // 비어있는 토큰
        String emptyToken = null;
        Assertions.assertThrows(AuthenticationCredentialsNotFoundException.class, () -> jwtTokenProvider.validateToken(emptyToken));
    }

    /*
        전제 : 이미 Redis에 저장된 토큰이어야 한다.
        1) Access 유효, Refresh 유효 => false
        2) Access 만료, Refresh 유효 => true
        3) Access 만료, Refresh 만료 => false (재로그인 필요)
        4) 유효하지 않은(서명 오류, 형식 불일치) Access 토큰  => 예외처리
     */
    @Test
    @DisplayName("재발급 필요 여부 테스트")
    public void validateToken2Test() throws InterruptedException {
        String validAccessToken = jwtTokenProvider.generateAccessToken(001L, "test@gmail.com", "ADMIN");
        String validRefreshToken = jwtTokenProvider.generateMonthRefreshToken(001L, "test@gmail.com", "ADMIN");
        String invalidAccessToken = jwtTokenProvider.generateToken(001L, "test@gmail.com", "ADMIN", 1);
        String invalidRefreshToken = jwtTokenProvider.generateToken(001L, "test@gmail.com", "ADMIN", 1);

        // 1) Access 유효, Refresh 유효 => false
        redisUtils.saveToken(validAccessToken, validRefreshToken);
        Assertions.assertFalse(jwtTokenProvider.needsReissueToken(validAccessToken));
        // 2) Access 만료, Refresh 유효 => true
        redisUtils.saveToken(invalidAccessToken, validRefreshToken);
        Assertions.assertTrue(jwtTokenProvider.needsReissueToken(invalidAccessToken));
        // 3) Access 만료, Refresh 만료 => false (재로그인 필요)
        redisUtils.saveToken(invalidAccessToken, invalidRefreshToken);
        Assertions.assertFalse(jwtTokenProvider.needsReissueToken(invalidAccessToken));
        // 4) 유효하지 않은(서명 오류, 형식 불일치) Access 토큰  => 예외처리
        redisUtils.saveToken(validAccessToken,validRefreshToken);
        String tamperedToken = validAccessToken + "error";
        Assertions.assertThrows(AuthenticationCredentialsNotFoundException.class, () -> jwtTokenProvider.needsReissueToken(tamperedToken));
    }

    /*
        재발급 시, 재발급 전후의 Claims 값이 동일하게 유지되는지
        1) Access 만료, Refresh 유효 => 재발급O
        2) Access 유효, Refresh 유효 => null 반환
        3) Access 만료, Refresh 만료 => null 반환
        4) 유효하지 않은(서명 오류, 형식 불일치) Access 토큰  => 예외 발생
     */
    @Test
    @DisplayName("Access/Refresh 토큰 재발급 테스트")
    public void reGenerateToken() throws InterruptedException {
        String validAccessToken = jwtTokenProvider.generateAccessToken(001L, "test@gmail.com", "ADMIN");
        String validRefreshToken = jwtTokenProvider.generateMonthRefreshToken(001L, "test@gmail.com", "ADMIN");
        String invalidAccessToken = jwtTokenProvider.generateToken(001L, "test@gmail.com", "ADMIN", 1);
        String invalidRefreshToken = jwtTokenProvider.generateToken(001L, "test@gmail.com", "ADMIN", 1);

        // 1) Access 만료, Refresh 유효 => 재발급O
        redisUtils.saveToken(invalidAccessToken, validRefreshToken);
        Claims invalidAccessClaims = jwtTokenProvider.parseTokenClaims(invalidAccessToken);
        Claims validRefreshClaims = jwtTokenProvider.parseTokenClaims(validRefreshToken);

        Thread.sleep(10000);

        Map<String,String> tokenMap = jwtTokenProvider.reissueToken(invalidAccessToken);
        Assertions.assertNotNull(tokenMap);
        Claims accessClaims = jwtTokenProvider.parseTokenClaims(tokenMap.get("accessToken"));
        Claims refreshClaims = jwtTokenProvider.parseTokenClaims(tokenMap.get("refreshToken"));

        assertEquals(String.valueOf(001L), accessClaims.get("memberId").toString());
        assertEquals("test@gmail.com", accessClaims.get("email"));
        assertEquals("ADMIN", accessClaims.get("role"));
        // 만료일자 다른지/더 길게 생성된게 맞는지 확인
        Assertions.assertTrue(invalidAccessClaims.getExpiration().getTime() < accessClaims.getExpiration().getTime());

        assertEquals(String.valueOf(001L), refreshClaims.get("memberId").toString());
        assertEquals("test@gmail.com", refreshClaims.get("email"));
        assertEquals("ADMIN", refreshClaims.get("role"));
        // 만료일자 다른지/더 길게 생성된게 맞는지 확인
        Assertions.assertTrue(validRefreshClaims.getExpiration().getTime() < refreshClaims.getExpiration().getTime());

        // 4) 유효하지 않은(서명 오류, 형식 불일치) Access 토큰  => 예외처리
        redisUtils.saveToken(validAccessToken,validRefreshToken);
        String tamperedToken = validAccessToken + "error";
        Assertions.assertThrows(AuthenticationCredentialsNotFoundException.class, () -> jwtTokenProvider.reissueToken(tamperedToken));
    }

    /*
        유효한 토큰을 넣었을 때, memberId, email, role 정보가 제대로 추출되는지 확인
        잘못된 토큰을 넣었을 때, 예외 처리나 오류 응답 확인
     */
    @Test
    @DisplayName("Claims 추출 테스트")
    public void parseTokenClaimsTest() {
        // 1) 유효한 토큰인 경우
        String validAccessToken = jwtTokenProvider.generateAccessToken(001L, "test@gmail.com", "ADMIN");
        Claims claims = jwtTokenProvider.parseTokenClaims(validAccessToken);
        assertEquals(String.valueOf(001L), claims.get("memberId").toString());
        assertEquals("test@gmail.com", claims.get("email"));
        assertEquals("ADMIN", claims.get("role"));
        long compareValue = (claims.getExpiration().getTime()) - (30 * 60 * 1000L);
        long issuedAt = Long.parseLong(String.valueOf(claims.getIssuedAt().getTime()));
        assertEquals(compareValue, issuedAt);

        // 2) 유효하지 않은(서명 오류, 형식 불일치)인 경우
        String tamperedToken = validAccessToken + "error";
        Assertions.assertThrows(AuthenticationCredentialsNotFoundException.class, () -> jwtTokenProvider.needsReissueToken(tamperedToken));

    }


}