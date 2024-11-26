package com.be3c.sysmetic.global.util;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private Long userId; // 사용자 ID(PK)
    private String username; // 사용자 이름 또는 로그인 ID
    private String password; // 비밀번호
    private Collection<? extends GrantedAuthority> authorities; // 사용자 권한

    // 필요한 추가 정보들
    private boolean isAccountNonExpired;
    private boolean isAccountNonLocked;
    private boolean isCredentialsNonExpired;
    private boolean isEnabled;

    // 생성자
    public CustomUserDetails(Long userId, String username, String password,
                             Collection<? extends GrantedAuthority> authorities,
                             boolean isAccountNonExpired, boolean isAccountNonLocked,
                             boolean isCredentialsNonExpired, boolean isEnabled) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.isAccountNonExpired = isAccountNonExpired;
        this.isAccountNonLocked = isAccountNonLocked;
        this.isCredentialsNonExpired = isCredentialsNonExpired;
        this.isEnabled = isEnabled;
    }

    // UserDetails 인터페이스의 메서드 구현
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    // 사용자 ID(PK)를 반환하는 메서드
    public Long getUserId() {
        return userId;
    }
}

