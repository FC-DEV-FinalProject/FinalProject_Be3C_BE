package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.repository.StrategyApprovalRepository;
import com.be3c.sysmetic.domain.strategy.repository.StrategyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StrategyApprovalServiceImpl implements StrategyApprovalService {

    private final StrategyRepository strategyRepository;

    private final StrategyApprovalRepository strategyApprovalRepository;
}
