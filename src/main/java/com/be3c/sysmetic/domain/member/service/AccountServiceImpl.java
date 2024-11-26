package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    /*
        [이메일 찾기 API 메서드]
        1. 이메일 반환 메서드 (이름+휴대번호로 DB에 조회 후 이메일 반환)
        2. Log 기록 메서드   // todo: 2차 개발 고려 (FindEmailLog 구현 시 사용)
        3. IP 추출 메서드    // todo: 2차 개발 고려 (FindEmailLog 구현 시 사용)

        [비밀번호 재설정 API]
        4. 이메일 확인
        0. 이메일 인증코드 발송 및 저장 - RegisterServiceImpl 메서드 사용
        0. 이메일 인증코드 확인 - RegisterServiceImpl 메서드 사용
        5. 비밀번호 일치 여부 확인
        6. 비밀번호 재설정
     */
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    // 1. 이메일 반환 메서드
    @Override
    public String findEmail(String name, String phoneNumber) {
        // 이름+휴대번호로 DB 조회 후 회원정보가 있으면 이메일 반환
        List<String> emailList = memberRepository.findEmailByNameAndPhoneNumber(name, phoneNumber);
        if(emailList == null || emailList.isEmpty()) {
            throw new EntityNotFoundException("일치하는 회원 정보를 찾을 수 없습니다.");
        }

        return String.join(", ", emailList);
    }


    // 4. 이메일 확인
    public boolean isPresentEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    // 5. 비밀번호 일치 여부 확인
    public boolean isPasswordMatch(String password, String rewritePassword) {
        return Objects.equals(password, rewritePassword);
    }

    // 6. 비밀번호 재설정
    @Transactional
    public boolean resetPassword(String email, String password) {
        String encodedPassword = bCryptPasswordEncoder.encode(password);
        int updatedRows = memberRepository.updatePasswordByEmail(email, encodedPassword);
        return updatedRows == 1;
    }

}
