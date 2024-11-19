package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.domain.strategy.dto.StrategyPostRequestDto;
import com.be3c.sysmetic.domain.strategy.entity.Method;
import com.be3c.sysmetic.domain.strategy.entity.Stock;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.entity.StrategyStockReference;
import com.be3c.sysmetic.domain.strategy.repository.MethodRepository;
import com.be3c.sysmetic.domain.strategy.repository.StockRepository;
import com.be3c.sysmetic.domain.strategy.repository.StrategyRepository;
import com.be3c.sysmetic.domain.strategy.repository.StrategyStockReferenceRepository;
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
public class TraderStrategyServiceTest {
    private final MemberRepository memberRepository;
    private final StockRepository stockRepository;
    private final MethodRepository methodRepository;
    private final StrategyStockReferenceRepository strategyStockReferenceRepository;
    private final StrategyRepository strategyRepository;
    private final TraderStrategyServiceImpl traderStrategyService;

    @BeforeEach
    void setUp() {
        // save
        memberRepository.save(getMember());
        stockRepository.save(getStock());
        methodRepository.save(getMethod());

        // delete
        strategyStockReferenceRepository.deleteAll();
        strategyRepository.deleteAll();
    }

    // insert
    @DisplayName("등록 성공 테스트")
    @Test
    void insert_success_test() {
        StrategyPostRequestDto requestDto = getStrategyPostRequestDto();

        traderStrategyService.insertStrategy(requestDto, null);

        Strategy savedStrategy = strategyRepository.findAll().stream().findFirst().orElse(null);

        StrategyStockReference strategyStockReference = StrategyStockReference.builder()
                .strategy(savedStrategy)
                .stock(findStock())
                .build();

        strategyStockReferenceRepository.save(strategyStockReference);

        StrategyStockReference savedStrategyStockReference = strategyStockReferenceRepository.findAll().stream().findFirst().orElse(null);

        assertNotNull(savedStrategy);
        assertNotNull(savedStrategyStockReference);
    }

    // update
    @DisplayName("수정 성공 테스트")
    @Test
    void update_success_test() {
        StrategyPostRequestDto requestDto = getStrategyPostRequestDto();

        traderStrategyService.insertStrategy(requestDto, null);

        Strategy savedStrategy = strategyRepository.findAll().stream().findFirst().orElse(null);

        StrategyStockReference strategyStockReference = StrategyStockReference.builder()
                .strategy(savedStrategy)
                .stock(findStock())
                .build();

        strategyStockReferenceRepository.save(strategyStockReference);

        StrategyStockReference savedStrategyStockReference = strategyStockReferenceRepository.findAll().stream().findFirst().orElse(null);

        assertNotNull(savedStrategy);
        assertNotNull(savedStrategyStockReference);

        StrategyPostRequestDto updateRequestDto = requestDto.toBuilder()
                .name("수정전략")
                .cycle('D')
                .content("전략 수정 테스트입니다.")
                .build();

        traderStrategyService.updateStrategy(savedStrategy.getId(), updateRequestDto, null);

        Strategy updatedStrategy = strategyRepository.findById(savedStrategy.getId()).orElse(null);

        assertNotNull(updatedStrategy);
        assertEquals(updateRequestDto.getName(), updatedStrategy.getName());
        assertEquals(updateRequestDto.getCycle(), updatedStrategy.getCycle());
        assertEquals(updateRequestDto.getContent(), updatedStrategy.getContent());
    }

    // delete
    @DisplayName("삭제 성공 테스트")
    @Test
    void delete_success_test() {
        StrategyPostRequestDto requestDto = getStrategyPostRequestDto();

        traderStrategyService.insertStrategy(requestDto, null);

        Strategy savedStrategy = strategyRepository.findAll().stream().findFirst().orElse(null);

        StrategyStockReference strategyStockReference = StrategyStockReference.builder()
                .strategy(savedStrategy)
                .stock(findStock())
                .build();

        strategyStockReferenceRepository.save(strategyStockReference);

        StrategyStockReference savedStrategyStockReference = strategyStockReferenceRepository.findAll().stream().findFirst().orElse(null);

        assertNotNull(savedStrategy);
        assertNotNull(savedStrategyStockReference);

        traderStrategyService.deleteStrategy(savedStrategy.getId());

        Strategy findStrategy = strategyRepository.findAll().stream().findFirst().orElse(null);

        assertNull(findStrategy);
    }


    // get member
    private Member getMember() {
        return Member.builder()
                .roleCode("TRADER")
                .email("sysmetic_tester@sysmetic.com")
                .password("sys1234!")
                .name("한감자")
                .nickname("감자")
                .birth(LocalDateTime.now())
                .phoneNumber("01022223333")
                .usingStatusCode("")
                .totalFollow(160)
                .totalStrategyCount(4)
                .receiveInfoConsent("Y")
                .infoConsentDate(LocalDateTime.now())
                .receiveMarketingConsent("Y")
                .marketingConsentDate(LocalDateTime.now())
                .build();
    }

    // get stock
    private Stock getStock() {
        return Stock.builder()
                .name("국내ETF")
                .statusCode("PUBLIC")
                .build();
    }

    // get method
    private Method getMethod() {
        return Method.builder()
                .name("Auto")
                .statusCode("PUBLIC")
                .build();
    }

    // find member
    private Member findMember() {
        return memberRepository.findAll().stream().findFirst().orElse(null);
    }

    // find method
    private Method findMethod() {
        return methodRepository.findAll().stream().findFirst().orElse(null);
    }

    // find Stock
    private Stock findStock() {
        return stockRepository.findAll().stream().findFirst().orElse(null);
    }

    // get insert strategy request dto
    private StrategyPostRequestDto getStrategyPostRequestDto() {
        return StrategyPostRequestDto.builder()
                .traderId(findMember().getId()) // todo. security 적용 후 제거 필요
                .methodId(findMethod().getId())
                .stockIdList(List.of(findStock().getId()))
                .name("테스트 전략")
                .content("테스트 전략입니다.")
                .cycle('P')
                .build();
    }
}
