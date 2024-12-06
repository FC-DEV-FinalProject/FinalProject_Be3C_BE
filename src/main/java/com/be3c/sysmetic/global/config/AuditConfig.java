package com.be3c.sysmetic.global.config;

import com.be3c.sysmetic.global.config.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
@RequiredArgsConstructor
public class AuditConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public AuditorAware<String> auditorProvider(){
        return new AuditorAwareImpl(jwtTokenProvider);
    }
}
