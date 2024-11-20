package com.be3c.sysmetic.domain.member.service;

public interface AccountService {
    // 1. 이메일 반환 메서드
    String findEmail(String name, String phoneNumber);

    // 4. 이메일 확인
    boolean isPresentEmail(String email);

    // 5. 비밀번호 일치 여부 확인
    boolean isPasswordMatch(String password, String rewritePassword);

    // 6. 비밀번호 재설정
    boolean resetPassword(String email, String password);
}
