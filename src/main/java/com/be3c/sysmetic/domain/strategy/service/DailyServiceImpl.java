package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.DailyGetResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.DailyRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.DailyPostResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.StrategyStatusCode;
import com.be3c.sysmetic.domain.strategy.repository.StrategyStatisticsRepository;
import com.be3c.sysmetic.domain.strategy.entity.Daily;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.exception.StrategyBadRequestException;
import com.be3c.sysmetic.domain.strategy.exception.StrategyExceptionMessage;
import com.be3c.sysmetic.domain.strategy.repository.DailyRepository;
import com.be3c.sysmetic.domain.strategy.repository.MonthlyRepository;
import com.be3c.sysmetic.domain.strategy.repository.StrategyRepository;
import com.be3c.sysmetic.domain.strategy.util.StrategyCalculator;
import com.be3c.sysmetic.domain.strategy.util.StrategyViewAuthorize;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import com.be3c.sysmetic.global.common.response.PageResponse;
import com.be3c.sysmetic.global.util.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@Slf4j
@Service
public class DailyServiceImpl implements DailyService {
    private final DailyRepository dailyRepository;
    private final MonthlyRepository monthlyRepository;
    private final StrategyRepository strategyRepository;
    private final StrategyStatisticsRepository statisticsRepository;
    private final StrategyViewAuthorize strategyViewAuthorize;

    private final StrategyCalculator strategyCalculator;
    private final MonthlyServiceImpl monthlyService;
    private final SecurityUtils securityUtils;

    // 일간분석 등록
    @Transactional
    @Override
    public void saveDaily(Long strategyId, List<DailyRequestDto> requestDtoList) {
        Strategy exitingStrategy = strategyRepository.findById(strategyId).orElseThrow(() ->
                new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage(), ErrorCode.NOT_FOUND));

        // user 검증
        validUser(exitingStrategy.getTrader().getId());

        // 요청 객체 검증 및 entity 변환
        List<Daily> dailyList = requestDtoListToEntity(strategyId, null, requestDtoList);

        // 일간분석 등록
        dailyRepository.saveAll(dailyList);

        // 누적금액 갱신
        dailyList.forEach(daily -> recalculateAccumulatedData(strategyId, daily.getDate()));

        // 월간분석 갱신
        List<LocalDate> updatedDateList = dailyList
                .stream().map(Daily::getDate).collect(Collectors.toList());

