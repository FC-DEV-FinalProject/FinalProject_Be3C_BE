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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StrategyServiceImpl implements StrategyService {

    @Autowired
    private StrategyRepository strategyRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MethodRepository methodRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private StrategyStockReferenceRepository strategyStockReferenceRepository;

    @Override
    @Transactional
    public Strategy insertStrategy(InsertStrategyRequestDto requestDto) {
        // 전략명 중복 여부 검증
        checkDuplicationName(requestDto.getName());
        checkStock(requestDto.getStockIdList());

        Strategy strategy = Strategy.builder()
                .trader(getMember(requestDto.getTraderId()))
                .method(getMethod(requestDto.getMethodId()))
                .statusCode(StrategyStatusCode.PRIVATE.name()) // 비공개 설정
                .name(requestDto.getName())
                .cycle(requestDto.getCycle())
                .minOperationAmount(requestDto.getMinOperationAmount())
                .content(requestDto.getContent())
                .createdBy(requestDto.getTraderId())
                .modifiedBy(requestDto.getTraderId())
                .build();

        Strategy savedStrategy = strategyRepository.save(strategy);
        insertStrategyStockReference(savedStrategy, requestDto.getStockIdList(), requestDto.getTraderId());
        return savedStrategy;
    }

    @Override
    public boolean confirmDuplicationName(String name) {
        return strategyRepository.existsByName(name);
    }

    Member getMember(Long id) {
        return memberRepository.findById(id).orElseThrow(() -> new StrategyBadRequestException("존재하지 않는 트레이더입니다."));
    }

    Method getMethod(Long id) {
        return methodRepository.findById(id).orElseThrow(() -> new StrategyBadRequestException("존재하지 않는 매매방식입니다."));
    }

    void checkStock(List<Long> idList) {
        if (idList == null) {
            throw new StrategyBadRequestException("종목 데이터가 없습니다.");
        }
    }

    public void checkDuplicationName(String name) {
        if(strategyRepository.existsByName(name)) {
            throw new StrategyBadRequestException("동일한 전략명이 이미 존재합니다.");
        }
    }

    private void insertStrategyStockReference(Strategy strategy, List<Long> stockIdList, Long traderId) {
        stockIdList.forEach((id) -> {
            Stock stock = stockRepository.findById(id).orElseThrow(() -> new StrategyBadRequestException("존재하지 않는 종목입니다."));

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
