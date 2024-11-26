package com.be3c.sysmetic.domain.strategy.service;

public interface StrategyAllowApprovalService {
    boolean approveOpen(Long id);
    boolean approveCancel(Long id);
}
