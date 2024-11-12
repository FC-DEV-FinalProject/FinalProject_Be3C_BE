package com.be3c.sysmetic.domain.member.service;


import com.be3c.sysmetic.domain.member.dto.MemberPutPasswordRequestDto;
import jakarta.servlet.http.HttpServletRequest;

public interface MemberInfoService {
    boolean changePassword(MemberPutPasswordRequestDto memberPutPasswordRequestDto, Long userId, HttpServletRequest request);
}
