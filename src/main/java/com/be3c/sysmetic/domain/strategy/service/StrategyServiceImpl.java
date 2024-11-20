package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.repository.StrategyRepository;
import com.be3c.sysmetic.global.common.Code;
import com.be3c.sysmetic.global.util.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.be3c.sysmetic.global.common.Code.*;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StrategyServiceImpl implements StrategyService {

    private final StrategyRepository strategyRepository;

    private final SecurityUtils securityUtils;

    @Override
    public boolean privateStrategy(Long id) {
        Long userId = securityUtils.getUserIdInSecurityContext();
        Strategy strategy = strategyRepository.findByIdAndTraderIdAndStatusCode(id, userId, OPEN_STRATEGY.getCode()).orElseThrow(EntityNotFoundException::new);

        strategy.setStatusCode(Code.CLOSE_STRATEGY.getCode());
        strategyRepository.save(strategy);
        return true;
    }
}
