package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.strategy.dto.InsertStrategyRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.StrategyStatusCode;
import com.be3c.sysmetic.domain.strategy.entity.Method;
import com.be3c.sysmetic.domain.strategy.entity.Stock;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.entity.StrategyStockReference;
import com.be3c.sysmetic.domain.strategy.exception.StrategyBadRequestException;
import com.be3c.sysmetic.domain.strategy.repository.*;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@SpringBootTest
class StrategyServiceImplTest {

    private final StrategyServiceImpl strategyService;

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
        InsertStrategyRequestDto requestDto = getInsertStrategyRequestDto();
        Strategy savedStrategy = strategyService.insertStrategy(requestDto);

        // 검증
        assertNotNull(savedStrategy);
        assertEquals(requestDto.getTraderId(), savedStrategy.getTrader().getId());
        assertEquals(requestDto.getMethodId(), savedStrategy.getMethod().getId());
        assertEquals(savedStrategy.getStatusCode(), StrategyStatusCode.PRIVATE.name());
        assertEquals(requestDto.getName(), savedStrategy.getName());
        assertEquals(requestDto.getCycle(), savedStrategy.getCycle());
        assertEquals(requestDto.getMinOperationAmount(), savedStrategy.getMinOperationAmount());
        assertEquals(requestDto.getContent(), savedStrategy.getContent());
        assertEquals(0, savedStrategy.getFollowerCount());
        assertEquals(0.0, savedStrategy.getKpRatio());
        assertEquals(0.0, savedStrategy.getSmScore());
        assertNotNull(savedStrategy.getStrategyCreatedDate());
        assertNotNull(savedStrategy.getStrategyModifiedDate());
    }

    @DisplayName("전략 등록 실패 테스트 - 멤버id 미존재")
    @Test
    void insertStrategyFailureTestNullMemberId() {
        InsertStrategyRequestDto requestDto = getInsertStrategyRequestDto();
        requestDto.setTraderId(null);

        // 예외 검증
        assertThrows(StrategyBadRequestException.class, () -> {
            strategyService.insertStrategy(requestDto);
        });
    }

    @DisplayName("전략 등록 실패 테스트 - 전략명 미존재")
    @Test
    void insertStrategyFailureTestNullStrategyName() {
        InsertStrategyRequestDto requestDto = getInsertStrategyRequestDto();
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
                .explanation("설명")
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

    Method getMethod() {
        return methodRepository.findAll().stream().findFirst().get();
    }

    Member getMember() {
        return memberRepository.findAll().stream().findFirst().get();
    }

    Stock getStock() {
        return stockRepository.findAll().stream().findFirst().get();
    }

    InsertStrategyRequestDto getInsertStrategyRequestDto() {
        return InsertStrategyRequestDto.builder()
                .name("전략명")
                .content("전략 내용")
                .traderId(getMember().getId())
                .methodId(getMethod().getId())
                .stockIdList(List.of(getStock().getId()))
                .cycle('D')
                .minOperationAmount(300000.0)
                .build();
    }

    Strategy getStrategy(InsertStrategyRequestDto requestDto, Member member, Method method) {
        return Strategy.builder()
                .trader(member)
                .method(method)
                .statusCode(StrategyStatusCode.PRIVATE.name())
                .name(requestDto.getName())
                .cycle(requestDto.getCycle())
                .minOperationAmount(requestDto.getMinOperationAmount())
                .content(requestDto.getContent())
                .createdBy(getMember().getId())
                .modifiedBy(getMember().getId())
                .build();
    }
}
