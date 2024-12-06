package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.member.entity.MemberRole;
import com.be3c.sysmetic.domain.strategy.dto.*;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.exception.StrategyBadRequestException;
import com.be3c.sysmetic.domain.strategy.exception.StrategyExceptionMessage;
import com.be3c.sysmetic.domain.strategy.repository.*;
import com.be3c.sysmetic.domain.strategy.util.DoubleHandler;
import com.be3c.sysmetic.domain.strategy.util.StockGetter;
import com.be3c.sysmetic.global.common.Code;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import com.be3c.sysmetic.global.util.SecurityUtils;
import com.be3c.sysmetic.global.util.file.dto.FileReferenceType;
import com.be3c.sysmetic.global.util.file.dto.FileRequest;
import com.be3c.sysmetic.global.util.file.service.FileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static com.be3c.sysmetic.global.common.Code.*;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StrategyServiceImpl implements StrategyService {
    private final MethodRepository methodRepository;
    private final StockRepository stockRepository;
    private final StrategyDetailRepository strategyDetailRepository;
    private final StrategyStatisticsRepository strategyStatisticsRepository;
    private final FileService fileService;
    private final StockGetter stockGetter;
    private final DoubleHandler doubleHandler;
    private final StrategyRepository strategyRepository;
    private final SecurityUtils securityUtils;

    @Override
    public StrategyDetailDto getStrategy(Long id) {
        StrategyDetailStatistics statistics = strategyStatisticsRepository.findStrategyDetailStatistics(id);
        StrategyDetailDto detailDto = strategyDetailRepository.findPublicStrategy(id)
                .map(strategy -> StrategyDetailDto.builder()
                        .id(strategy.getId())
                        .traderId(strategy.getTrader().getId())
                        .traderNickname(strategy.getTrader().getNickname())
                        .traderProfileImage(fileService.getFilePathNullable(new FileRequest(FileReferenceType.MEMBER, strategy.getTrader().getId())))
                        .methodId(strategy.getMethod().getId())
                        .methodName(strategy.getMethod().getName())
                        .methodIconPath(fileService.getFilePathNullable(new FileRequest(FileReferenceType.METHOD, strategy.getMethod().getId())))
                        .stockList(stockGetter.getStocks(strategy.getId()))
                        .isFollow(false)
                        .name(strategy.getName())
                        .statusCode(strategy.getStatusCode())
                        .cycle(strategy.getCycle())
                        .content(strategy.getContent())
                        .followerCount(strategy.getFollowerCount())
                        .mdd(strategy.getMdd())
                        .kpRatio(strategy.getKpRatio())
                        .smScore(strategy.getSmScore())
                        .accumulatedProfitLossRate(strategy.getAccumulatedProfitLossRate())
                        .maximumCapitalReductionAmount(statistics.getMaximumCapitalReductionAmount())
                        .averageProfitLossRate(doubleHandler.cutDouble(statistics.getAverageProfitLossRate()))
                        .profitFactor(doubleHandler.cutDouble(statistics.getProfitFactor()))
                        .winningRate(strategy.getWinningRate())
                        .monthlyRecord(null)
                        .fileWithInfoResponse(
                                fileService.getFileWithInfoNullable(new FileRequest(FileReferenceType.STRATEGY, strategy.getId()))
                        )
                        .build())
                .orElseThrow(() -> new NoSuchElementException("수정할 전략을 찾지 못했습니다."));

        checkAllowUpdate(detailDto.getTraderId());

        return detailDto;
    }

    @Override
    public boolean privateStrategy(Long id) {
        Long userId = securityUtils.getUserIdInSecurityContext();
        Strategy strategy = strategyRepository.findByIdAndTraderId(id, userId).orElseThrow(EntityNotFoundException::new);

        strategy.setStatusCode(StrategyStatusCode.PRIVATE.getCode());
        strategyRepository.save(strategy);
        return true;
    }

    @Override
    public MethodAndStockGetResponseDto findMethodAndStock() {
        List<MethodGetResponseDto> methodList = methodRepository.findAllByStatusCode(Code.USING_STATE.getCode());
        if(methodList.isEmpty()) throw new EntityNotFoundException();

        List<StockGetResponseDto> stockList = stockRepository.findAllByStatusCode(Code.USING_STATE.getCode());
        if(stockList.isEmpty()) throw new EntityNotFoundException();

        return MethodAndStockGetResponseDto.builder()
                .methodList(methodList)
                .stockList(stockList)
                .build();
    }

    private void checkAllowUpdate(Long traderId) {
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
}
