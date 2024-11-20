package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.AdminStrategyGetResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.AdminStrategySearchGetDto;
import com.be3c.sysmetic.domain.strategy.dto.AllowApprovalRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.RejectStrategyApprovalDto;
import com.be3c.sysmetic.domain.strategy.entity.StrategyApprovalHistory;
import com.be3c.sysmetic.domain.strategy.repository.StrategyApprovalRepository;
import com.be3c.sysmetic.domain.strategy.repository.StrategyRepository;
import com.be3c.sysmetic.global.common.Code;
import com.be3c.sysmetic.global.common.response.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AdminStrategyServiceImpl implements AdminStrategyService {

    private final StrategyApprovalRepository strategyApprovalRepository;

    /*
        관리자 전략 관리 페이지
        1. page + pageSize를 사용해서 Pageable 객체를 만든다.
        2. 검색 조건 3개 + pageable 객체를 통해 페이지를 찾는다.
           동적 쿼리를 사용하여 필요한 검색 조건만 사용한다.
        3. 만약 아무 전략도 검색 조건에 맞지 않는다면, NoSuchElementException을 발생시킨다.
        4. PageResponse로 변경해 반환한다.
     */
    @Override
    public PageResponse<AdminStrategyGetResponseDto> findStrategyPage(
            AdminStrategySearchGetDto adminStrategySearchGetDto
    ) {
        Pageable pageable = PageRequest.of(adminStrategySearchGetDto.getPage(), 10);

        Page<AdminStrategyGetResponseDto> find_page = strategyApprovalRepository.findStrategiesAdminPage(
                adminStrategySearchGetDto.getOpenStatus(),
                adminStrategySearchGetDto.getApprovalStatus(),
                adminStrategySearchGetDto.getKeyword(),
                pageable);

        if(!find_page.hasContent()) {
            throw new NoSuchElementException();
        }

        return PageResponse.<AdminStrategyGetResponseDto>builder()
                .totalPages(find_page.getTotalPages())
                .currentPage(find_page.getNumber())
                .pageSize(find_page.getNumberOfElements())
                .totalElement(find_page.getTotalElements())
                .build();
    }

    /*
        선택 전략 승인 요청 처리
        1. 요청한 requestDto의 Id 목록을 받아온다.
        2. 해당 전략의 아이디와 승인 단계를 allowApproval 메서드의 매개변수로 사용하여 실행한다.
        2-1. 만약 해당 전략의 공개 요청이 존재하지 않는다면, resultMap에 id와 실패 사유를 저장한다.
        3. 결과를 반환한다.
     */
    @Override
    public Map<Long, String> StrategyApproveApplyAllow(AllowApprovalRequestDto requestDtoList) {
        HashMap<Long, String> resultMap = new HashMap<>();

        for(Long id : requestDtoList.getApprovalId()) {
            try {
                allowApproval(id, requestDtoList.getApprovalCode());
            } catch (EntityNotFoundException e) {
                resultMap.put(id, e.getMessage());
            }
        }

        return resultMap;
    }

    /*
        해당 승인 요청 거절
        1. strategyId를 통해 해당 승인 요청을 찾는다.
        2. 해당 승인 요청의 상태를 거절 상태로 변경한다.
        3. 해당 승인 요청의 거절 사유를 저장한다.
        4. 승인 요청 거절 메일을 발송한다.
     */
    @Override
    public boolean rejectStrategyApproval(RejectStrategyApprovalDto rejectStrategyApprovalDto) {
        StrategyApprovalHistory strategyApproval = strategyApprovalRepository
                .findByStrategyIdAndStatusCodeNotApproval(
                        rejectStrategyApprovalDto.getApprovalId())
                .orElseThrow(EntityNotFoundException::new);

        strategyApproval.setStatusCode(Code.APPROVE_REJECT.getCode());
        strategyApproval.setRejectReason(rejectStrategyApprovalDto.getRejectReason());

        strategyApprovalRepository.save(strategyApproval);

        // TODO - 메일 발송 시스템 작성 필요
        return true;
    }

    private void allowApproval(Long id, String statusCode) {
        StrategyApprovalHistory approvalRequest =
                strategyApprovalRepository.
                        findByStrategyIdNotApproval(
                                id
                        ).orElseThrow(EntityNotFoundException::new);

        approvalRequest.setStatusCode(statusCode);
    }
}