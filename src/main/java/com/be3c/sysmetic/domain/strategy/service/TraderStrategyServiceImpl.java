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
import com.be3c.sysmetic.global.util.SecurityUtils;
import com.be3c.sysmetic.global.util.file.dto.FileReferenceType;
import com.be3c.sysmetic.global.util.file.dto.FileRequest;
import com.be3c.sysmetic.global.util.file.service.FileServiceImpl;
import lombok.RequiredArgsConstructor;
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
    private final FileServiceImpl fileService;
    private final SecurityUtils securityUtils;

    // 전략 등록
    @Override
    @Transactional
    public void insertStrategy(StrategyPostRequestDto requestDto, MultipartFile file) {
        checkDuplName(requestDto.getName());

        Strategy saveStrategy = Strategy.builder()
                .trader(findMember(securityUtils.getUserIdInSecurityContext()))
                .method(findMethod(requestDto.getMethodId()))
                .statusCode(StrategyStatusCode.PRIVATE.name())
                .name(requestDto.getName())
                .cycle(requestDto.getCycle())
                .content(requestDto.getContent())
                .followerCount(0L)
                .kpRatio(0.0)
                .smScore(0.0)
                .mdd(0.0)
                .accumulatedProfitLossRate(0.0)
                .winningRate(0.0)
                .build();

        strategyRepository.save(saveStrategy);

        // 파일 존재할 경우 제안서 등록
        if(file != null) {
            FileRequest fileRequest = new FileRequest(FileReferenceType.STRATEGY, saveStrategy.getId());
            fileService.uploadPdf(file, fileRequest);
        }

        insertStrategyStockReference(requestDto.getStockIdList(), saveStrategy);
    }

    // 전략 수정
    @Override
    public void updateStrategy(Long strategyId, StrategyPostRequestDto requestDto, MultipartFile file) {
        Strategy existingStrategy = findStrategy(strategyId);
        checkStatus(existingStrategy.getStatusCode());
        checkTrader(existingStrategy.getTrader().getId());

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

        // 파일 존재할 경우 제안서 수정
        if(file != null) {
            FileRequest fileRequest = new FileRequest(FileReferenceType.STRATEGY, existingStrategy.getId());
            fileService.updatePdf(file, fileRequest);
        }

        strategyRepository.save(existingStrategy);
    }

    // 전략 삭제
    @Override
    @Transactional
    public void deleteStrategy(Long strategyId) {
        Strategy existingStrategy = findStrategy(strategyId);
        checkTrader(existingStrategy.getTrader().getId());

        // 미사용 상태로 변경
        Strategy savedStrategy = strategyRepository.findById(strategyId).orElseThrow(() ->
                new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage()));

        savedStrategy.setStatusCode(StrategyStatusCode.NOT_USING_STATE.name());

        strategyRepository.save(savedStrategy);

        // 파일 존재할 경우 제안서 삭제
        FileRequest fileRequest = new FileRequest(FileReferenceType.STRATEGY, existingStrategy.getId());

        if(!fileService.getFilePath(fileRequest).isEmpty()) {
            fileService.deleteFile(fileRequest);
        }
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
        if(!securityUtils.getUserIdInSecurityContext().equals(traderId)) {
            throw new StrategyBadRequestException(StrategyExceptionMessage.INVALID_MEMBER.getMessage());
        }
    }

    // find member
    private Member findMember(Long traderId) {
        return memberRepository.findById(traderId).orElseThrow(() ->
                new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage()));
    }

    // find method
    private Method findMethod(Long methodId) {
        return methodRepository.findById(methodId).orElseThrow(() ->
                new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage()));
    }

    // find strategy
    private Strategy findStrategy(Long strategyId) {
        return strategyRepository.findById(strategyId).orElseThrow(() ->
                new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage()));
    }

    // 전략종목 교차테이블 저장
    private void insertStrategyStockReference(List<Long> stockIdList, Strategy strategy) {
        stockIdList.forEach((id) -> {
            Stock stock = stockRepository.findById(id).orElseThrow(() ->
                    new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage()));

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
                Stock stock = stockRepository.findById(stockId).orElseThrow(() ->
                        new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage()));
                
                return StrategyStockReference.builder()
                        .strategy(strategy)
                        .stock(stock)
                        .build();
            }).collect(Collectors.toList());

            strategyStockReferenceRepository.saveAll(addList);
        }
    }

}
