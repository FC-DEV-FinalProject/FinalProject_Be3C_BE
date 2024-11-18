package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.dto.FolderPostRequestDto;
import com.be3c.sysmetic.domain.member.dto.FollowDeleteRequestDto;
import com.be3c.sysmetic.domain.member.dto.FollowPostRequestDto;
import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.repository.FolderRepository;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.domain.strategy.entity.Method;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.entity.StrategyStatistics;
import com.be3c.sysmetic.domain.strategy.repository.MethodRepository;
import com.be3c.sysmetic.domain.strategy.repository.StrategyRepository;
import com.be3c.sysmetic.domain.strategy.repository.StrategyStatisticsRepository;
import com.be3c.sysmetic.global.common.Code;
import com.be3c.sysmetic.global.config.security.CustomUserDetails;
import com.be3c.sysmetic.global.util.SecurityUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(locations = "/application-test.properties")
@Slf4j
@SpringBootTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InterestStrategyServiceTest {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private FolderService folderService;

    @Autowired
    private InterestStrategyService interestStrategyService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MethodRepository methodRepository;

    @Autowired
    private StrategyRepository strategyRepository;

    @Autowired
    private StrategyStatisticsRepository strategyStatisticsRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Autowired
    EntityManager entityManager;
    @Autowired
    private SecurityUtils securityUtils;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();
        folderRepository.deleteAll();
        strategyStatisticsRepository.deleteAll();
        strategyRepository.deleteAll();
        methodRepository.deleteAll();

        entityManager.createNativeQuery("ALTER TABLE member AUTO_INCREMENT = 1")
                .executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE folder AUTO_INCREMENT = 1")
                .executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE strategy AUTO_INCREMENT = 1")
                .executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE strategy_statistics AUTO_INCREMENT = 1")
                .executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE method AUTO_INCREMENT = 1")
                .executeUpdate();


        Member member = Member.builder()
                .email("test@test.com")
                .password(bCryptPasswordEncoder.encode("encodedPassword"))
                // 초기값 설정
                .id(1L)
                .roleCode("USER")
                .name("테스트")
                .nickname("테스트")
                .phoneNumber("01012341234")
                .usingStatusCode("US001")
                .totalFollow(0)
                .totalStrategyCount(0)
                .receiveInfoConsent("Y")
                .infoConsentDate(LocalDateTime.now())
                .receiveMarketingConsent("Y")
                .marketingConsentDate(LocalDateTime.now())
                .build();

        Method method = Method.builder()
                .name("테스트매매유형")
                .statusCode(Code.USING_STATE.getCode())
                .build();

        Strategy strategy = Strategy.builder()
                .name("테스트전략")
                .trader(member)
                .content("설명")
                .method(method)
                .statusCode(Code.USING_STATE.getCode())
                .cycle('D')
                .build();

        StrategyStatistics strategyStatistics = StrategyStatistics.builder()
                .strategy(strategy)
                .currentBalance(1000000.0)
                .principal(500000.0)
                .accumulatedDepositWithdrawalAmount(200000.0)
                .accumulatedProfitLossAmount(150000.0)
                .accumulatedProfitLossRate(30.0)
                .maximumAccumulatedProfitLossAmount(200000.0)
                .maximumAccumulatedProfitLossRate(40.0)
                .currentCapitalReductionAmount(50000.0)
                .currentCapitalReductionRate(5.0)
                .maximumCapitalReductionAmount(100000.0)
                .maximumCapitalReductionRate(10.0)
                .averageProfitLossAmount(2000.0)
                .averageProfitLossRate(1.0)
                .maximumDailyProfitAmount(5000.0)
                .maximumDailyProfitRate(2.0)
                .maximumDailyLossAmount(-3000.0)
                .maximumDailyLossRate(-1.5)
                .totalTradingDays(100L)
                .currentContinuousProfitLossDays(5L)
                .totalProfitDays(60L)
                .maximumContinuousProfitDays(10L)
                .totalLossDays(40L)
                .maximumContinuousLossDays(8L)
                .winningRate(60.0)
                .highPointRenewalProgress(30L)
                .profitFactor(1.5)
                .roa(0.08)
                .firstRegistrationDate(LocalDateTime.now().minusMonths(6))
                .lastRegistrationDate(LocalDateTime.now())
                .build();

        methodRepository.save(method);
        memberRepository.save(member);
        strategyRepository.save(strategy);
        strategyStatisticsRepository.save(strategyStatistics);

        // 권한 설정
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        // CustomUserDetails 생성
        CustomUserDetails userDetails = new CustomUserDetails(
                1L, // memberId
                "test@example.com", // email
                "USER", // role
                authorities // 권한 목록
        );

        // Authentication 객체 생성
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // SecurityContext에 Authentication 객체 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @DisplayName("관심 전략 추가 테스트 - 성공")
    @Order(1)
    public void testInsertInterestStrategy() {
        Long userId = securityUtils.getUserIdInSecurityContext();

        folderService.insertFolder(FolderPostRequestDto.builder()
                .name("테스트폴더")
                .CheckDupl(true)
                .build());

        assertEquals(
                1,
                folderRepository.findByMemberIdAndStatusCode(
                        userId,
                        Code.USING_STATE.getCode()).size()
        );

        interestStrategyService.follow(FollowPostRequestDto.builder()
                        .strategyId(1L)
                        .folderId(1L)
                        .build()
        );
    }

    @Test
    @DisplayName("관심 전략 추가 테스트 - 실패 : 존재하지 않는 전략, 관심 전략 추가 시도")
    @Order(2)
    public void testInsertInterestStrategyNotExist() {
        Long userId = securityUtils.getUserIdInSecurityContext();

        folderService.insertFolder(FolderPostRequestDto.builder()
                .name("테스트폴더")
                .CheckDupl(true)
                .build());

        assertEquals(
                1,
                folderRepository.findByMemberIdAndStatusCode(
                        userId,
                        Code.USING_STATE.getCode()).size()
        );

        assertThrows(EntityNotFoundException.class, () -> {
            interestStrategyService.follow(FollowPostRequestDto.builder()
                    .strategyId(2L)
                    .folderId(1L)
                    .build()
            );
        });
    }

    @Test
    @DisplayName("관심 전략 추가 테스트 - 실패 : 이미 추가한 관심 전략, 추가 시도")
    @Order(3)
    public void testInsertInterestStrategyExist() {
        Long userId = securityUtils.getUserIdInSecurityContext();

        folderService.insertFolder(FolderPostRequestDto.builder()
                .name("테스트폴더")
                .CheckDupl(true)
                .build());

        assertEquals(
                1,
                folderRepository.findByMemberIdAndStatusCode(
                        userId,
                        Code.USING_STATE.getCode()).size()
        );

        interestStrategyService.follow(FollowPostRequestDto.builder()
                .strategyId(1L)
                .folderId(1L)
                .build()
        );

        assertThrows(IllegalArgumentException.class, () -> {
            interestStrategyService.follow(FollowPostRequestDto.builder()
                    .strategyId(1L)
                    .folderId(1L)
                    .build()
            );
        });
    }

    @Test
    @DisplayName("관심 전략 추가 테스트 - 실패 : 관심 전략 추가 폴더 입력 실수")
    @Order(4)
    public void testInsertInterestStrategyExistUnFollowed() {
        Long userId = securityUtils.getUserIdInSecurityContext();

        folderService.insertFolder(FolderPostRequestDto.builder()
                .name("테스트폴더")
                .CheckDupl(true)
                .build());

        assertEquals(
                1,
                folderRepository.findByMemberIdAndStatusCode(
                        userId,
                        Code.USING_STATE.getCode()).size()
        );

        assertThrows(EntityNotFoundException.class, () -> {
            interestStrategyService.follow(FollowPostRequestDto.builder()
                    .strategyId(1L)
                    .folderId(2L)
                    .build()
            );
        });
    }

    @Test
    @DisplayName("관심 전략 삭제 테스트 - 성공")
    @Order(5)
    public void testDeleteInterestStrategy() {
        Long userId = securityUtils.getUserIdInSecurityContext();

        folderService.insertFolder(FolderPostRequestDto.builder()
                .name("테스트폴더")
                .CheckDupl(true)
                .build());

        assertEquals(
                1,
                folderRepository.findByMemberIdAndStatusCode(
                        userId,
                        Code.USING_STATE.getCode()).size()
        );

        interestStrategyService.follow(FollowPostRequestDto.builder()
                .strategyId(1L)
                .folderId(1L)
                .build()
        );

        ArrayList<Long> unfollowList = new ArrayList<>();

        unfollowList.add(1L);

        FollowDeleteRequestDto followDeleteRequestDto = FollowDeleteRequestDto.builder()
                .strategyId(unfollowList)
                .build();

        interestStrategyService.unfollow(followDeleteRequestDto);
    }

    @Test
    @DisplayName("관심 전략 추가 테스트 - 성공 : 관심 전략 삭제 후 다시 추가 시도")
    @Order(5)
    public void testInsertInterestStrategyDeleted() {
        Long userId = securityUtils.getUserIdInSecurityContext();

        folderService.insertFolder(FolderPostRequestDto.builder()
                .name("테스트폴더")
                .CheckDupl(true)
                .build());

        assertEquals(
                1,
                folderRepository.findByMemberIdAndStatusCode(
                        userId,
                        Code.USING_STATE.getCode()).size()
        );

        interestStrategyService.follow(FollowPostRequestDto.builder()
                .strategyId(1L)
                .folderId(1L)
                .build()
        );

        ArrayList<Long> unfollowList = new ArrayList<>();

        unfollowList.add(1L);

        FollowDeleteRequestDto followDeleteRequestDto = FollowDeleteRequestDto.builder()
                .strategyId(unfollowList)
                .build();

        interestStrategyService.unfollow(followDeleteRequestDto);

        interestStrategyService.follow(FollowPostRequestDto.builder()
                        .folderId(1L)
                        .strategyId(1L)
                        .build()
        );
    }
}
