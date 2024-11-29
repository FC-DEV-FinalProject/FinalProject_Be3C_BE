package com.be3c.sysmetic.domain.member.service;

import java.util.Map;

public interface LoginService {
    String findEmail(String email);

    boolean validatePassword(String email, String password);

    Map<String, String> generateTokenBasedOnRememberMe(String email, Boolean rememberMe);
}
