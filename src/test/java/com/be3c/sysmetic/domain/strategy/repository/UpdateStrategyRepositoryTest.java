package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.domain.strategy.dto.SaveStrategyRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.StrategyStatusCode;
import com.be3c.sysmetic.domain.strategy.entity.Method;
import com.be3c.sysmetic.domain.strategy.entity.Stock;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.exception.StrategyBadRequestException;
import com.be3c.sysmetic.domain.strategy.exception.StrategyExceptionMessage;
import jakarta.el.MethodNotFoundException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@SpringBootTest
public class UpdateStrategyRepositoryTest {

    private final StrategyRepository strategyRepository;
    private final MethodRepository methodRepository;
    private final StockRepository stockRepository;
    private final MemberRepository memberRepository;

    @BeforeEach
    void setup() {
        saveMember();
        saveMethod();
        saveModificationMethod();
        saveStock();

        strategyRepository.deleteAll();
        strategyRepository.save(getStrategy(getRequestDto(), findMember(), findMethod()));
    }

    @DisplayName("전략 수정 성공 테스트 - 전체 수정")
    @Test
    void updateStrategyTest_All() {
        // 전략 조회
        Strategy existingStrategy = strategyRepository.findById(1L).orElseThrow(() -> new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage()));

        // 전략 상태 검증
        if(!existingStrategy.getStatusCode().equals(StrategyStatusCode.PRIVATE.name())) {
            throw new StrategyBadRequestException(StrategyExceptionMessage.INVALID_STATUS.getMessage());
        }

        SaveStrategyRequestDto updateRequestDto = SaveStrategyRequestDto.builder()
                .methodId(2L)
                .name("전략수정")
                .cycle('D')
                .minOperationAmount(1000.0)
                .content("전략수정내용")
                .build();

        // null이 아닌 데이터만 업데이트
        if(updateRequestDto.getMethodId() != null) {
            existingStrategy.setMethod(findMethodById(updateRequestDto.getMethodId()));
        }

        if(updateRequestDto.getName() != null) {
            existingStrategy.setName(updateRequestDto.getName());
        }

        if(updateRequestDto.getCycle() != null) {
            existingStrategy.setCycle(updateRequestDto.getCycle());
        }

        if(updateRequestDto.getContent() != null) {
            existingStrategy.setContent(updateRequestDto.getContent());
        }

        // DB 업데이트
        Strategy updatedStrategy = strategyRepository.save(existingStrategy);

        // 검증
        assertNotNull(updatedStrategy);
        assertEquals(updatedStrategy.getMethod().getId(), updateRequestDto.getMethodId());
        assertEquals(updatedStrategy.getName(), updateRequestDto.getName());
        assertEquals(updatedStrategy.getCycle(), updateRequestDto.getCycle());
        assertEquals(updatedStrategy.getContent(), updateRequestDto.getContent());
    }

    void saveMethod() {
        Method method = Method.builder()
                .id(1L)
                .name("Auto")
                .statusCode("Y")
                .build();

        methodRepository.save(method);
    }

    void saveModificationMethod() {
        Method method = Method.builder()
                .id(2L)
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
                .totalStrategyCount(0)
                .receiveInfoConsent("Y")
                .infoConsentDate(LocalDateTime.now().minusDays(10))
                .receiveMarketingConsent("Y")
                .marketingConsentDate(LocalDateTime.now().minusDays(10))
                .build();

        memberRepository.save(member);
    }

    void saveStock() {
        Stock stock = Stock.builder()
                .id(1L)
                .name("국내종목")
                .statusCode("PUBLIC")
                .code("001")
                .build();

        stockRepository.saveAndFlush(stock);
    }

    Method findMethod() {
        return methodRepository.findAll().stream().findFirst().get();
    }

    Method findMethodById(Long id) {
        return methodRepository.findById(id).orElseThrow(() -> new MethodNotFoundException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage()));
    }

    Member findMember() {
        return memberRepository.findAll().stream().findFirst().get();
    }

    Stock findStock() {
        return stockRepository.findAll().stream().findFirst().get();
    }

    SaveStrategyRequestDto getRequestDto() {
        return SaveStrategyRequestDto.builder()
                .traderId(null)
                .methodId(1L)
                .stockIdList(List.of(1L, 2L))
                .name("전략")
                .cycle('P')
                .minOperationAmount(300000.0)
                .content("전략내용")
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
