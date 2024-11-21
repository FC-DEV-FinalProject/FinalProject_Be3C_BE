package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.member.entity.Member;
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
import jakarta.persistence.EntityManager;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@Slf4j
@SpringBootTest
@Transactional
public class StrategyApprovalServiceTest {

    private final AdminStrategyService adminStrategyService;

    private final StrategyApprovalRepository strategyApprovalRepository;

    private final MemberRepository memberRepository;

    private final MethodRepository methodRepository;

    private final StrategyRepository strategyRepository;

    private final DailyRepository dailyRepository;

    private final EntityManager entityManager;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final StrategyStatisticsRepository strategyStatisticsRepository;

    @BeforeEach
    public void setUp() {
        memberRepository.deleteAll();
        strategyRepository.deleteAll();
        methodRepository.deleteAll();
        dailyRepository.deleteAll();
        strategyApprovalRepository.deleteAll();

        entityManager.createNativeQuery("ALTER TABLE Member AUTO_INCREMENT = 1")
                .executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE folder AUTO_INCREMENT = 1")
                .executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE strategy AUTO_INCREMENT = 1")
                .executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE strategy_statistics AUTO_INCREMENT = 1")
                .executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE method AUTO_INCREMENT = 1")
                .executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE method AUTO_INCREMENT = 1")
                .executeUpdate();


        Member member = Member.builder()
                .email("test@test.com")
                .password(bCryptPasswordEncoder.encode("encodedPassword"))
                // 초기값 설정
                .id(1L)
                .roleCode("TRADER")
                .name("테스트")
                .nickname("테스트")
                .phoneNumber("01012341234")
                .usingStatusCode("US001")
                .birth(LocalDateTime.now())
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
                .firstRegistrationDate(LocalDateTime.now().minusMonths(6))
                .lastRegistrationDate(LocalDateTime.now())
                .build();

        methodRepository.save(method);
        memberRepository.save(member);
        strategyRepository.saveAll(strategyList);
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
    @DisplayName("나는 졸리다.")
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
    @DisplayName("나는 졸렸다.")
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
}
