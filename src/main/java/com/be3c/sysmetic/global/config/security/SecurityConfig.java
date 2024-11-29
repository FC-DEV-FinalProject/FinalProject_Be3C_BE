package com.be3c.sysmetic.global.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@EnableWebSecurity
@Configuration
public class SecurityConfig {
//    // 허용할 url 이 많아지면 사용하려고 작성해둔 String 배열
//    private final String[] allowedUrls = {"/","/login", "/register"};

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

    // 보안 설정
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Spring security의 버전이 6으로 올라가면서, 기존에 있던 method chaining 방식은 deprecated되고, lambda 방식이 새로 들어왔다.
        http
                // REST API, JWT 사용을 위해 csrf / basic auth / formLogin 비활성화
                .csrf(csrf -> csrf.disable())
                .httpBasic(httpbasic -> httpbasic.disable())
                .formLogin(formLogin -> formLogin.disable())

                // session 생성 정책 - JWT를 사용하기 때문에 세션을 사용하지 않음
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

//                .cors(cors ->
//                        cors.configure())

                // HTTP 요청에 대한 역할별 URL 접근 권한 부여
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers("/swagger", "/swagger-ui.html", "/swagger-ui/**", "/api-docs", "/api-docs/**", "/v3/api-docs/**")
                                .permitAll()
                                .requestMatchers("/", "/**", "/auth/login", "/auth/register", "/error").permitAll()
                                .requestMatchers("/admin").hasRole("ADMIN")
                                .requestMatchers("/manager").hasRole("MANAGER")
                                .requestMatchers("/trader").hasRole("TRADER")
                                .anyRequest().authenticated()
                )
                // 예외 처리
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                )

                // JWT 인증 필터 등록
                .addFilterBefore(JwtAuthenticationFilter.builder().jwtTokenProvider(jwtTokenProvider).build(), UsernamePasswordAuthenticationFilter.class);

        /*
            [완료]
            passwordEncoder
             -> 로그인 시 사용 예정
            가장 중요한 것 = jwtHandler OR jwtFilter / dispacherservlet이 메서드를 찾기 전에 hasRole. jwt를 스프링 시큐리티에게 언제 전달해주는지. (jwt - filter or handler)
             -> REST API에서 클라이언트의 모든 요청이 JWT를 통해 인증할 때 Filter를 사용하는 것이 적합하다고 판단.

            [진행중]
            jwtHandler 설정
            포트설정(http -> https로 redirect 해주는 기능 찾아보기)
            roleHieraachy 찾아보기. 역할 분류되는 방식이 다양하다.


            [선택 개발]
            메서드 제한(put/post/...등 제외한 메서드로는 제한) - 시큐리티로 막을 수 있다.
         */
        return http.build();
    }


}
