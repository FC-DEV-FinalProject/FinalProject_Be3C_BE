package com.be3c.sysmetic.global.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberId = "";
        if(authentication != null)
            memberId = authentication.getName();    // UserDetails의 getUsername() 반환값 -- email
        return Optional.of(memberId);
    }
}