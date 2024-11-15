package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.SaveStrategyRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.StrategyStatusCode;
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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@Service
public class UpdateStrategyServiceImpl implements UpdateStrategyService {

    /*
    트레이더 전략 기본정보 수정

    1. 클라이언트에서 전략 수정 post API 요청
    2. 트레이더 권한을 가진 멤버인지 확인
    3. 비공개 상태인지 확인
    4. request body로 받은 전략명, 매매방식, 주기, 종목(List), 최소운용금액, 전략소개내용 검증 후 DB 업데이트
     */

    private final StrategyRepository strategyRepository;
    private final MethodRepository methodRepository;
    private final StockRepository stockRepository;
    private final StrategyStockReferenceRepository strategyStockReferenceRepository;

    @Override
    @Transactional
    public Strategy updateStrategy(Long id, SaveStrategyRequestDto requestDto) {
        if(id == null || requestDto == null) throw new StrategyBadRequestException(StrategyExceptionMessage.INVALID_VALUE.getMessage());

        Strategy existingStrategy = strategyRepository.findById(id).orElseThrow(() -> new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage()));
        checkStatus(existingStrategy.getStatusCode());

        updateName(requestDto.getName(), existingStrategy);
        updateMethod(requestDto.getMethodId(), existingStrategy);
        updateCycle(requestDto.getCycle(), existingStrategy);
        updateStrategyStockReferences(existingStrategy, requestDto.getStockIdList());
        existingStrategy.setContent(requestDto.getContent());

        return strategyRepository.save(existingStrategy);
    }

    // 상태 검증 - 비공개 상태만 수정 가능
    void checkStatus(String status) {
        if(!status.equals(StrategyStatusCode.PRIVATE.name())) {
            throw new StrategyBadRequestException(StrategyExceptionMessage.INVALID_STATUS.getMessage());
        }
    }

    private void updateName(String name, Strategy existingStrategy) {
        if(name != null || !name.isEmpty()) {
            checkDuplicationName(name, existingStrategy.getName());
            existingStrategy.setName(name);
        }
    }

    private void updateMethod(Long methodId, Strategy existingStrategy) {
        if(methodId != null) {
            Method method = methodRepository.findById(methodId).orElseThrow(() -> new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage()));
            existingStrategy.setMethod(method);
        }
    }

    private void updateCycle(Character cycle, Strategy existingStrategy) {
        if(cycle != null) {
            if(cycle == 'D' || cycle == 'P') {
                existingStrategy.setCycle(cycle);
            } else {
                // null이 아니고, 다른 값일 경우 exception
                throw new StrategyBadRequestException(StrategyExceptionMessage.INVALID_VALUE.getMessage());
            }
        }
    }

    @Transactional
    void updateStrategyStockReferences(Strategy strategy, List<Long> stockIdList) {
        List<Long> currentStockIdList = strategyStockReferenceRepository.findStockIdsByStrategyId(strategy.getId());

        // currentStockIdList - stockIdList(request) 차집합 계산
        Set<Long> stockIdListToDelete = new HashSet<>(currentStockIdList);
        stockIdListToDelete.removeAll(stockIdList);

        // stockIdList(request) - currentStockIdList 차집합 계산
        Set<Long> stockIdListToAdd = new HashSet<>(stockIdList);
        stockIdListToAdd.removeAll(currentStockIdList);

        // delete
        if(!stockIdListToAdd.isEmpty()) {
            strategyStockReferenceRepository.deleteByStrategyIdAndStockIds(strategy.getId(), stockIdListToDelete);
        }

        // insert
        if(!stockIdListToAdd.isEmpty()) {
            List<StrategyStockReference> addList = stockIdListToAdd.stream()
                    .map(stockId -> {
                        Stock stock = stockRepository.findById(stockId).orElseThrow(() -> new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage()));
                        return StrategyStockReference.builder()
                                .strategy(strategy)
                                .stock(stock)
                                .build();
                    }).collect(Collectors.toList());

            strategyStockReferenceRepository.saveAll(addList);
        }
    }

    /*
    전략 수정시 전략명 중복 검증
    - existsByName() 사용시 현재 전략의 전략명까지 체크 -> 기존 전략명 사용 불가
    - 현재 전략명과 다를 경우에만 검증
     */
    public void checkDuplicationName(String name, String existingStrategyName) {
        if(!name.equals(existingStrategyName) && strategyRepository.existsByName(name)) {
            throw new StrategyBadRequestException(StrategyExceptionMessage.DUPLICATE_STRATEGY_NAME.getMessage());
        }
    }
}
