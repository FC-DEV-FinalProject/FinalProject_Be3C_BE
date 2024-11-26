package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.DailyGetResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.DailyRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.DailyPostResponseDto;
import com.be3c.sysmetic.global.common.response.PageResponse;

import java.time.LocalDate;
import java.util.List;

public interface DailyService {
    void saveDaily(Long strategyId, List<DailyRequestDto> requestDtoList);
    void updateDaily(Long strategyId, Long dailyId, DailyRequestDto requestDto);
    void deleteDaily(Long strategyId, Long dailyId);
    DailyPostResponseDto getIsDuplicate(Long strategyId, List<DailyRequestDto> requestDtoList);
    PageResponse<DailyGetResponseDto> findDaily(Long strategyId, Integer page, LocalDate startDate, LocalDate endDate);
    PageResponse<DailyGetResponseDto> findTraderDaily(Long strategyId, Integer page, LocalDate startDate, LocalDate endDate);
}

/*
일간분석 데이터

# 등록
트레이더는 자신의 전략에 일간분석 데이터를 등록할 수 있다.
한 개 또는 여러개의 일간분석 데이터를 한 번에 등록할 수 있다.
등록하고자 하는 일간분석 데이터 중 이미 등록한 일자가 있을 경우 해당 데이터는 반영되지 않으며, 응답시 중복인 데이터가 있음을 알린다.
입력받는 데이터는 일자, 입출금, 일손익(금액)이다.

1) security context의 member id로 권한을 검증한다.
2) 검증 통과한 member id가 현재 전략을 업로드한 트레이더 id와 일치하는지 검증한다.
3) 일간분석 등록 요청 데이터를 validator를 사용하여 검증한다.
4) 일간분석 등록 요청 데이터의 일자 중 이미 등록한 일자가 있을 경우 등록하지 않고, 중복인 데이터가 있음을 응답 객체에 포함한다.
5) 그 외 데이터를 DB에 저장한다.
6) 반영된 데이터가 존재할 경우 월간분석 데이터를 갱신한다.
7) 응답한다.

#수정
트레이더는 자신의 전략의 일간분석 데이터를 수정할 수 있다.
한 개씩만 수정 가능하다.
입력받는 데이터는 일자, 입출금, 일손익(금액), 전략id 이다.

1) security context의 member id로 권한을 검증한다.
2) 검증 통과한 member id가 현재 전략을 업로드한 트레이더 id와 일치하는지 검증한다.
3) strategy id가 유효한지 검증한다.
4) 일간분석 수정 요청 데이터를 validator를 사용하여 검증한다.
5) 데이터를 DB에 수정 반영한다.
6) 반영된 데이터가 존재할 경우 월간분석 데이터를 갱신한다.
7) 응답한다.

#삭제
트레이더는 자신의 전략의 일간분석 데이터를 삭제할 수 있다.
한 개씩만 삭제 가능하다.
입력받는 데이터는 전략id 이다.

1) security context의 member id로 권한을 검증한다.
2) 검증 통과한 member id가 현재 전략을 업로드한 트레이더 id와 일치하는지 검증한다.
3) strategy id가 유효한지 검증한다.
4) DB에서 삭제한다.
5) 반영된 데이터가 존재할 경우 월간분석 데이터를 갱신한다.
6) 응답한다.

#조회
모든 사용자는 PUBLIC 상태인 전략의 일간분석 데이터를 조회할 수 있다.
관리자는 PUBLIC, PRIVATE 상태인 전략의 일간분석 데이터를 조회할 수 있다.
트레이더는 자신의 전략일 경우 PRIVATE 상태인 전략의 일간분석 데이터를 조회할 수 있다.
페이징 처리가 필요하다.
한 페이지당 10개의 데이터를 조회할 수 있다.
특정 기간의 일간분석 데이터를 조회할 수 있다.
입력받는 데이터는 전략id, 조회 시작 년월일, 조회 종료 년월일이다.

1) security context의 member id로 권한을 검증한다.
2) strategy id가 유효한지 검증한다.
3) 관리자일 경우 전략의 상태가 PUBLIC 또는 PRIVATE 상태인지 검증한다.
4) 트레이더일 경우 전략의 상태가 PUBLIC 또는 자신의 전략이면서 PRIVATE 상태인지 검증한다.
5) 투자자 또는 비회원일 경우 전략의 상태가 PUBLIC 상태인지 검증한다.
6) 한 페이지당 10개의 입력받은 기간에 해당하는 일간분석 데이터를 조회한다.
7) 응답한다.
 */