        monthlyService.updateMonthly(strategyId, updatedDateList);
    }

    // 일간분석 수정
    @Transactional
    @Override
    public void updateDaily(Long dailyId, DailyRequestDto requestDto) {
        Daily exitingDaily = dailyRepository.findById(dailyId).orElseThrow(() ->
                new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage(), ErrorCode.NOT_FOUND));

        Long strategyId = exitingDaily.getStrategy().getId();

        Strategy exitingStrategy = strategyRepository.findById(strategyId).orElseThrow(() ->
                new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage(), ErrorCode.NOT_FOUND));

        // user 검증
        validUser(exitingStrategy.getTrader().getId());

        // 요청 객체 검증 및 entity 변환
        Daily daily = requestDtoListToEntity(strategyId, dailyId, List.of(requestDto))
                .stream().findFirst().orElseThrow(() ->
                        new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage(), ErrorCode.NOT_FOUND));

        // 일간분석 수정
        dailyRepository.save(daily);

        // 누적금액 갱신
        recalculateAccumulatedData(strategyId, daily.getDate());

        // 월간분석 갱신
        List<LocalDate> updatedDateList = List.of(daily.getDate());
        monthlyService.updateMonthly(strategyId, updatedDateList);
    }

    // 일간분석 삭제
    @Transactional
    @Override
    public void deleteDaily(Long dailyId) {
        Daily exitingDaily = dailyRepository.findById(dailyId).orElseThrow(() ->
                new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage(), ErrorCode.NOT_FOUND));

        Long strategyId = exitingDaily.getStrategy().getId();

        Strategy exitingStrategy = strategyRepository.findById(strategyId).orElseThrow(() ->
                new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage(), ErrorCode.NOT_FOUND));

        // user 검증
        validUser(exitingStrategy.getTrader().getId());

        // DB 삭제
        dailyRepository.deleteById(dailyId);

        // 삭제 후 일간분석 데이터 수
        Long countDaily = countDaily(strategyId);

        // 누적금액 갱신
        recalculateAccumulatedData(strategyId, exitingDaily.getDate());

        // 월간분석 갱신
        List<LocalDate> updatedDateList = List.of(exitingDaily.getDate());
        monthlyService.updateMonthly(strategyId, updatedDateList);

        if(countDaily < 3) {
            // 일간분석 데이터 수가 3 미만일 경우 비공개 전환
            strategyRepository.updateStatusToPrivate(strategyId);
        }

        if(countDaily == 0) {
            // 모든 일간분석 데이터 삭제시 전략 통계, 월간분석 데이터 삭제
            monthlyRepository.deleteAllByStrategyId(strategyId);
            statisticsRepository.deleteByStrategyId(strategyId);
        }
    }

    // 일간분석 조회 - PUBLIC 상태인 전략의 일간분석 데이터 조회
    @Override
    public PageResponse<DailyGetResponseDto> findDaily(Long strategyId, Integer page, LocalDate startDate, LocalDate endDate) {
        Pageable pageable = PageRequest.of(page, 10);

        Strategy strategy = strategyRepository.findById(strategyId).orElseThrow(() ->
                new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage(), ErrorCode.NOT_FOUND));

        strategyViewAuthorize.Authorize(strategy);

        Page<DailyGetResponseDto> dailyResponseDtoPage = dailyRepository
                .findAllByStrategyIdAndDateBetween(strategyId, startDate, endDate, pageable)
                .map(this::entityToDto);

        // 페이지 요청이 제대로 된 것인지 확인 ( 페이지 안에 데이터가 하나라도 존재한다면 )
        if(dailyResponseDtoPage.hasContent()) {
            return PageResponse.<DailyGetResponseDto>builder()
                    .currentPage(dailyResponseDtoPage.getPageable().getPageNumber())
                    .pageSize(dailyResponseDtoPage.getPageable().getPageSize())
                    .totalElement(dailyResponseDtoPage.getTotalElements())
                    .totalPages(dailyResponseDtoPage.getTotalPages())
                    .content(dailyResponseDtoPage.getContent())
                    .build();
        }

        // 페이지 안에 데이터가 하나라도 존재하지 않는다면 ( 잘못된 페이지 요청 )
        throw new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage(), ErrorCode.NOT_FOUND);
    }

    /*
    해당 부분 미사용으로 인한 삭제
    
    일간분석 조회 - 트레이더 또는 관리자의 일간분석 데이터 조회
    1) 트레이더
    본인의 전략이면서 공개, 비공개, 승인대기 상태의 전략 조회 가능
    2) 관리자
    모든 상태의 전략 조회 가능
     */
