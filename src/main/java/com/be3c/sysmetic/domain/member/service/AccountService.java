package com.be3c.sysmetic.domain.member.service;

public interface AccountService {
    String findEmail(String name, String phoneNumber);

    boolean isPresentEmail(String email);

    boolean isPasswordMatch(String password, String rewritePassword);

    boolean resetPassword(String email, String password, String rewritePassword);
}
