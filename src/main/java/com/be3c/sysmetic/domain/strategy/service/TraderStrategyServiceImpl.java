package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.entity.MemberRole;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.domain.strategy.dto.*;
import com.be3c.sysmetic.domain.strategy.entity.*;
import com.be3c.sysmetic.domain.strategy.exception.StrategyBadRequestException;
import com.be3c.sysmetic.domain.strategy.exception.StrategyExceptionMessage;
import com.be3c.sysmetic.domain.strategy.repository.*;
import com.be3c.sysmetic.domain.strategy.util.PathGetter;
import com.be3c.sysmetic.domain.strategy.util.StockGetter;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import com.be3c.sysmetic.global.common.response.PageResponse;
import com.be3c.sysmetic.global.util.SecurityUtils;
import com.be3c.sysmetic.global.util.file.dto.FileReferenceType;
import com.be3c.sysmetic.global.util.file.dto.FileRequest;
import com.be3c.sysmetic.global.util.file.service.FileServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final StockGetter stockGetter;
    private final PathGetter pathGetter;
    private final StrategyStatisticsRepository strategyStatisticsRepository;

    // 전략 등록
    @Override
    @Transactional
    public StrategyPostResponseDto insertStrategy(StrategyPostRequestDto requestDto, MultipartFile file) {
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

        // 기본 전략 통계 저장
        strategyStatisticsRepository.save(new StrategyStatistics(saveStrategy));

        return StrategyPostResponseDto.builder().strategyId(saveStrategy.getId()).build();
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
    public void deleteStrategy(StrategyDeleteRequestDto requestDto) {

        Strategy existingStrategy = findStrategy(requestDto.getStrategyIdList().stream().findFirst().get());
        checkAllowDelete(existingStrategy.getTrader().getId());

        for(Long strategyId : requestDto.getStrategyIdList()) {
            // 미사용 상태로 변경
            Strategy savedStrategy = strategyRepository.findById(strategyId).orElseThrow(() ->
                    new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage(), ErrorCode.NOT_FOUND));

            savedStrategy.setStatusCode(StrategyStatusCode.NOT_USING_STATE.name());

            // 파일 존재할 경우 제안서 삭제
            // todo. 파일 수정 필요
//            FileRequest fileRequest = new FileRequest(FileReferenceType.STRATEGY, existingStrategy.getId());
//
//            if(fileService.getFilePath(fileRequest) != null) {
//                fileService.deleteFile(fileRequest);
//            }
        }
    }

    // 전략명 중복 여부 검증
    private void checkDuplName(String name) {
        if(strategyRepository.existsByName(name)) {
            throw new StrategyBadRequestException(StrategyExceptionMessage.DUPLICATE_STRATEGY_NAME.getMessage(), ErrorCode.DUPLICATE_RESOURCE);
        }
    }

    // 상태 검증 - 비공개 상태만 기본정보 수정 가능
    private void checkStatus(String status) {
        if(!status.equals(StrategyStatusCode.PRIVATE.name())) {
            throw new StrategyBadRequestException(StrategyExceptionMessage.INVALID_STATUS.getMessage(), ErrorCode.DISABLED_DATA_STATUS);
        }
    }

    private void checkAllowDelete(Long traderId) {
        if(!(
                securityUtils.getUserRoleInSecurityContext().equals(MemberRole.ADMIN.name()) ||
                securityUtils.getUserRoleInSecurityContext().equals(MemberRole.USER_MANAGER.name()) ||
                securityUtils.getUserRoleInSecurityContext().equals(MemberRole.TRADER_MANAGER.name()) ||
                securityUtils.getUserIdInSecurityContext().equals(traderId)
            )
        ) {
            throw new StrategyBadRequestException(StrategyExceptionMessage.INVALID_MEMBER.getMessage(), ErrorCode.FORBIDDEN);
        }
    }

    // member 검증
    private void checkTrader(Long traderId) {
        if(!securityUtils.getUserIdInSecurityContext().equals(traderId)) {
            throw new StrategyBadRequestException(StrategyExceptionMessage.INVALID_MEMBER.getMessage(), ErrorCode.FORBIDDEN);
        }
    }

    // find member
    private Member findMember(Long traderId) {
        return memberRepository.findById(traderId).orElseThrow(() ->
                new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage(), ErrorCode.NOT_FOUND));
    }

    // find method
    private Method findMethod(Long methodId) {
        return methodRepository.findById(methodId).orElseThrow(() ->
                new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage(), ErrorCode.NOT_FOUND));
    }

    // find strategy
    private Strategy findStrategy(Long strategyId) {
        return strategyRepository.findById(strategyId).orElseThrow(() ->
                new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage(), ErrorCode.NOT_FOUND));
    }

    // 전략종목 교차테이블 저장
    private void insertStrategyStockReference(List<Long> stockIdList, Strategy strategy) {
        stockIdList.forEach((id) -> {
            Stock stock = stockRepository.findById(id).orElseThrow(() ->
                    new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage(), ErrorCode.NOT_FOUND));

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
                        new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage(), ErrorCode.NOT_FOUND));
                
                return StrategyStockReference.builder()
                        .strategy(strategy)
                        .stock(stock)
                        .build();
            }).collect(Collectors.toList());

            strategyStockReferenceRepository.saveAll(addList);
        }
    }

    @Override
    public MyStrategyListResponseDto getMyStrategyList(Integer page) {
        Long userId = securityUtils.getUserIdInSecurityContext();

        Member member = findMember(userId);

        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt"));

        Page<MyStrategyListDto> findPage = strategyRepository.findPageMyStrategy(member.getId(), pageable);

        findPage.getContent().forEach(strategy -> {
            strategy.setMethodIconPath(pathGetter.getMethodIconPath(strategy.getMethodId()));
            strategy.setStockList(stockGetter.getStocks(strategy.getStrategyId()));
        });

        return MyStrategyListResponseDto.builder()
                .traderId(member.getId())
                .traderNickname(member.getNickname())
                .traderProfileImage(pathGetter.getMemberProfilePath(member.getId()))
                .totalfollowers(member.getTotalFollow())
                .strategyList(PageResponse.<MyStrategyListDto>builder()
                        .totalElement(findPage.getTotalElements())
                        .totalPages(findPage.getTotalPages())
                        .currentPage(findPage.getNumber())
                        .pageSize(findPage.getSize())
                        .content(findPage.getContent())
                        .build())
                .build();
    }
}
