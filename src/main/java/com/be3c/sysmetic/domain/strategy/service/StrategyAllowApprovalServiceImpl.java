package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.domain.strategy.dto.StrategyStatusCode;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.entity.StrategyApprovalHistory;
import com.be3c.sysmetic.domain.strategy.repository.DailyRepository;
import com.be3c.sysmetic.domain.strategy.repository.StrategyApprovalRepository;
import com.be3c.sysmetic.domain.strategy.repository.StrategyRepository;
import com.be3c.sysmetic.global.common.Code;
import com.be3c.sysmetic.global.exception.ConflictException;
import com.be3c.sysmetic.global.util.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.be3c.sysmetic.global.common.Code.*;

@Service
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StrategyAllowApprovalServiceImpl implements StrategyAllowApprovalService {

    private final StrategyRepository strategyRepository;

    private final DailyRepository dailyRepository;

    private final StrategyApprovalRepository strategyApprovalRepository;

    private final SecurityUtils securityUtils;

    private final MemberRepository memberRepository;

    @Override
    public boolean approveOpen(Long id) {
        Long userId = securityUtils.getUserIdInSecurityContext();

        Strategy strategy = strategyRepository.findByIdAndTraderIdAndStatusCode(
                id,
                userId,
                StrategyStatusCode.PRIVATE.getCode()
        ).orElseThrow(EntityNotFoundException::new);

        if(dailyRepository.countByStrategyId(id) <= 2) {
            throw new IllegalStateException();
        }

        if(strategyApprovalRepository.findByStrategyIdAndStatusCodeNotApproval(id).isPresent()) {
            throw new ConflictException();
        }

        StrategyApprovalHistory strategyApprovalHistory = StrategyApprovalHistory.builder()
                .strategy(strategy)
                .manager(memberRepository.findByIdAndUsingStatusCode(userId, USING_STATE.getCode()).orElseThrow(EntityNotFoundException::new))
                .statusCode(APPROVE_WAIT.getCode())
                .build();

        strategyApprovalRepository.save(strategyApprovalHistory);

        return true;
    }

    @Override
    public boolean approveCancel(Long id) {
        StrategyApprovalHistory strategyApprovalHistory = strategyApprovalRepository
                .findByStrategyIdNotApproval(
                        id
                ).orElseThrow(EntityNotFoundException::new);

        strategyApprovalHistory.setStatusCode(APPROVE_CANCEL.getCode());

        strategyApprovalRepository.save(strategyApprovalHistory);

        return true;
    }
}
