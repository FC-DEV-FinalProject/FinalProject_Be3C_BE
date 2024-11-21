package com.be3c.sysmetic.domain.member.service;


import com.be3c.sysmetic.domain.member.dto.MemberPatchInfoRequestDto;
import com.be3c.sysmetic.domain.member.dto.MemberPutPasswordRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

public interface MemberInfoService {
    boolean changePassword(MemberPutPasswordRequestDto memberPutPasswordRequestDto, HttpServletRequest request);
    boolean changeMemberInfo(MemberPatchInfoRequestDto memberPatchInfoRequestDto);
    boolean deleteUser(Long userId, HttpServletRequest request) throws AuthenticationCredentialsNotFoundException;
}
