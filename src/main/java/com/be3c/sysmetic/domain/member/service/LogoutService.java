package com.be3c.sysmetic.domain.member.service;

import jakarta.servlet.http.HttpServletRequest;

public interface LogoutService {
    void logout(HttpServletRequest request);
}
