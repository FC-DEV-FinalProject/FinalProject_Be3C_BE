package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.domain.strategy.dto.SaveStrategyRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.StrategyStatusCode;
import com.be3c.sysmetic.domain.strategy.entity.Method;
import com.be3c.sysmetic.domain.strategy.entity.Stock;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.repository.*;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@SpringBootTest
public class UpdateStrategyServiceTest {

    private final UpdateStrategyServiceImpl updateStrategyService;

    private final InsertStrategyServiceImpl insertStrategyService;

    private final StrategyRepository strategyRepository;

    private final MethodRepository methodRepository;

    private final MemberRepository memberRepository;

    private final StockRepository stockRepository;

    private final StrategyStockReferenceRepository strategyStockReferenceRepository;

    @BeforeEach
    void setup() {
        saveMember();
        saveMethod();
        saveStock();
        strategyStockReferenceRepository.deleteAll();
        strategyRepository.deleteAll();
    }

    @DisplayName("전략 수정 성공 테스트")
    @Test
    void updateStrategySuccessTest() {
        // insert
        SaveStrategyRequestDto requestDto = getRequestDto();
        Strategy strategy = insertStrategyService.insertStrategy(requestDto);

        assertNotNull(strategy);

        // update
        SaveStrategyRequestDto updateRequestDto = getRequestDto();
//        updateRequestDto.setName("이름 수정");
        updateRequestDto.setContent("내용 수정");
        updateStrategyService.updateStrategy(strategy.getId(), updateRequestDto);

        // find
        Strategy updatedStrategy = strategyRepository.findAll().stream().findFirst().orElse(null);

        if(updatedStrategy != null) {
            assertEquals(updateRequestDto.getName(), updatedStrategy.getName());
            assertEquals(updateRequestDto.getContent(), updatedStrategy.getContent());
        }
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
