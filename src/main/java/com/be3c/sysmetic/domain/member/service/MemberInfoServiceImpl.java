package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.dto.MemberPutPasswordRequestDto;
import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.entity.ResetPasswordLog;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.domain.member.repository.ResetPasswordLogRepository;
import com.be3c.sysmetic.global.common.Code;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class MemberInfoServiceImpl implements MemberInfoService {

    private final MemberRepository memberRepository;

    private final ResetPasswordLogRepository resetPasswordLogRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean changePassword(MemberPutPasswordRequestDto memberPutPasswordRequestDto, Long userId, HttpServletRequest request) {
        Member member = memberRepository
                .findByIdAndStatusCode(
                        userId,
                        Code.USING_STATE.getCode())
                .orElseThrow(() -> new EntityNotFoundException("해당 유저를 찾을 수 없습니다."));

        if(passwordEncoder.matches(memberPutPasswordRequestDto.getCurrentPassword(), member.getPassword()) &&
        memberPutPasswordRequestDto.getNewPassword().equals(memberPutPasswordRequestDto.getNewPasswordConfirm())) {
            member.setPassword(passwordEncoder.encode(memberPutPasswordRequestDto.getNewPassword()));

            saveChangePasswordLog(request, member, Code.PASSWORD_CHANGE_SUCCESS.getCode());
        }
        saveChangePasswordLog(request, member, Code.PASSWORD_CHANGE_FAIL.getCode());

        return false;
    }

    private void saveChangePasswordLog(HttpServletRequest request, Member member, String resultCode) {
        ResetPasswordLog passwordLog = ResetPasswordLog.builder()
                .tryIp(request.getHeader("X-FORWARDED-FOR"))
                .member(member)
                .result(resultCode)
                .build();

        resetPasswordLogRepository.save(passwordLog);
    }
}
