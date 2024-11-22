package com.be3c.sysmetic.domain.member.controller;

import org.springframework.stereotype.Controller;

@Controller
public class LogoutController {
    /*
        [로그아웃시 해야 하는 것]
        - 만료된 토큰 전달
        - 기존 토큰 정보 Redis 에서 삭제
        - SecurityContext에서 회원정보 삭제


     */
}
