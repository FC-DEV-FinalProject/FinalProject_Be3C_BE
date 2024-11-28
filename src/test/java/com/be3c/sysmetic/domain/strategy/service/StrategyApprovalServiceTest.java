package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.member.entity.InterestStrategy;
import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.repository.FolderRepository;
import com.be3c.sysmetic.domain.member.repository.InterestStrategyRepository;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.domain.strategy.dto.AdminStrategyGetResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.AdminStrategySearchGetDto;
import com.be3c.sysmetic.domain.strategy.dto.AllowApprovalRequestDto;
import com.be3c.sysmetic.domain.strategy.entity.Method;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.entity.StrategyApprovalHistory;
import com.be3c.sysmetic.domain.strategy.entity.StrategyStatistics;
import com.be3c.sysmetic.domain.strategy.repository.*;
import com.be3c.sysmetic.global.common.Code;
import com.be3c.sysmetic.global.common.response.PageResponse;
import com.be3c.sysmetic.global.config.security.CustomUserDetails;
import com.be3c.sysmetic.global.util.SecurityUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@Slf4j
@SpringBootTest
@Transactional
public class StrategyApprovalServiceTest {

    private final AdminStrategyService adminStrategyService;

    private final StrategyApprovalRepository strategyApprovalRepository;

    private final MemberRepository memberRepository;

    private final FolderRepository folderRepository;

    private final InterestStrategyRepository interestStrategyRepository;

    private final MethodRepository methodRepository;

    private final StrategyRepository strategyRepository;

    private final DailyRepository dailyRepository;

    private final EntityManager entityManager;

    private final StrategyStockReferenceRepository strategyStockReferenceRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final StrategyStatisticsRepository strategyStatisticsRepository;
    @Autowired
    private SecurityUtils securityUtils;

    @BeforeEach
    public void setUp() {
        strategyStockReferenceRepository.deleteAll();
        interestStrategyRepository.deleteAll();
        folderRepository.deleteAll();
        strategyRepository.deleteAll();
        methodRepository.deleteAll();
        dailyRepository.deleteAll();
        strategyApprovalRepository.deleteAll();
        memberRepository.deleteAll();

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

        entityManager.clear();

        Member member = Member.builder()
                .email("test@test.com")
                .password(bCryptPasswordEncoder.encode("encodedPassword"))
                // 초기값 설정
                .roleCode("TRADER")
                .name("테스트")
                .nickname("테스트")
                .birth(LocalDate.of(2000,1,1))
                .phoneNumber("01012341234")
                .usingStatusCode("US001")
                .totalFollow(0)
                .totalStrategyCount(0)
                .receiveInfoConsent("Y")
                .infoConsentDate(LocalDateTime.now())
                .receiveMarketingConsent("Y")
                .marketingConsentDate(LocalDateTime.now())
                .build();

        memberRepository.save(member);

        Method method = Method.builder()
                .name("테스트매매유형")
                .statusCode(Code.USING_STATE.getCode())
                .build();

        methodRepository.save(method);

        List<Strategy> strategyList = new ArrayList<>();

        for(int i = 1; i <= 20; i++) {
            strategyList.add(Strategy.builder()
                    .name("테스트전략" + i)
                    .trader(member)
                    .content("설명" + i)
                    .method(method)
                    .statusCode(Code.OPEN_STRATEGY.getCode())
                    .cycle('D')
                    .build());
        }

        for (Strategy strategy : strategyList) {
            assertNotNull(strategy.getTrader(), "Strategy의 trader가 null입니다.");
            assertNotNull(strategy.getTrader().getId(), "Strategy의 trader에 저장된 Member의 ID가 null입니다.");
        }

        strategyRepository.saveAll(strategyList);

        StrategyStatistics strategyStatistics = StrategyStatistics.builder()
                .strategy(strategyList.get(1))
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
                .firstRegistrationDate(LocalDate.now())
                .lastRegistrationDate(LocalDate.now())
                .build();

        strategyStatisticsRepository.save(strategyStatistics);

        // 권한 설정
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        // CustomUserDetails 생성
        CustomUserDetails userDetails = new CustomUserDetails(
                1L, // memberId
                "test@example.com", // email
                "TRADER", // role
                authorities // 권한 목록
        );

        // Authentication 객체 생성
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // SecurityContext에 Authentication 객체 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @DisplayName("전략 관리 페이징 : 성공 - 페이징 테스트")
    @Order(1)
    public void imsleepy() {
        PageResponse<AdminStrategyGetResponseDto> pageResponse =
                adminStrategyService
                        .findStrategyPage(AdminStrategySearchGetDto.builder()
                                .page(1)
                                .build()
                );

        AtomicInteger expectedId = new AtomicInteger(1);

        pageResponse.getContent().forEach(dto ->
                assertEquals(expectedId.getAndIncrement(), dto.getStrategyId())
        );
    }

    @Test
    @DisplayName("전략 관리 페이징 : 실패 - 빈 페이지 호출")
    @Order(2)
    public void iwassleepy() {
        assertThrows(NoSuchElementException.class, () ->
                adminStrategyService
                        .findStrategyPage(AdminStrategySearchGetDto.builder()
                        .page(3)
                        .build()
                )
        );
    }

    @Test
    @DisplayName("전략 관리 페이징 : 성공 - 여러 개의 관심 전략 승인 목록")
    @Order(3)
    public void imtired() {
        Long userId = securityUtils.getUserIdInSecurityContext();
        List<StrategyApprovalHistory> approvalHistoryList = new ArrayList<>();

        for(int i = 1; i <= 3; i++) {
            StrategyApprovalHistory approvalHistory = StrategyApprovalHistory.builder()
                    .manager(memberRepository.findById(userId).orElseThrow(EntityNotFoundException::new))
                    .strategy(strategyRepository.findById((long) i).orElseThrow(EntityNotFoundException::new))
                    .statusCode(Code.APPROVE_WAIT.getCode())
                    .build();

            approvalHistoryList.add(approvalHistory);
            strategyApprovalRepository.save(approvalHistory);
        }

        for(int i = 2; i <= 6; i++) {
            StrategyApprovalHistory approvalHistory = StrategyApprovalHistory.builder()
                    .manager(memberRepository.findById(userId).orElseThrow(EntityNotFoundException::new))
                    .strategy(strategyRepository.findById((long) i).orElseThrow(EntityNotFoundException::new))
                    .statusCode(Code.APPROVE_SUCCESS.getCode())
                    .build();

            approvalHistoryList.add(approvalHistory);
            strategyApprovalRepository.save(approvalHistory);
        }

        for(int i = 4; i <= 9; i++) {
            StrategyApprovalHistory approvalHistory = StrategyApprovalHistory.builder()
                    .manager(memberRepository.findById(userId).orElseThrow(EntityNotFoundException::new))
                    .strategy(strategyRepository.findById((long) i).orElseThrow(EntityNotFoundException::new))
                    .statusCode(Code.APPROVE_REJECT.getCode())

                    .build();

            approvalHistoryList.add(approvalHistory);
            strategyApprovalRepository.save(approvalHistory);
        }

        PageResponse<AdminStrategyGetResponseDto> pageResponse =
                adminStrategyService
                        .findStrategyPage(AdminStrategySearchGetDto.builder()
                                .page(1)
                                .build()
                        );

        AtomicInteger expectedId = new AtomicInteger(1);

        pageResponse.getContent().forEach(dto -> {
                log.info(dto.toString());
            assertEquals(expectedId.getAndIncrement(), dto.getStrategyId());
        });
    }
}