//    @Override
//    public PageResponse<DailyGetResponseDto> findTraderDaily(Long strategyId, Integer page, LocalDate startDate, LocalDate endDate) {
//        Strategy exitingStrategy = strategyRepository.findById(strategyId).orElseThrow(() ->
//                new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage(), ErrorCode.NOT_FOUND));
//
//        Pageable pageable = PageRequest.of(page, 10);
//
//        String userRole = securityUtils.getUserRoleInSecurityContext();
//
//        // trader일 경우, 본인의 전략인지 검증
//        if(userRole.equals("TRADER")) {
//           validUser(exitingStrategy.getTrader().getId());
//        }
//
//        // member일 경우, 권한 없음 처리
//        if(userRole.equals("USER")) {
//            throw new StrategyBadRequestException(StrategyExceptionMessage.INVALID_MEMBER.getMessage(), ErrorCode.FORBIDDEN);
//        }
//
//        // 전략 상태 NOT_USING_STATE 일 경우 예외 처리
//        Strategy strategy = strategyRepository.findById(strategyId).orElseThrow(() ->
//                new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage(), ErrorCode.NOT_FOUND));
//
//        if(strategy.getStatusCode().equals(StrategyStatusCode.NOT_USING_STATE.name())) {
//            throw new StrategyBadRequestException(StrategyExceptionMessage.INVALID_STATUS.getMessage(), ErrorCode.DISABLED_DATA_STATUS);
//        }
//
//        Page<DailyGetResponseDto> dailyResponseDtoPage = dailyRepository
//                .findAllByStrategyIdAndDateBetween(strategyId, startDate, endDate, pageable)
//                .map(this::entityToDto);
//
//        return PageResponse.<DailyGetResponseDto>builder()
//                .currentPage(dailyResponseDtoPage.getPageable().getPageNumber())
//                .pageSize(dailyResponseDtoPage.getPageable().getPageSize())
//                .totalElement(dailyResponseDtoPage.getTotalElements())
//                .totalPages(dailyResponseDtoPage.getTotalPages())
//                .content(dailyResponseDtoPage.getContent())
//                .build();
//    }

    // 중복 여부 조회
    @Override
    public DailyPostResponseDto getIsDuplicate(Long strategyId, List<DailyRequestDto> requestDtoList) {
        for (DailyRequestDto requestDto : requestDtoList) {
            // 이미 등록한 날짜가 하나라도 존재할 경우 true 반환
            if(dailyRepository.findByStrategyIdAndDate(strategyId, requestDto.getDate()) != null) {
                return DailyPostResponseDto.builder()
                        .isDuplicate(true)
                        .build();
            }
        }
        return DailyPostResponseDto.builder()
                .isDuplicate(false)
                .build();
    }

    private List<Daily> requestDtoListToEntity(Long strategyId, Long dailyId, List<DailyRequestDto> requestDtoList) {
        final Daily beforeDaily = dailyRepository.findTopByStrategyIdOrderByDateDesc(strategyId);
        List<Daily> dailyList = new ArrayList<>();

        // 요청 데이터가 여러 개일 경우, date 기준 최신순으로 정렬
        if(requestDtoList.size() > 1) {
            requestDtoList.sort(Comparator.comparing(DailyRequestDto::getDate).reversed());
        }

        for(DailyRequestDto requestDto : requestDtoList) {
            Daily existingDaily = dailyRepository.findByStrategyIdAndDate(strategyId, requestDto.getDate());

            // 일간분석 등록이면서 이미 존재하는 일자일 경우 미저장
            if(dailyId == null && existingDaily != null) continue;

            if(dailyList.stream().map(daily -> daily.getDate()).toList().contains(requestDto.getDate())) {
                continue;
            }

            if(dailyList.isEmpty()) {
                if(beforeDaily == null) {
                    // DB 데이터 미존재
                    dailyList.add(dtoToEntity(dailyId, strategyId, true, requestDto, 0.0, 0.0, 0.0));
                } else {
                    // DB 데이터 존재
                    dailyList.add(dtoToEntity(dailyId, strategyId, false, requestDto, beforeDaily.getPrincipal(), beforeDaily.getCurrentBalance(), beforeDaily.getStandardAmount()));
                }
            } else {
                // 요청 데이터가 여러 개일 경우, 이전 요청 데이터
                Daily beforeRequestDaily = dailyList.get(dailyList.size()-1);
                dailyList.add(dtoToEntity(dailyId, strategyId, false, requestDto, beforeRequestDaily.getPrincipal(), beforeRequestDaily.getCurrentBalance(), beforeRequestDaily.getStandardAmount()));
            }
        }

        return dailyList;
    }

    // 현재 로그인한 유저와 전략 업로드한 유저가 일치하는지 검증
    private void validUser(Long traderId) {
        if(!securityUtils.getUserIdInSecurityContext().equals(traderId)) {
            throw new StrategyBadRequestException(StrategyExceptionMessage.INVALID_MEMBER.getMessage(), ErrorCode.FORBIDDEN);
        }
    }

    // 일간분석 총 개수 조회
    private Long countDaily(Long strategyId) {
        return dailyRepository.countByStrategyId(strategyId);
    }

    private Daily dtoToEntity(Long dailyId, Long strategyId, boolean isFirst, DailyRequestDto requestDto, Double beforePrincipal, Double beforeBalance, Double beforeStandardAmount) {
        // 일손익률
        Double dailyProfitLossRate = strategyCalculator.getDailyProfitLossRate(
                isFirst,
                requestDto.getDepositWithdrawalAmount(),
                requestDto.getDailyProfitLossAmount(),
                beforeBalance,
                beforePrincipal,
                beforeStandardAmount
        );

        return Daily.builder()
                .id(dailyId) // 등록일 경우 null
                .strategy(strategyRepository.findById(strategyId).orElseThrow(() ->
                        new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage(), ErrorCode.NOT_FOUND)))
                .date(requestDto.getDate())
                .principal(strategyCalculator.getPrincipal(isFirst, requestDto.getDepositWithdrawalAmount(), beforePrincipal, beforeBalance)) // 원금
                .depositWithdrawalAmount(requestDto.getDepositWithdrawalAmount()) // 입출금
                .profitLossAmount(requestDto.getDailyProfitLossAmount()) // 일손익금
                .profitLossRate(dailyProfitLossRate) // 일손익률
                .currentBalance(strategyCalculator.getCurrentBalance(isFirst, beforeBalance, requestDto.getDepositWithdrawalAmount(), requestDto.getDailyProfitLossAmount())) // 잔고
                .standardAmount(strategyCalculator.getStandardAmount(isFirst, requestDto.getDepositWithdrawalAmount(), requestDto.getDailyProfitLossAmount(), beforeBalance, beforePrincipal)) // 기준가
                .accumulatedProfitLossAmount(isFirst ? requestDto.getDailyProfitLossAmount() : getAccumulatedProfitLossAmount(strategyId, requestDto.getDailyProfitLossAmount())) // 누적손익금액
                .accumulatedProfitLossRate(isFirst ? dailyProfitLossRate : getAccumulatedProfitLossRate(strategyId, dailyProfitLossRate)) // 누적손익률
                .build();
    }

    private DailyGetResponseDto entityToDto(Daily daily) {
        return DailyGetResponseDto.builder()
                .dailyId(daily.getId())
                .date(daily.getDate())
                .principal(daily.getPrincipal())
                .depositWithdrawalAmount(daily.getDepositWithdrawalAmount())
                .profitLossAmount(daily.getProfitLossAmount())
                .profitLossRate(daily.getProfitLossRate())
                .accumulatedProfitLossAmount(daily.getAccumulatedProfitLossAmount())
                .accumulatedProfitLossRate(daily.getAccumulatedProfitLossRate())
                .build();
    }

    private Double getAccumulatedProfitLossAmount(Long strategyId, Double profitLossAmount) {
        Double accumulatedProfitLossAmount = dailyRepository.findTotalProfitLossAmountByStrategyId(strategyId);
        if(accumulatedProfitLossAmount == null) accumulatedProfitLossAmount = 0.0;
        accumulatedProfitLossAmount += profitLossAmount; // 현재 손익금 추가
        return accumulatedProfitLossAmount;
    }

    private Double getAccumulatedProfitLossRate(Long strategyId, Double profitLossRate) {
        Double accumulativeProfitLossRate = dailyRepository.findTotalProfitLossRateByStrategyId(strategyId);
        if(accumulativeProfitLossRate == null) accumulativeProfitLossRate = 0.0;
        accumulativeProfitLossRate += profitLossRate; // 현재 손익률 추가
        return accumulativeProfitLossRate;
    }

    // 누적 데이터 갱신
    public void recalculateAccumulatedData(Long strategyId, LocalDate startDate) {
        Double accumulativeProfitLossAmount = 0.0;
        Double accumulativeProfitLossRate = 0.0;

        // 수정 또는 삭제 이후의 데이터 조회
        List<Daily> dailyList = dailyRepository.findAllByStrategyIdAndDateAfterOrderByDateAsc(strategyId, startDate);

        // 수정 또는 삭제 이전 데이터의 누적손익금액, 누적손익률 조회
        Daily beforeDaily = dailyRepository.findFirstByStrategyIdAndDateBeforeOrderByDateDesc(strategyId, startDate);
        if (beforeDaily != null) {
            accumulativeProfitLossAmount = beforeDaily.getAccumulatedProfitLossAmount();
            accumulativeProfitLossRate = beforeDaily.getAccumulatedProfitLossRate();
        }

        // 누적손익금액, 누적손익률 다시 계산
        for (Daily daily : dailyList) {
            accumulativeProfitLossAmount += daily.getProfitLossAmount();
            accumulativeProfitLossRate += daily.getProfitLossRate();

            // 누적손익금액 및 누적손익률 업데이트
            daily.setAccumulatedProfitLossAmount(accumulativeProfitLossAmount);
            daily.setAccumulatedProfitLossRate(accumulativeProfitLossRate);
        }

        // DB 업데이트
        dailyRepository.saveAll(dailyList);
    }

