package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.dto.MemberPatchConsentRequestDto;
import com.be3c.sysmetic.domain.member.dto.MemberPatchInfoRequestDto;
import com.be3c.sysmetic.domain.member.dto.MemberPutPasswordRequestDto;
import com.be3c.sysmetic.domain.member.entity.Folder;
import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.entity.ResetPasswordLog;
import com.be3c.sysmetic.domain.member.repository.*;
import com.be3c.sysmetic.domain.strategy.entity.Reply;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.entity.StrategyStatistics;
import com.be3c.sysmetic.domain.strategy.repository.*;
import com.be3c.sysmetic.global.common.Code;
import com.be3c.sysmetic.global.util.SecurityUtils;
import com.be3c.sysmetic.global.util.email.dto.Subscriber;
import com.be3c.sysmetic.global.util.email.dto.SubscriberRequest;
import com.be3c.sysmetic.global.util.email.service.EmailService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class MemberInfoServiceImpl implements MemberInfoService {

    private final SecurityUtils securityUtils;

    private final EmailService emailService;

    private final MemberRepository memberRepository;

    private final ResetPasswordLogRepository resetPasswordLogRepository;

    private final MonthlyRepository monthlyRepository;

    private final DailyRepository dailyRepository;

    private final StrategyRepository strategyRepository;

    private final InterestStrategyRepository interestStrategyRepository;

    private final FolderRepository folderRepository;

    private final InquiryAnswerRepository inquiryAnswerRepository;

    private final InquiryRepository inquiryRepository;

    private final ResetPasswordLogRepository resetpasswordLogRepository;

    private final AccountImageRepository accountImageRepository;

    private final ReplyRepository replyRepository;

    private final StrategyApprovalRepository strategyApprovalRepository;

    private final StrategyStatisticsRepository strategyStatisticsRepository;

    private final StrategyStockReferenceRepository strategyStockReferenceRepository;

    private final PasswordEncoder passwordEncoder;

    private final InterestStrategyLogRepository interestStrategyLogRepository;

    @Override
    public boolean changePassword(Long userId, MemberPutPasswordRequestDto memberPutPasswordRequestDto, HttpServletRequest request) {
        Long requestId = securityUtils.getUserIdInSecurityContext();

        if(!(memberPutPasswordRequestDto.getUserId().equals(userId) || userId.equals(requestId))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        Member member = findMemberById(userId);

        if(
            // 현재 비밀번호가 제대로 입력되었는지 확인
            passwordEncoder
                .matches(
                        memberPutPasswordRequestDto.getCurrentPassword(),
                        member.getPassword()
                ) &&
            // 비밀번호 확인과 비밀번호가 일치하는지 확인.
            memberPutPasswordRequestDto
                    .getNewPassword()
                    .equals(memberPutPasswordRequestDto
                            .getNewPasswordConfirm())
        ) {
            member.setPassword(passwordEncoder.encode(memberPutPasswordRequestDto.getNewPassword()));

            saveChangePasswordLog(request, member, Code.PASSWORD_CHANGE_SUCCESS.getCode());

            return true;
        }
        saveChangePasswordLog(request, member, Code.PASSWORD_CHANGE_FAIL.getCode());

        return false;
    }

    @Override
    public boolean changeMemberInfo(Long userId, MemberPatchInfoRequestDto memberPatchInfoRequestDto) {
        Long requestId = securityUtils.getUserIdInSecurityContext();

        if(!(memberPatchInfoRequestDto.getUserId().equals(userId) || userId.equals(requestId))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        Member member = findMemberById(userId);

        if(memberPatchInfoRequestDto.getNicknameDuplCheck() &&
            memberPatchInfoRequestDto.getNickname() != null &&
            memberPatchInfoRequestDto.getNickname().isEmpty()
        ) {
            member.setNickname(memberPatchInfoRequestDto.getNickname());

            List<Subscriber> subscribers = new ArrayList<>();

            subscribers.add(Subscriber.builder()
                            .email(member.getEmail())
                            .name(memberPatchInfoRequestDto.getNickname())
                    .build());

            switch (member.getRoleCode()) {
                case "USER":
                    emailService.updateUserSubscriberRequest(SubscriberRequest.builder()
                            .subscribers(subscribers)
                            .build());
                    break;
                case "TRADER":
                    emailService.updateTraderSubscriberRequest(SubscriberRequest.builder()
                            .subscribers(subscribers)
                            .build());
                    break;
            }

        }

        if(!memberPatchInfoRequestDto.getPhoneNumber().isEmpty()) {
            member.setPhoneNumber(memberPatchInfoRequestDto.getPhoneNumber());
        }

        memberRepository.save(member);

        return true;
    }

    @Override
    public boolean changeMemberConsent(Long userId, MemberPatchConsentRequestDto memberPatchInfoRequestDto) {
        return false;
    }

    @Override
    public boolean deleteUser(Long userId) throws AuthenticationCredentialsNotFoundException {
        Long requestId = securityUtils.getUserIdInSecurityContext();

        if(!requestId.equals(userId)) {
            throw new AuthenticationCredentialsNotFoundException("");
        }

        Member member = findMemberById(userId);

        deleteUser(member);

        return true;
    }

    private void deleteUser(Member member) {
        List<Strategy> strategyList = strategyRepository.findByTraderId(member.getId());
        List<Folder> folderList = folderRepository.findByMemberId(member.getId());

        replyRepository.deleteByMemberId(member.getId());

        // strategy 삭제
        strategyList.forEach(strategy -> {
            strategyStatisticsRepository.deleteByStrategyId(strategy.getId());
            dailyRepository.deleteByStrategyId(strategy.getId());
            monthlyRepository.deleteByStrategyId(strategy.getId());
            strategyStockReferenceRepository.deleteByStrategyId(strategy.getId());
            strategyApprovalRepository.deleteByStrategyId(strategy.getId());
            accountImageRepository.deleteByStrategyId(strategy.getId());
            inquiryAnswerRepository.deleteByStrategyId(strategy.getId());
            inquiryRepository.deleteByStrategyId(strategy.getId());
        });

        strategyRepository.deleteByMemberId(member.getId());

        folderList.forEach(folder -> {
            folder.getInterestStrategies().forEach(interestStrategy -> {
                interestStrategyLogRepository.deleteByInterestStrategyId(interestStrategy.getId());
            });
            interestStrategyRepository.deleteByFolderId(folder.getId());
        });

        folderRepository.deleteByMemberId(member.getId());

        memberRepository.delete(member);
    }

    private void saveChangePasswordLog(HttpServletRequest request, Member member, String resultCode) {
        ResetPasswordLog passwordLog = ResetPasswordLog.builder()
                .tryIp(request.getHeader("X-FORWARDED-FOR"))
                .member(member)
                .build();

        resetPasswordLogRepository.save(passwordLog);
    }

    private Member findMemberById(Long userId) {
        return memberRepository
                .findByIdAndUsingStatusCode(
                        userId,
                        Code.USING_STATE.getCode())
                .orElseThrow(() -> new EntityNotFoundException("해당 유저를 찾을 수 없습니다."));
    }
}
