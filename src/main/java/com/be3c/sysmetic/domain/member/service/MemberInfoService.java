package com.be3c.sysmetic.domain.member.service;


import com.be3c.sysmetic.domain.member.dto.MemberPatchConsentRequestDto;
import com.be3c.sysmetic.domain.member.dto.MemberPatchInfoRequestDto;
import com.be3c.sysmetic.domain.member.dto.MemberPutPasswordRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.multipart.MultipartFile;

public interface MemberInfoService {
    boolean changePassword(Long userId, MemberPutPasswordRequestDto memberPutPasswordRequestDto, HttpServletRequest request);
    boolean changeMemberInfo(Long userId, MemberPatchInfoRequestDto memberPatchInfoRequestDto, MultipartFile file);
    boolean deleteUser(Long userId) throws AuthenticationCredentialsNotFoundException;
    boolean changeMemberConsent(Long userId, MemberPatchConsentRequestDto memberPatchInfoRequestDto);
}
