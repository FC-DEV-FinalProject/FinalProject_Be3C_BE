package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.DailyResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.SaveDailyRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.SaveDailyResponseDto;
import com.be3c.sysmetic.domain.strategy.entity.Daily;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.exception.StrategyBadRequestException;
import com.be3c.sysmetic.domain.strategy.exception.StrategyExceptionMessage;
import com.be3c.sysmetic.domain.strategy.repository.DailyRepository;
import com.be3c.sysmetic.domain.strategy.repository.StrategyRepository;
import com.be3c.sysmetic.domain.strategy.util.StrategyCalculator;
import com.be3c.sysmetic.global.common.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@Service
public class DailyServiceImpl implements DailyService {
    /*
    일간분석 데이터 등록

    1. 날짜, 입출금, 일손익을 리스트 형태로 받는다.
    2. 입력한 날짜 중 이미 존재하는 날짜가 있을 경우 해당 데이터는 추가 또는 수정하지 않고 응답 데이터에 중복임을 보낸다.
    3. 미래의 날짜인지 검증 필요
    4. 나머지 데이터는 등록한다.
    5. 모든 데이터가 중복일 경우 예외 처리

    일간분석 데이터 수정

    1. 날짜, 입출금, 일손익과 일간분석 식별번호를 받는다.
    2. 일간분석 식별번호를 검증한다.
    3. 미래의 날짜인지 검증 필요
    4. 날짜 변경시 변경한 날짜 데이터가 기존에 존재하는지 검증
        존재할 경우 기존에 존재하던 데이터 삭제 및 변경한 데이터 업데이트 처리
        미존재할 경우 DB 업데이트
    5. 수정 이후의 날짜부터 누적손익 다시 계산

    일간분석 데이터 삭제

    1. 일간분석 식별번호를 받는다.
    2. 일간분석 식별번호를 검증한다.
    3. DB 삭제
    4. 삭제 이후의 날짜부터 누적손익 다시 계산

    일간분석 데이터 조회

    1. 전략 식별번호, 기간, 페이지를 받는다.
    2. 전략 식별번호를 검증한다.
    3. 해당 기간의 일간분석 데이터를 한 페이지에 10개 노출한다.
    */

    private final DailyRepository dailyRepository;
    private final StrategyRepository strategyRepository;
    private final StrategyCalculator strategyCalculator;

    @Override
    public void saveDaily(Long strategyId, List<SaveDailyRequestDto> requestDtoList) {
        List<Daily> dailyList = processingDaily(strategyId, null, requestDtoList);
        dailyRepository.saveAll(dailyList);
    }

    // 일간분석 등록
    @Override
    public SaveDailyResponseDto getIsDuplicate(Long strategyId, List<SaveDailyRequestDto> requestDtoList) {
        for (SaveDailyRequestDto requestDto : requestDtoList) {
            // 이미 등록한 날짜가 하나라도 존재할 경우 true 반환
            if(findDuplicateDate(strategyId, requestDto.getDate()) != null) {
                return SaveDailyResponseDto.builder()
                        .isDuplicate(true)
                        .build();
            }
        }
        return SaveDailyResponseDto.builder()
                .isDuplicate(false)
                .build();
    }

    // 일간분석 수정
    @Transactional
    @Override
    public void updateDaily(Long strategyId, Long dailyId, SaveDailyRequestDto requestDto) {
        // 등록시 사용하는 메서드 사용하기 위해 List로 변환
        List<SaveDailyRequestDto> requestDtoList = new ArrayList<>();
        requestDtoList.add(requestDto);
        Daily daily = processingDaily(strategyId, dailyId, requestDtoList).stream().findFirst().orElseThrow(() -> new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage()));

        // DB 저장
        dailyRepository.save(daily);

