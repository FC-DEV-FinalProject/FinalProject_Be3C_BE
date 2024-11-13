package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.strategy.dto.SaveStrategyRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.StrategyStatusCode;
import com.be3c.sysmetic.domain.strategy.entity.Method;
import com.be3c.sysmetic.domain.strategy.entity.Stock;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.entity.StrategyStockReference;
import com.be3c.sysmetic.domain.strategy.exception.StrategyBadRequestException;
import com.be3c.sysmetic.domain.strategy.exception.StrategyExceptionMessage;
import com.be3c.sysmetic.domain.strategy.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@Service
public class InsertStrategyServiceImpl implements InsertStrategyService {

    private final StrategyRepository strategyRepository;

    private final MemberRepository memberRepository;

    private final MethodRepository methodRepository;

    private final StockRepository stockRepository;

    private final StrategyStockReferenceRepository strategyStockReferenceRepository;

    @Override
    @Transactional
    public Strategy insertStrategy(SaveStrategyRequestDto requestDto) {
        // 전략명 중복 여부 검증
        checkDuplicationName(requestDto.getName());

        // 종목 존재 여부 검증
        checkStock(requestDto.getStockIdList());

        // TODO 시큐리티 완료 후 멤버 적용
        Strategy strategy = Strategy.builder()
                .trader(findMember(requestDto.getTraderId()))
                .method(findMethod(requestDto.getMethodId()))
                .statusCode(StrategyStatusCode.PRIVATE.name()) // 비공개 설정
                .name(requestDto.getName())
                .cycle(requestDto.getCycle())
                .minOperationAmount(requestDto.getMinOperationAmount())
                .content(requestDto.getContent())
                .createdBy(requestDto.getTraderId())
                .modifiedBy(requestDto.getTraderId())
                .build();

        // DB 저장
        Strategy savedStrategy = strategyRepository.save(strategy);

        // 전략 종목 교차테이블 DB 저장
        insertStrategyStockReference(savedStrategy, requestDto.getStockIdList(), requestDto.getTraderId());

        return savedStrategy;
    }

    @Override
    public boolean returnIsDuplicationName(String name) {
        return strategyRepository.existsByName(name);
    }

    Member findMember(Long id) {
        if(id == null) {
            throw new StrategyBadRequestException(StrategyExceptionMessage.INVALID_VALUE.getMessage());
        }
        return memberRepository.findById(id).orElseThrow(() -> new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage()));
    }

    Method findMethod(Long id) {
        if(id == null) {
            throw new StrategyBadRequestException(StrategyExceptionMessage.INVALID_VALUE.getMessage());
        }
        return methodRepository.findById(id).orElseThrow(() -> new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage()));
    }

    void checkStock(List<Long> idList) {
        if (idList == null || idList.isEmpty()) {
            throw new StrategyBadRequestException(StrategyExceptionMessage.INVALID_VALUE.getMessage());
        }
    }

    public void checkDuplicationName(String name) {
        if(strategyRepository.existsByName(name)) {
            throw new StrategyBadRequestException(StrategyExceptionMessage.DUPLICATE_STRATEGY_NAME.getMessage());
        }
    }

    private void insertStrategyStockReference(Strategy strategy, List<Long> stockIdList, Long traderId) {
        stockIdList.forEach((id) -> {
            Stock stock = stockRepository.findById(id).orElseThrow(() -> new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage()));

            StrategyStockReference strategyStockReference = StrategyStockReference.builder()
                    .strategy(strategy)
                    .stock(stock)
                    .createdBy(traderId)
                    .modifiedBy(traderId)
                    .build();

            strategyStockReferenceRepository.save(strategyStockReference);
        });
    }
}
