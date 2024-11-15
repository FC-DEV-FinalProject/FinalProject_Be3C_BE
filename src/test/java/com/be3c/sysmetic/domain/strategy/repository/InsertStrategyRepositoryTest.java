package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.domain.strategy.dto.SaveStrategyRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.StrategyStatusCode;
import com.be3c.sysmetic.domain.strategy.entity.Method;
import com.be3c.sysmetic.domain.strategy.entity.Stock;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
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
class InsertStrategyRepositoryTest {

    private final StrategyRepository strategyRepository;

    private final MethodRepository methodRepository;

    private final MemberRepository memberRepository;

    private final StockRepository stockRepository;

    @BeforeEach
    void setup() {
        saveMethod();
        saveMember();
        saveStock();

        strategyRepository.deleteAll();
    }

    @DisplayName("전략 등록 성공 테스트")
    @Test
    void insertStrategySuccessTest() {

        // request 객체
        SaveStrategyRequestDto saveStrategyRequestDto = getRequestDto().toBuilder().name("전략1").build();

        // DB 저장 객체
        Strategy strategy = getStrategy(saveStrategyRequestDto, findMember(), findMethod());

        // DB 저장
        Strategy savedStrategy = strategyRepository.saveAndFlush(strategy);

        // 검증
        assertNotNull(savedStrategy);
        assertEquals(saveStrategyRequestDto.getTraderId(), savedStrategy.getTrader().getId());
        assertEquals(saveStrategyRequestDto.getMethodId(), savedStrategy.getMethod().getId());
        assertEquals(savedStrategy.getStatusCode(), StrategyStatusCode.PRIVATE.name());
        assertEquals(saveStrategyRequestDto.getName(), savedStrategy.getName());
        assertEquals(saveStrategyRequestDto.getCycle(), savedStrategy.getCycle());
        assertEquals(saveStrategyRequestDto.getContent(), savedStrategy.getContent());
        assertEquals(0, savedStrategy.getFollowerCount());
        assertEquals(0.0, savedStrategy.getKpRatio());
        assertEquals(0.0, savedStrategy.getSmScore());
        assertNotNull(savedStrategy.getStrategyCreatedDate());
        assertNotNull(savedStrategy.getStrategyModifiedDate());
    }

    @DisplayName("전략 등록 실패 테스트 - 멤버id 미존재")
    @Test
    void insertStrategyFailureTest_NullMemberId() {
        SaveStrategyRequestDto saveStrategyRequestDto = getRequestDto();

        Strategy strategy = getStrategy(saveStrategyRequestDto, null, findMethod());

        // 예외 검증
        assertThrows(DataIntegrityViolationException.class, () -> {
            strategyRepository.saveAndFlush(strategy);
        });
    }

    @DisplayName("전략 등록 실패 테스트 - 전략명 미존재")
    @Test
    void insertStrategyFailureTest_NullStrategyName() {
        SaveStrategyRequestDto saveStrategyRequestDto = getRequestDto().toBuilder().name(null).build();

        Strategy strategy = getStrategy(saveStrategyRequestDto, findMember(), findMethod());

        // 예외 검증
        assertThrows(DataIntegrityViolationException.class, () -> {
            strategyRepository.saveAndFlush(strategy);
        });
    }

    @DisplayName("전략 등록 실패 테스트 - 매매방식 미존재")
    @Test
    void insertStrategyFailureTest_NullMethod() {
        SaveStrategyRequestDto saveStrategyRequestDto = getRequestDto();

        Strategy strategy = getStrategy(saveStrategyRequestDto, findMember(), null);

        // 예외 검증
        assertThrows(DataIntegrityViolationException.class, () -> {
            strategyRepository.saveAndFlush(strategy);
        });
    }

    @DisplayName("전략 등록 실패 테스트 - 주기 미존재")
    @Test
    void insertStrategyFailureTest_NullCycle() {
        SaveStrategyRequestDto saveStrategyRequestDto = getRequestDto().toBuilder().cycle(null).build();

        Strategy strategy = getStrategy(saveStrategyRequestDto, findMember(), findMethod());

        // 예외 검증
        assertThrows(DataIntegrityViolationException.class, () -> {
            strategyRepository.saveAndFlush(strategy);
        });
    }

//    @DisplayName("전략 등록 실패 테스트 - 종목 미존재")
//    @Test
//    void insertStrategyFailureTestEmptyStockList() {
//        InsertStrategyRequestDto insertStrategyRequestDto = getInsertStrategyRequestDto().toBuilder().stockIdList(null).build();
//
//        Strategy strategy = getStrategy(insertStrategyRequestDto, getMember(), getMethod());
//
//        // 예외 검증
//        assertThrows(DataIntegrityViolationException.class, () -> {
//            strategyRepository.saveAndFlush(strategy);
//        });
//    }

    @DisplayName("전략 등록 실패 테스트 - 전략소개내용 미존재")
    @Test
    void insertStrategyFailureTest_NullContent() {
        SaveStrategyRequestDto saveStrategyRequestDto = getRequestDto().toBuilder().content(null).build();

        Strategy strategy = getStrategy(saveStrategyRequestDto, findMember(), findMethod());

        // 예외 검증
        assertThrows(DataIntegrityViolationException.class, () -> {
            strategyRepository.saveAndFlush(strategy);
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

    SaveStrategyRequestDto getRequestDto() {
        return SaveStrategyRequestDto.builder()
                .name("전략명")
                .content("전략 내용")
                .traderId(findMember().getId())
                .methodId(findMethod().getId())
                .stockIdList(List.of(findStock().getId()))
                .cycle('D')
                .build();
    }

    Strategy getStrategy(SaveStrategyRequestDto requestDto, Member member, Method method) {
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