        // 누적금액 다시 계산
        recalculateAccumulatedData(strategyId, daily.getDate());
    }

    // 일간분석 삭제
    @Transactional
    @Override
    public void deleteDaily(Long strategyId, Long dailyId) {
        Daily daily = dailyRepository.findById(dailyId).orElseThrow(() -> new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage()));

        // DB 삭제
        dailyRepository.deleteById(dailyId);

        // 누적금액 다시 계산
        recalculateAccumulatedData(strategyId, daily.getDate());
    }

    // 일간분석 조회
    public PageResponse<DailyResponseDto> findDaily(Long strategyId, int page, LocalDateTime startDate, LocalDateTime endDate) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<DailyResponseDto> dailyResponseDtoPage = dailyRepository.findAllByDateBetween(startDate, endDate, pageable).map(this::entityToDto);

        PageResponse<DailyResponseDto> responseDto = PageResponse.<DailyResponseDto>builder()
                .currentPage(dailyResponseDtoPage.getPageable().getPageNumber())
                .pageSize(dailyResponseDtoPage.getPageable().getPageSize())
                .totalElement(dailyResponseDtoPage.getTotalElements())
                .totalPages(dailyResponseDtoPage.getTotalPages())
                .content(dailyResponseDtoPage.getContent())
                .build();

        return responseDto;
    }

    // TODO 시큐리티 완료 후 수정 필요
    private List<Daily> processingDaily(Long strategyId, Long dailyId, List<SaveDailyRequestDto> requestDtoList) {
        final Daily beforeDaily = getBeforeDaily(strategyId);
        final boolean isFirst = beforeDaily == null;
        final Double beforePrincipal = beforeDaily != null ? beforeDaily.getPrincipal() : null;
        final Double beforeBalance = beforeDaily != null ? beforeDaily.getCurrentBalance() : null;
        final Double beforeStandardAmount =  beforeDaily != null ?  beforeDaily.getStandardAmount() : null;
        final Long createdBy = 1L;

        List<Daily> dailyList = new ArrayList<>();

        for(SaveDailyRequestDto requestDto : requestDtoList) {
            Daily duplicatedDaily = findDuplicateDate(strategyId, requestDto.getDate());

            if(dailyId == null && duplicatedDaily != null) {
                // 일간분석 데이터 등록이면서 중복일 경우
                continue;
            }

            // 중복이 아닌 등록 또는 수정일 경우
            dailyList.add(dtoToEntity(dailyId, strategyId, createdBy, isFirst, requestDto, beforePrincipal, beforeBalance, beforeStandardAmount));
        }

        return dailyList;
    }

    private Daily dtoToEntity(Long dailyId, Long strategyId, Long createdBy, boolean isFirst, SaveDailyRequestDto requestDto, Double beforePrincipal, Double beforeBalance, Double beforeStandardAmount) {
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
                .strategy(getStrategy(strategyId))
                .date(requestDto.getDate())
                .principal(strategyCalculator.getPrincipal(isFirst, requestDto.getDepositWithdrawalAmount(), beforePrincipal, beforeBalance)) // 원금
                .depositWithdrawalAmount(requestDto.getDepositWithdrawalAmount()) // 입출금
                .profitLossAmount(requestDto.getDailyProfitLossAmount()) // 일손익금
                .profitLossRate(dailyProfitLossRate) // 일손익률
                .currentBalance(strategyCalculator.getCurrentBalance(isFirst, beforeBalance, requestDto.getDepositWithdrawalAmount(), requestDto.getDailyProfitLossAmount())) // 잔고
                .standardAmount(strategyCalculator.getStandardAmount(isFirst, requestDto.getDepositWithdrawalAmount(), requestDto.getDailyProfitLossAmount(), beforeBalance, beforePrincipal)) // 기준가
                .accumulatedProfitLossAmount(getAccumulatedProfitLossAmount(strategyId, requestDto.getDailyProfitLossAmount())) // 누적손익금액
                .accumulatedProfitLossRate(getAccumulatedProfitLossRate(strategyId, dailyProfitLossRate)) // 누적손익률
                .createdBy(createdBy)
                .modifiedBy(createdBy)
                .build();
    }

    private DailyResponseDto entityToDto(Daily daily) {
        return DailyResponseDto.builder()
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

    private Strategy getStrategy(Long strategyId) {
        return strategyRepository.findById(strategyId).orElseThrow(() -> new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage()));
    }

    // 이전 일자 데이터 조회
    private Daily getBeforeDaily(Long strategyId) {
        return dailyRepository.findTopByStrategyIdOrderByDateDesc(strategyId);
    }

    // 날짜 중복 데이터 조회
    private Daily findDuplicateDate(Long strategyId, LocalDateTime date) {
        return dailyRepository.findByStrategyIdAndDate(strategyId, date.toLocalDate());
    }

    /*
    TODO :
    2차 개발 때 누적손익금액, 누적손익률 계산 성능 개선 필요
    현재 모든 일간분석 데이터 조회하여 계산
    중간 데이터 삭제시 어떻게 처리할지 고민
    추후 등록일시로부터 7일이 지난 데이터의 가장 마지막 누적손익금액과 나머지 데이터의 손익금액을 합산하여 계산하는 방식으로 진행 생각중
     */
    private Double getAccumulatedProfitLossAmount(Long strategyId, Double profitLossAmount) {
        List<Daily> dailyList = dailyRepository.findAllByStrategyIdOrderByDateDesc(strategyId);
        Double cumulativeProfitLossAmount = dailyList.stream().mapToDouble(Daily::getProfitLossAmount).sum();
        cumulativeProfitLossAmount += profitLossAmount;
        return cumulativeProfitLossAmount;
    }

    private Double getAccumulatedProfitLossRate(Long strategyId, Double profitLossRate) {
        List<Daily> dailyList = dailyRepository.findAllByStrategyIdOrderByDateDesc(strategyId);
        Double cumulativeProfitLossRate = dailyList.stream().mapToDouble(Daily::getProfitLossRate).sum();
        cumulativeProfitLossRate += profitLossRate;
        return cumulativeProfitLossRate;
    }

    // 수정 또는 삭제시 누적 데이터 다시 계산
    public void recalculateAccumulatedData(Long strategyId, LocalDateTime startDate) {
        Double cumulativeProfitLossAmount = 0.0;
        Double cumulativeProfitLossRate = 0.0;

        // 수정 또는 삭제 이후의 데이터 조회
        List<Daily> dailyList = dailyRepository.findAllByStrategyIdAndDateAfterOrderByDateAsc(strategyId, startDate.toLocalDate());

        // 수정 또는 삭제 이전 데이터의 누적손익금액, 누적손익률 조회
        Daily beforeDaily = dailyRepository.findFirstByStrategyIdAndDateBeforeOrderByDateDesc(strategyId, startDate.toLocalDate());
        if (beforeDaily != null) {
            cumulativeProfitLossAmount = beforeDaily.getAccumulatedProfitLossAmount();
            cumulativeProfitLossRate = beforeDaily.getAccumulatedProfitLossRate();
        }

        // 누적손익금액, 누적손익률 다시 계산
        for (Daily daily : dailyList) {
            cumulativeProfitLossAmount += daily.getProfitLossAmount();
            cumulativeProfitLossRate += daily.getProfitLossRate();

            // 누적손익금액 및 누적손익률 업데이트
            daily.setAccumulatedProfitLossAmount(cumulativeProfitLossAmount);
            daily.setAccumulatedProfitLossRate(cumulativeProfitLossRate);
        }

        // DB 업데이트
        dailyRepository.saveAll(dailyList);
    }

}
