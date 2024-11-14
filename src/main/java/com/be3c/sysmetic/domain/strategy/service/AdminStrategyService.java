package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.AdminStrategyApprovalGetResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.AdminStrategyGetResponseDto;
import com.be3c.sysmetic.global.common.response.PageResponse;

public interface AdminStrategyService {
    PageResponse<AdminStrategyGetResponseDto> findStrategyPage(Integer page);
    PageResponse<AdminStrategyApprovalGetResponseDto> findApproveStrategyPage(Integer page);
    boolean StrategyApproveApplyAllow(Long id);
}
