package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.domain.strategy.dto.*;
import com.be3c.sysmetic.domain.strategy.entity.Method;
import com.be3c.sysmetic.domain.strategy.entity.Stock;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.entity.StrategyStockReference;
import com.be3c.sysmetic.domain.strategy.exception.StrategyBadRequestException;
import com.be3c.sysmetic.domain.strategy.exception.StrategyExceptionMessage;
import com.be3c.sysmetic.domain.strategy.repository.MethodRepository;
import com.be3c.sysmetic.domain.strategy.repository.StockRepository;
import com.be3c.sysmetic.domain.strategy.repository.StrategyRepository;
import com.be3c.sysmetic.domain.strategy.repository.StrategyStockReferenceRepository;
import com.be3c.sysmetic.global.common.Code;
import com.be3c.sysmetic.global.util.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@Service
public class TraderStrategyServiceImpl implements TraderStrategyService {

    private final StrategyRepository strategyRepository;
    private final MemberRepository memberRepository;
    private final MethodRepository methodRepository;
    private final StockRepository stockRepository;
    private final StrategyStockReferenceRepository strategyStockReferenceRepository;
    private final SecurityUtils securityUtils;

    // 전략 등록
    @Override
    @Transactional
    public void insertStrategy(StrategyPostRequestDto requestDto, MultipartFile file) {
        checkDuplName(requestDto.getName());
        // todo. Long traderId = securityUtils.getUserIdInSecurityContext();

        Strategy saveStrategy = Strategy.builder()
                .trader(findMember(requestDto.getTraderId())) // todo. security 적용 후 제거 필요
                .method(findMethod(requestDto.getMethodId()))
                .statusCode(StrategyStatusCode.PRIVATE.name())
                .name(requestDto.getName())
                .cycle(requestDto.getCycle())
                .content(requestDto.getContent())
                .build();

        strategyRepository.save(saveStrategy);

        // todo. 전략 제안서 파일 등록 로직 필요

        insertStrategyStockReference(requestDto.getStockIdList(), saveStrategy);
    }

    // 전략 수정
    @Override
    public void updateStrategy(Long strategyId, StrategyPostRequestDto requestDto, MultipartFile file) {
        Strategy existingStrategy = findStrategy(strategyId);

        checkStatus(existingStrategy.getStatusCode());
        // todo. checkTrader(existingStrategy.getCreatedBy());

        // update
        if(!requestDto.getName().equals(existingStrategy.getName())) {
            checkDuplName(requestDto.getName());
            existingStrategy.setName(requestDto.getName());
        }

        if(!requestDto.getMethodId().equals(existingStrategy.getMethod().getId())) {
            existingStrategy.setMethod(findMethod(requestDto.getMethodId()));
        }

        if(!requestDto.getCycle().equals(existingStrategy.getCycle())) {
            existingStrategy.setCycle(requestDto.getCycle());
        }

        if(!requestDto.getContent().equals(existingStrategy.getContent())) {
            existingStrategy.setContent(requestDto.getContent());
        }

        updateStrategyStockReferences(existingStrategy, requestDto.getStockIdList());

        // todo. 전략 제안서 파일 수정 로직 필요

        strategyRepository.save(existingStrategy);
    }

    // 전략 삭제
    @Override
    @Transactional
    public void deleteStrategy(Long strategyId) {
        findStrategy(strategyId);
        // todo. checkTrader(existingStrategy.getCreatedBy());
        // todo. 전략 제안서 파일 삭제 로직 필요

        // DB 삭제가 아닌 미사용 상태로 변경
        // strategyStockReferenceRepository.deleteByStrategyId(strategyId);
        // strategyRepository.deleteById(strategyId);

        Strategy savedStrategy = strategyRepository.findById(strategyId).orElseThrow(() -> new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage()));
        savedStrategy.setStatusCode(StrategyStatusCode.NOT_USING_STATE.name());
        strategyRepository.save(savedStrategy);
    }

    // 전략명 중복 여부 검증
    private void checkDuplName(String name) {
        if(strategyRepository.existsByName(name)) {
            throw new StrategyBadRequestException(StrategyExceptionMessage.DUPLICATE_STRATEGY_NAME.getMessage());
        }
    }

    // 상태 검증 - 비공개 상태만 기본정보 수정 가능
    private void checkStatus(String status) {
        if(!status.equals(StrategyStatusCode.PRIVATE.name())) {
            throw new StrategyBadRequestException(StrategyExceptionMessage.INVALID_STATUS.getMessage());
        }
    }

    // member 검증
    private void checkTrader(Long traderId) {
        if(securityUtils.getUserIdInSecurityContext() != traderId) {
            throw new StrategyBadRequestException(StrategyExceptionMessage.INVALID_MEMBER.getMessage());
        }
    }

    // find member
    private Member findMember(Long traderId) {
        return memberRepository.findById(traderId).orElseThrow(() -> new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage()));
    }

    // find method
    private Method findMethod(Long methodId) {
        return methodRepository.findById(methodId).orElseThrow(() -> new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage()));
    }

    // find strategy
    private Strategy findStrategy(Long strategyId) {
        return strategyRepository.findById(strategyId).orElseThrow(() -> new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage()));
    }

    // 전략종목 교차테이블 저장
    private void insertStrategyStockReference(List<Long> stockIdList, Strategy strategy) {
        stockIdList.forEach((id) -> {
            Stock stock = stockRepository.findById(id).orElseThrow(() -> new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage()));

            StrategyStockReference strategyStockReference = StrategyStockReference.builder()
                    .strategy(strategy)
                    .stock(stock)
                    .build();

            strategyStockReferenceRepository.save(strategyStockReference);
        });
    }

    // 전략종목 교차테이블 업데이트
    private void updateStrategyStockReferences(Strategy strategy, List<Long> updateStockIdList) {
        List<Long> existingStockIdList = strategyStockReferenceRepository.findStockIdsByStrategyId(strategy.getId());

        Set<Long> toDeleteIdList = new HashSet<>(existingStockIdList);
        toDeleteIdList.removeAll(updateStockIdList);

        Set<Long> toAddIdList = new HashSet<>(updateStockIdList);
        toAddIdList.removeAll(existingStockIdList);

        if(!toDeleteIdList.isEmpty()) {
            strategyStockReferenceRepository.deleteByStrategyIdAndStockIds(strategy.getId(), toDeleteIdList);
        }

        if(!toAddIdList.isEmpty()) {
            List<StrategyStockReference> addList = toAddIdList.stream().map(stockId -> {
                Stock stock = stockRepository.findById(stockId).orElseThrow(() -> new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage()));
                return StrategyStockReference.builder()
                        .strategy(strategy)
                        .stock(stock)
                        .build();
            }).collect(Collectors.toList());

            strategyStockReferenceRepository.saveAll(addList);
        }
    }

}