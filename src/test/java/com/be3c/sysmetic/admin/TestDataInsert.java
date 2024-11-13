package com.be3c.sysmetic.admin;

import com.be3c.sysmetic.domain.member.entity.Folder;
import com.be3c.sysmetic.domain.member.entity.InterestStrategy;
import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.repository.FolderRepository;
import com.be3c.sysmetic.domain.member.repository.InterestStrategyRepository;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.domain.strategy.dto.StrategyStatusCode;
import com.be3c.sysmetic.domain.strategy.entity.Method;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.entity.StrategyStatistics;
import com.be3c.sysmetic.domain.strategy.repository.MethodRepository;
import com.be3c.sysmetic.domain.strategy.repository.StrategyRepository;
import com.be3c.sysmetic.domain.strategy.repository.StrategyStatisticsRepository;
import com.be3c.sysmetic.global.common.Code;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@TestPropertySource(locations = "/application-test.properties")
@Slf4j
@SpringBootTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestDataInsert {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    InterestStrategyRepository interestStrategyRepository;

    @Autowired
    StrategyStatisticsRepository strategyStatisticsRepository;

    @Autowired
    FolderRepository folderRepository;

    @Autowired
    EntityManager entityManager;
    @Autowired
    private MethodRepository methodRepository;
    @Autowired
    private StrategyRepository strategyRepository;

    @BeforeEach
    public void setUp() {
//        entityManager.createNativeQuery("ALTER TABLE InterestStrategy AUTO_INCREMENT = 1")
//                .executeUpdate();
//        interestStrategyRepository.deleteAll();
    }

    @Test
    @DisplayName("이름 짓는 게 제일 어려워")
    @Order(1)
    @Commit
    public void testInterestStrategyInsert() {
        Member member = Member.builder()
                .roleCode("UR001")
                .email("asd@ASD")
                .password("asdasd")
                .name("LAS")
                .nickname("LASD")
                .phoneNumber("01012312312")
                .usingStatusCode("US001")
                .totalFollow(0)
                .receiveInfoConsent("RI001")
                .infoConsentDate(LocalDateTime.now())
                .receiveMarketingConsent("RM001")
                .marketingConsentDate(LocalDateTime.now())
                .build();

        Member member1 = Member.builder()
                .roleCode("UR001")
                .email("das@ASD")
                .password("dasdas")
                .name("LIS")
                .nickname("AMSD")
                .phoneNumber("01012312312")
                .usingStatusCode("US002")
                .totalFollow(0)
                .receiveInfoConsent("RI001")
                .infoConsentDate(LocalDateTime.now())
                .receiveMarketingConsent("RM001")
                .marketingConsentDate(LocalDateTime.now())
                .build();

        ArrayList<Method> methods = new ArrayList<>();
        for(int i = 0; i < 1000; i++) {
            methods.add(Method.builder()
                    .name("이름" + i)
                    .statusCode(Code.USING_STATE.getCode())
                    .build());
        }


        List<Strategy> strategyList = new ArrayList<>();
        for(int i = 0; i < 1000; i++) {
            strategyList.add(Strategy.builder()
                    .trader(member)
                    .method(methods.get(0))
                    .statusCode(StrategyStatusCode.PRIVATE.name())
                    .name("테스트 전략" + i)
                    .cycle('C')
                    .minOperationAmount(1000.0)
                    .followerCount((long) ((int) (Math.random() * 1000)))
                    .smScore(Math.random() * 100)
                    .kpRatio(Math.random() * 100)
                    .content("테스트 전략" + i)
                    .build());
        }

        List<Folder> folderList = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            folderList.add(Folder.builder()
                            .folderName(member.getName() + "의 폴더" + i)
                            .internalInterestStrategyCount(50)
                            .statusCode("US001")
                            .latestInterestStrategyAddedDate(LocalDateTime.now())
                            .member(member)
                    .build());
            folderList.add(Folder.builder()
                    .folderName(member1.getName() + "의 폴더" + i)
                    .internalInterestStrategyCount(50)
                    .statusCode("US001")
                    .latestInterestStrategyAddedDate(LocalDateTime.now())
                    .member(member1)
                    .build());
        }

        List<InterestStrategy> interestStrategyList = new ArrayList<>();
        for (int i = 0; i < 250; i++) {
            interestStrategyList.add(InterestStrategy.builder()
                    .folder(folderList.get((i % 5) * 2))
                            .member(member)
                            .strategy(strategyList.get(i))
                            .statusCode(Code.USING_STATE.getCode())
                    .build());

            interestStrategyList.add(InterestStrategy.builder()
                    .folder(folderList.get((i % 5) * 2 + 1))
                    .member(member1)
                    .strategy(strategyList.get(i))
                    .statusCode(Code.USING_STATE.getCode())
                    .build());
        }

        List<StrategyStatistics> strategyStatisticsList = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            Random random = new Random();

            // 전략 통계 엔티티 생성
            strategyStatisticsList.add(StrategyStatistics.builder()
                    .strategy(strategyList.get(i))
                    .currentBalance(random.nextDouble() * 100000)
                    .principal(random.nextDouble() * 50000)
                    .accumulatedDepositWithdrawalAmount(random.nextDouble() * 20000)
                    .accumulatedProfitLossAmount(random.nextDouble() * 15000 - 7500)
                    .accumulatedProfitLossRate(random.nextDouble() * 20 - 10)
                    .accumulatedProfitAmount(random.nextDouble() * 10000)
                    .accumulatedProfitRate(random.nextDouble() * 50)
                    .currentCapitalReductionAmount(random.nextDouble() * 5000)
                    .currentCapitalReductionRate(random.nextDouble() * 10)
                    .maximumCapitalReductionAmount(random.nextDouble() * 10000)
                    .maximumCapitalReductionRate(random.nextDouble() * 15)
                    .averageProfitLossAmount(random.nextDouble() * 500)
                    .averageProfitLossRate(random.nextDouble() * 5)
                    .maximumDailyProfitAmount(random.nextDouble() * 1000)
                    .maximumDailyProfitRate(random.nextDouble() * 10)
                    .maximumDailyLossAmount(random.nextDouble() * 1000)
                    .maximumDailyLossRate(random.nextDouble() * 10)
                    .totalTradingDays((long) random.nextInt(365))
                    .currentContinuousProfitLossDays((long) random.nextInt(30))
                    .totalProfitDays((long) random.nextInt(200))
                    .maxContinuousProfitDays((long) random.nextInt(20))
                    .totalLossDays((long) random.nextInt(150))
                    .maxContinuousLossDays((long) random.nextInt(15))
                    .winningRate(random.nextDouble() * 100)
                    .highPointRenewalProgress(LocalDateTime.now().minusDays(random.nextInt(365)))
                    .profitFactor(random.nextDouble() * 2)
                    .roa(random.nextDouble() * 20)
                    .lastYearProfitRate(random.nextDouble() * 30 - 10)
                    .standardDeviation(random.nextDouble() * 5)
                    .build()
            );
        }

        memberRepository.save(member);
        memberRepository.save(member1);
        methodRepository.saveAll(methods);

        strategyRepository.saveAll(strategyList);
        strategyStatisticsRepository.saveAll(strategyStatisticsList);
        folderRepository.saveAll(folderList);
        interestStrategyRepository.saveAll(interestStrategyList);
    }
}
