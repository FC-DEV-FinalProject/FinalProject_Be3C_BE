package com.be3c.sysmetic.global.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@EnableWebSecurity
@Configuration
public class SecurityConfig {
    // permitAll 에 해당되는 Url 배열
    private final String[] permitAllUrls = {
            "/",
            "/v1",
            "/**",
            "/v1/**",
            "/error",
            "/swagger",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/api-docs",
            "/api-docs/**",
            "/v3/api-docs/**"
    };

    private final JwtTokenProvider jwtTokenProvider;

    // 단계별 역할 부여 (권한이 점차 넓어지는 방식)
    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy(
                "ROLE_ADMIN > ROLE_MANAGER\n" +
                        "ROLE_MANAGER > ROLE_TRADER\n" +
                        "ROLE_TRADER > ROLE_USER"
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*"); // 모든 출처 허용
        configuration.addAllowedMethod("*"); // 모든 HTTP 메서드 허용
        configuration.addAllowedHeader("*"); // 모든 헤더 허용
        configuration.addExposedHeader("Authorization");
//        configuration.setExposedHeaders(List.of("Authorization", "Authorization-refresh")); // 인증 정보 포함 허용
        configuration.setAllowCredentials(true); // 자격 증명 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 적용
        return source;
    }

    // 보안 설정
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // REST API, JWT 사용을 위해 csrf / basic auth / formLogin 비활성화
                .csrf(csrf -> csrf.disable())
                .httpBasic(httpbasic -> httpbasic.disable())
                .formLogin(formLogin -> formLogin.disable())

                // session 생성 정책 - JWT를 사용하기 때문에 세션을 사용하지 않음
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // CORS 설정 추가
                .cors(cors ->
                        cors.configurationSource(corsConfigurationSource())
                )

                // HTTP 요청에 대한 역할별 URL 접근 권한 부여
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers(permitAllUrls).permitAll()
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .requestMatchers("/manager/**").hasRole("MANAGER")
                                .requestMatchers("/trader/**").hasRole("TRADER")
                                .anyRequest().authenticated()
                )

                // 예외 처리
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                )

                // JWT 인증 필터 등록
                .addFilterBefore(JwtAuthenticationFilter.builder().jwtTokenProvider(jwtTokenProvider).build(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
