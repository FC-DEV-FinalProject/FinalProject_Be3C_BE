package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.AdminStrategyGetResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.AdminStrategySearchGetDto;
import com.be3c.sysmetic.domain.strategy.dto.AllowApprovalRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.RejectStrategyApprovalDto;
import com.be3c.sysmetic.global.common.response.PageResponse;

import java.util.Map;

public interface AdminStrategyService {
    PageResponse<AdminStrategyGetResponseDto> findStrategyPage(AdminStrategySearchGetDto adminStrategySearchGetDto);
    Map<Long, String> StrategyApproveApplyAllow(AllowApprovalRequestDto allowApprovalRequestDto);
    boolean rejectStrategyApproval(RejectStrategyApprovalDto rejectStrategyApprovalDto);
}
