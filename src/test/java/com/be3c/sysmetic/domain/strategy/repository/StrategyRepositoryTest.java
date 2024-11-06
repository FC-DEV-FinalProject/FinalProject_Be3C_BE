package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.strategy.dto.InsertStrategyRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.StrategyStatusCode;
import com.be3c.sysmetic.domain.strategy.entity.Method;
import com.be3c.sysmetic.domain.strategy.entity.Stock;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class StrategyRepositoryTest {
    /*
    트레이더 전략 등록

    1. 클라이언트에서 전략 등록 post API 요청
    2. 트레이더 권한을 가진 멤버인지 확인
    3. request body로 받은 멤버id, 전략명, 매매방식, 주기, 종목(List), 최소운용금액, 전략소개내용이 모두 null 값이 아닌지 검증
    4. 전략명 중복 데이터가 없는지 확인
    5. 비공개 상태로 설정하여 DB에 저장
     */

    @Autowired
    private StrategyRepository strategyRepository;

    @Autowired
    private MethodRepository methodRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StockRepository stockRepository;

    @BeforeEach
    void deleteAll() {
        saveMethod();
        saveMember();
        saveStock();

        strategyRepository.deleteAll();
    }

    @DisplayName("전략 등록 성공 테스트")
    @Test
    void insertStrategySuccessTest() {

        // request 객체
        InsertStrategyRequestDto insertStrategyRequestDto = getInsertStrategyRequestDto().toBuilder().name("전략1").build();

        // DB 저장 객체
        Strategy strategy = getStrategy(insertStrategyRequestDto, getMember(), getMethod());

        // DB 저장
        Strategy savedStrategy = strategyRepository.saveAndFlush(strategy);

        // 검증
        assertNotNull(savedStrategy);
        assertEquals(insertStrategyRequestDto.getTraderId(), savedStrategy.getTrader().getId());
        assertEquals(insertStrategyRequestDto.getMethodId(), savedStrategy.getMethod().getId());
        assertEquals(savedStrategy.getStatusCode(), StrategyStatusCode.PRIVATE.name());
        assertEquals(insertStrategyRequestDto.getName(), savedStrategy.getName());
        assertEquals(insertStrategyRequestDto.getCycle(), savedStrategy.getCycle());
        assertEquals(insertStrategyRequestDto.getMinOperationAmount(), savedStrategy.getMinOperationAmount());
        assertEquals(insertStrategyRequestDto.getContent(), savedStrategy.getContent());
        assertEquals(0, savedStrategy.getFollowerCount());
        assertEquals(0.0, savedStrategy.getKpRatio());
        assertEquals(0.0, savedStrategy.getSmScore());
        assertNotNull(savedStrategy.getStrategyCreatedDate());
        assertNotNull(savedStrategy.getStrategyModifiedDate());
    }

    @DisplayName("전략 등록 실패 테스트 - 멤버id 미존재")
    @Test
    void insertStrategyFailureTestNullMemberId() {
        InsertStrategyRequestDto insertStrategyRequestDto = getInsertStrategyRequestDto();

        Strategy strategy = getStrategy(insertStrategyRequestDto, null, getMethod());

        // 예외가 발생하는지 검증
        assertThrows(DataIntegrityViolationException.class, () -> {
            strategyRepository.saveAndFlush(strategy);
        });
    }

    @DisplayName("전략 등록 실패 테스트 - 전략명 미존재")
    @Test
    void insertStrategyFailureTestNullStrategyName() {
        InsertStrategyRequestDto insertStrategyRequestDto = getInsertStrategyRequestDto().toBuilder().name(null).build();

        Strategy strategy = getStrategy(insertStrategyRequestDto, getMember(), getMethod());

        // 예외가 발생하는지 검증
        assertThrows(DataIntegrityViolationException.class, () -> {
            strategyRepository.saveAndFlush(strategy);
        });
    }

    @DisplayName("전략 등록 실패 테스트 - 매매방식 미존재")
    @Test
    void insertStrategyFailureTestNullMethod() {
        InsertStrategyRequestDto insertStrategyRequestDto = getInsertStrategyRequestDto();

        Strategy strategy = getStrategy(insertStrategyRequestDto, getMember(), null);

        // 예외가 발생하는지 검증
        assertThrows(DataIntegrityViolationException.class, () -> {
            strategyRepository.saveAndFlush(strategy);
        });
    }

    @DisplayName("전략 등록 실패 테스트 - 주기 미존재")
    @Test
    void insertStrategyFailureTestNullCycle() {
        InsertStrategyRequestDto insertStrategyRequestDto = getInsertStrategyRequestDto().toBuilder().cycle(null).build();

        Strategy strategy = getStrategy(insertStrategyRequestDto, getMember(), getMethod());

        // 예외가 발생하는지 검증
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
//        // 예외가 발생하는지 검증
//        assertThrows(DataIntegrityViolationException.class, () -> {
//            strategyRepository.saveAndFlush(strategy);
//        });
//    }

    @DisplayName("전략 등록 실패 테스트 - 최소운용금액 미존재")
    @Test
    void insertStrategyFailureTestNullMinOperationAmount() {
        InsertStrategyRequestDto insertStrategyRequestDto = getInsertStrategyRequestDto().toBuilder().minOperationAmount(null).build();

        Strategy strategy = getStrategy(insertStrategyRequestDto, getMember(), getMethod());

        // 예외가 발생하는지 검증
        assertThrows(DataIntegrityViolationException.class, () -> {
            strategyRepository.saveAndFlush(strategy);
        });
    }

    @DisplayName("전략 등록 실패 테스트 - 전략소개내용 미존재")
    @Test
    void insertStrategyFailureTestNullContent() {
        InsertStrategyRequestDto insertStrategyRequestDto = getInsertStrategyRequestDto().toBuilder().content(null).build();

        Strategy strategy = getStrategy(insertStrategyRequestDto, getMember(), getMethod());

        // 예외가 발생하는지 검증
        assertThrows(DataIntegrityViolationException.class, () -> {
            strategyRepository.saveAndFlush(strategy);
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
                .id(0L)
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

