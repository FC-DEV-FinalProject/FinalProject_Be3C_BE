package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.global.config.security.JwtTokenProvider;
import com.be3c.sysmetic.global.util.file.dto.FileReferenceType;
import com.be3c.sysmetic.global.util.file.dto.FileRequestDto;
import com.be3c.sysmetic.global.util.file.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {
    /*
        [로그인 과정]
        로그인 홈페이지로 접속
        로그인 정보 입력(이메일, 비밀번호, 로그인 유지)
        로그인 버튼 클릭 - POST

        [처리해야 하는 것]
        - 이메일과 비밀번호의 형식 확인
        - 존재하는 이메일인지
        - 이메일과 비밀번호가 DB에 저장된 것과 일치하는지
        - 로그인 유지 기능을 사용하는지 (사용X -> Refresh 토큰의 시간이 짧아져야 할 것 같다. ex. 1시간)

        [필요한 메서드]
        1. 이메일 DB 조회 메서드
        2. 비밀번호 DB 조회 메서드
        3. 로그인 유지 기능 체크에 따른 Token 생성 메서드

        [순서]
        1. email, pw 형식 체크
            1-1. 형식 불일치 -> 실패(Error)
            1-1. 형식 일치 -> 2.으로 이동
        2. DB에서 email 조회
            2-1. 존재X -> 실패(Error)
            2-2. 존재O -> 3.으로 이동
        3. DB에서 해당 email의 pw와 비교
            3-1. 일치X -> 실패(Error)
            3-2. 일치O -> 4.으로 이동
        4. rememberMe 여부에 따른 jwt 토큰 생성
            4-1. 체크박스 선택O -> refresh토큰 30일로 발급
            4-2. 체크박스 선택X -> refresh토큰 1시간으로 발급
        5. jwt 전달
            5-1. access 토큰은 response에 담아서 클라이언트에게 전달
            5.2. refresh 토큰은 Redis에 저장
     */
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    private final JwtTokenProvider jwtTokenProvider;
    private final FileService fileService;

    // 1. DB에서 Email 조회
    @Override
    public String findEmail(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.info("존재하지 않는 이메일");
                    return new UsernameNotFoundException("이메일 또는 비밀번호가 일치하지 않습니다");
                });
        return member.getEmail();
    }

    // 2. 비밀번호 비교
    @Override
    public boolean validatePassword(String email, String password) {
        // DB에 저장된 pw 조회
        Member member = memberRepository.findByEmail(email)
                // 비교
                .orElseThrow(() -> {
                    log.info("비밀번호 불일치");
                    return new UsernameNotFoundException("이메일 또는 비밀번호가 일치하지 않습니다");
                });
        return bCryptPasswordEncoder.matches(password, member.getPassword());
    }

    // 3. rememberMe 체크여부에 따른 jwt 토큰 생성 메서드
    @Override
    public Map<String, String> generateTokenBasedOnRememberMe(String email, Boolean rememberMe) {
        // 회원 정보 가져오기
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.info("입력한 이메일로 저장된 회원정보가 존재하지 않습니다.");
                    return new UsernameNotFoundException("이메일 또는 비밀번호가 일치하지 않습니다");
                });

        String memberProfileImage = null;
        try {
            memberProfileImage = fileService.getFilePath(new FileRequestDto(FileReferenceType.MEMBER, member.getId()));
        } catch (Exception e) {
            log.info("파일 이미지 가져오기 에러 발생");
        }

        // 토큰 생성
        String accessToken = jwtTokenProvider.generateAccessToken(member.getId(), member.getEmail(), member.getRoleCode());
        String refreshToken = null;
        if(rememberMe) {
            refreshToken = jwtTokenProvider.generateMonthRefreshToken(member.getId(), member.getEmail(), member.getRoleCode());
        } else {
            refreshToken = jwtTokenProvider.generateHourRefreshToken(member.getId(), member.getEmail(), member.getRoleCode());
        }

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("accessToken", accessToken);
        tokenMap.put("refreshToken", refreshToken);

        return tokenMap;
    }

}
