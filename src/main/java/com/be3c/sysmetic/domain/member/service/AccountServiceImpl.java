package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    /*

        [이메일 찾기 API]
        1. 이메일 반환 메서드 (이름+휴대번호로 DB에 조회 후 이메일 반환)
        2. Log 기록 메서드   // todo: 2차 개발 고려 (FindEmailLog 구현 시 사용)
        3. IP 추출 메서드    // todo: 2차 개발 고려 (FindEmailLog 구현 시 사용)

        [비밀번호 재설정 API]
        1. 이메일 확인 및 인증코드 발송
        2. 이메일 인증코드 확인
        3. 비밀번호 재설정
     */
    private final MemberRepository memberRepository;

    // 1. 이메일 반환 메서드
    @Override
    public String findEmail(String name, String phoneNumber) {
        // 이름+휴대번호로 DB 조회 후 회원정보가 있으면 이메일 반환
        return memberRepository.findEmailByNameAndPhoneNumber(name, phoneNumber);
    }



}
