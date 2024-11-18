package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.domain.strategy.dto.StrategyPostRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.StrategyStatusCode;
import com.be3c.sysmetic.domain.strategy.entity.Method;
import com.be3c.sysmetic.domain.strategy.entity.Stock;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.exception.StrategyBadRequestException;
import com.be3c.sysmetic.domain.strategy.repository.*;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@SpringBootTest
class InsertStrategyServiceTest {

    private final InsertStrategyServiceImpl strategyService;

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

    @DisplayName("전략 등록 성공 테스트")
    @Test
    void insertStrategySuccessTest() {
        // DB 저장
        StrategyPostRequestDto requestDto = getRequestDto();
        Strategy savedStrategy = strategyService.insertStrategy(requestDto);

        // 검증
        assertNotNull(savedStrategy);
        assertEquals(requestDto.getTraderId(), savedStrategy.getTrader().getId());
        assertEquals(requestDto.getMethodId(), savedStrategy.getMethod().getId());
        assertEquals(savedStrategy.getStatusCode(), StrategyStatusCode.PRIVATE.name());
        assertEquals(requestDto.getName(), savedStrategy.getName());
        assertEquals(requestDto.getCycle(), savedStrategy.getCycle());
        assertEquals(requestDto.getContent(), savedStrategy.getContent());
        assertEquals(0, savedStrategy.getFollowerCount());
        assertEquals(0.0, savedStrategy.getKpRatio());
        assertEquals(0.0, savedStrategy.getSmScore());
        assertNotNull(savedStrategy.getStrategyCreatedDate());
        assertNotNull(savedStrategy.getStrategyModifiedDate());
    }

    @DisplayName("전략 등록 실패 테스트 - 멤버id 미존재")
    @Test
    void insertStrategyFailureTest_NullMemberId() {
        StrategyPostRequestDto requestDto = getRequestDto();
        requestDto.setTraderId(null);

        // 예외 검증
        assertThrows(StrategyBadRequestException.class, () -> {
            strategyService.insertStrategy(requestDto);
        });
    }

    @DisplayName("전략 등록 실패 테스트 - 전략명 미존재")
    @Test
    void insertStrategyFailureTest_NullStrategyName() {
        StrategyPostRequestDto requestDto = getRequestDto();
        requestDto.setName(null);

        // 예외 검증
        assertThrows(DataIntegrityViolationException.class, () -> {
            strategyService.insertStrategy(requestDto);
        });
    }

    void saveMethod() {
        Method method = Method.builder()
                .id(0L)
                .name("Auto")
                .statusCode("Y")
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
                .build();

        memberRepository.save(member);
    }

    void saveStock() {
        Stock stock = Stock.builder()
                .id(0L)
                .name("국내종목")
                .statusCode("PUBLIC")
                .code("001")
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

    StrategyPostRequestDto getRequestDto() {
        return StrategyPostRequestDto.builder()
                .name("전략명")
                .content("전략 내용")
                .traderId(findMember().getId())
                .methodId(findMethod().getId())
                .stockIdList(List.of(findStock().getId()))
                .cycle('D')
                .build();
    }

    Strategy getStrategy(StrategyPostRequestDto requestDto, Member member, Method method) {
        return Strategy.builder()
                .trader(member)
                .method(method)
                .statusCode(StrategyStatusCode.PRIVATE.name())
                .name(requestDto.getName())
                .cycle(requestDto.getCycle())
                .content(requestDto.getContent())
                .build();
    }
}
