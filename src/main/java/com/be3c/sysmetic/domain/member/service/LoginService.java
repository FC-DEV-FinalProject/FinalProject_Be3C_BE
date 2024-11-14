package com.be3c.sysmetic.domain.member.service;

import java.util.Map;

public interface LoginService {
    // 1. DB에서 Email 조회
    String findEmail(String email);

    // 2. 비밀번호 비교
    boolean validatePassword(String email, String password);

    // 3. rememberMe 체크여부에 따른 jwt 토큰 생성 메서드
    Map<String, String> generateTokenBasedOnRememberMe(String email, String rememberMe);
}
