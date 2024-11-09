package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.strategy.dto.SaveStrategyRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.StrategyStatusCode;
import com.be3c.sysmetic.domain.strategy.entity.Method;
import com.be3c.sysmetic.domain.strategy.entity.Stock;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@SpringBootTest
public class FindStrategyRepositoryTest {

    private final StrategyRepository strategyRepository;
    private final MemberRepository memberRepository;
    private final MethodRepository methodRepository;
    private final StockRepository stockRepository;
    private final StrategyStockReferenceRepository strategyStockReferenceRepository;

    @BeforeEach
    void setup() {
        saveMethod();
        saveMember();
        saveStock();

        strategyStockReferenceRepository.deleteAll();
        strategyRepository.deleteAll();
    }

    @Test
    void findByTraderIdSuccessTest() {
        Member member = findMember();
        Strategy strategy = getStrategy(getRequestDto(), member, findMethod());

        // DB 저장
        strategyRepository.save(strategy);

        // memberId로 조회
        List<Strategy> traderStrategyList = strategyRepository.findByTraderId(member.getId());
        assertTrue(!traderStrategyList.isEmpty());
        assertEquals(member.getId(), traderStrategyList.stream().findFirst().get().getTrader().getId());
    }

    void saveMethod() {
        Method method = Method.builder()
                .id(0L)
                .name("Auto")
                .statusCode("Y")
                .createdBy(0L)
                .modifiedBy(0L)
                .build();

        methodRepository.save(method);
    }

    void saveMember() {
        Member member = Member.builder()
                .roleCode("USER")
                .email("tester@example.com")
                .password("password123")
                .name("Test User")
                .nickname("testuser")
                .phoneNumber("010-1234-5678")
                .usingStatusCode("ACTIVE")
                .totalFollow(100)
                .receiveInfoConsent("Y")
                .infoConsentDate(LocalDateTime.now().minusDays(10))
                .receiveMarketingConsent("Y")
                .marketingConsentDate(LocalDateTime.now().minusDays(10))
                .createdBy(1L)
                .createdDate(LocalDateTime.now().minusDays(30))
                .modifiedBy(1L)
                .modifiedDate(LocalDateTime.now())
                .build();

        memberRepository.save(member);
    }

    void saveStock() {
        Stock stock = Stock.builder()
                .id(0L)
                .name("국내종목")
                .statusCode("PUBLIC")
                .code("001")
                .createdBy(0L)
                .modifiedBy(0L)
                .build();

        stockRepository.saveAndFlush(stock);
    }

    Method findMethod() {
        return methodRepository.findAll().stream().findFirst().get();
    }

    Member findMember() {
        return memberRepository.findAll().stream().findFirst().get();
    }

    Stock findStock() {
        return stockRepository.findAll().stream().findFirst().get();
    }

    SaveStrategyRequestDto getRequestDto() {
        return SaveStrategyRequestDto.builder()
                .name("전략명")
                .content("전략 내용")
                .traderId(findMember().getId())
                .methodId(findMethod().getId())
                .stockIdList(List.of(findStock().getId()))
                .cycle('D')
                .minOperationAmount(300000.0)
                .build();
    }

    Strategy getStrategy(SaveStrategyRequestDto requestDto, Member member, Method method) {
        return Strategy.builder()
                .trader(member)
                .method(method)
                .statusCode(StrategyStatusCode.PRIVATE.name())
                .name(requestDto.getName())
                .cycle(requestDto.getCycle())
                .minOperationAmount(requestDto.getMinOperationAmount())
                .content(requestDto.getContent())
                .createdBy(findMember().getId())
                .modifiedBy(findMember().getId())
                .build();
    }
}
