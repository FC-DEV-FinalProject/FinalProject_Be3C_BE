//package com.be3c.sysmetic.domain.member.service;
//
//import com.be3c.sysmetic.domain.member.dto.RegisterRequestDto;
//import org.springframework.transaction.annotation.Transactional;
//
//public interface RegisterService {
//    // 1. 이메일 중복확인
//    boolean checkEmailDuplication(String email);
//
//    // 2. 이메일 인증코드 발송 및 저장
//    @Transactional
//    boolean sendVerifyEmailCode(String email);
//
//    // 3. 이메일 인증코드 확인
//    boolean checkVerifyEmailCode(String email, String inputEmailCode);
//
//    // 4. 닉네임 중복확인
//    boolean checkNicknameDuplication(String nickname);
//
//    // 5. 회원가입
//    @Transactional
//    boolean registerMember(RegisterRequestDto dto);
//}