// kp ratio, sm score 갱신
//    public void updateKpRatioAndSmScore(Long strategyId) {
//        Strategy savedStrategy = strategyRepository.findById(strategyId).orElseThrow(() -> new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage()));
//        savedStrategy.setKpRatio(getKpRatio(strategyId));
//        savedStrategy.setSmScore(getSmScore(strategyId, savedStrategy.getKpRatio()));
//        strategyRepository.save(savedStrategy);
//    }
//    private Double getKpRatio(Long strategyId) {
//        List<Daily> dailyList = dailyRepository.findAllByStrategyIdOrderByDateAsc(strategyId);
//
//        Double highProfitLossRate = 0.0;
//        Double minDrawDown = 0.0;
//        Double sumDrawDown = 0.0;
//        Long sumDrawDownPeriod = 0L;
//
//        for(int i=0; i<dailyList.size(); i++) {
//            Double currentProfitLossRate = dailyList.get(i).getAccumulatedProfitLossRate();
//
//            if(highProfitLossRate > currentProfitLossRate) {
//                // 손익률 인하되는 시점
//                sumDrawDownPeriod++;
//                if(currentProfitLossRate - highProfitLossRate < minDrawDown) {
//                    // DD 갱신
//                    minDrawDown = currentProfitLossRate - highProfitLossRate;
//                }
//            } else {
//                highProfitLossRate = currentProfitLossRate;
//                sumDrawDown += minDrawDown;
//                minDrawDown = 0.0;
//            }
//        }
//
//        return strategyCalculator.getKpRatio(dailyList.stream().findFirst().get().getAccumulatedProfitLossRate(), sumDrawDown, sumDrawDownPeriod, Long.valueOf(dailyList.stream().toList().size()));
//    }
//
//    private Double getSmScore(Long strategyId, Double kpRatio) {
//        List<Strategy> strategyList = strategyRepository.findAllUsingState();
//
//        List<Double> kpRatioList = strategyList.stream()
//                .map(Strategy::getKpRatio)
//                .collect(Collectors.toList());
//
//        // kp ratio 평균
//        Double averageKpRatio = strategyCalculator.calculateAverage(kpRatioList);
//        // kp ratio 표준편차
//        Double standardDeviationKpRatio = strategyCalculator.calculateStandardDeviation(kpRatioList, averageKpRatio);
//
//        return StrategyCalculator.getSmScore(kpRatio, averageKpRatio, standardDeviationKpRatio);
//    }
}
