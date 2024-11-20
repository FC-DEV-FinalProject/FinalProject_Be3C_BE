package com.be3c.sysmetic.domain.member.service;

public interface AccountService {
    // 1. 이메일 반환 메서드
    String findEmail(String name, String phoneNumber);
}
