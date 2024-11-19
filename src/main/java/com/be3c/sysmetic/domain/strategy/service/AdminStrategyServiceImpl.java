package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.AdminStrategyApprovalGetResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.AdminStrategyGetResponseDto;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AdminStrategyServiceImpl implements AdminStrategyService {

    private final StrategyRepository strategyRepository;

    private final StrategyApprovalRepository strategyApprovalRepository;

    @Override
    public PageResponse<AdminStrategyGetResponseDto> findStrategyPage(Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());

        Page<AdminStrategyGetResponseDto> find_page = strategyRepository.findStrategiesPage(pageable);

        if(!find_page.hasContent()) {
            throw new NoSuchElementException("잘못된 페이지 요청입니다.");
        }

        return PageResponse.<AdminStrategyGetResponseDto>builder()
                .totalPages(find_page.getTotalPages())
                .currentPage(page)
                .pageSize(find_page.getNumberOfElements())
                .totalElement(find_page.getTotalElements())
                .build();
    }

    @Override
    public PageResponse<AdminStrategyApprovalGetResponseDto> findApproveStrategyPage(Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());

        Page<AdminStrategyGetResponseDto> find_page
                = strategyApprovalRepository
                .findApprovalStrategy(
                        pageable,
                        Code.APPROVE_WAIT.getCode()
                );

        if(!find_page.hasContent()) {
            throw new NoSuchElementException("잘못된 페이지 요청입니다.");
        }

        return PageResponse.<AdminStrategyApprovalGetResponseDto>builder()
                .totalPages(find_page.getTotalPages())
                .currentPage(page)
                .pageSize(find_page.getNumberOfElements())
                .totalElement(find_page.getTotalElements())
                .build();
    }

    @Override
    public boolean StrategyApproveApplyAllow(Long id) {
        StrategyApprovalHistory find_approval = strategyApprovalRepository
                    .findByIdAndStatusCode(
                            id,
                            Code.APPROVE_WAIT.getCode()
                    ).orElseThrow(
                            () -> new EntityNotFoundException("해당 전략 승인 신청을 찾을 수 없습니다.")
                );

        find_approval.setStatusCode(Code.APPROVE_SUCCESS.getCode());

        find_approval.getStrategy().setStatusCode(Code.OPEN_STRATEGY.getCode());

        strategyApprovalRepository.save(find_approval);

        return true;
    }

    @Override
    public boolean rejectStrategyApproval(RejectStrategyApprovalDto rejectStrategyApprovalDto) {
        StrategyApprovalHistory strategyApproval = strategyApprovalRepository
                .findByIdAndStatusCode(
                        rejectStrategyApprovalDto.getApprovalId(),
                        Code.WAIT_STRATEGY.getCode())
                .orElseThrow(EntityNotFoundException::new);

        strategyApproval.setStatusCode(Code.APPROVE_REJECT.getCode());
        strategyApproval.setRejectReason(rejectStrategyApprovalDto.getRejectReason());

        strategyApprovalRepository.save(strategyApproval);

        return false;
    }
}