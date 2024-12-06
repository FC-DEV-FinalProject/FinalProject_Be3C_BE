package com.be3c.sysmetic.domain.strategy.util;

import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.exception.StrategyBadRequestException;
import com.be3c.sysmetic.domain.strategy.exception.StrategyExceptionMessage;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import com.be3c.sysmetic.global.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StrategyViewAuthorize {

    private final SecurityUtils securityUtils;

    public boolean Authorize(Strategy strategy) {
        if (strategy.getStatusCode().equals("NOT_USING_STATE")) {
            // 1. 상태 코드가 NOT_USING_STATE인 경우 - 실패 (삭제된 전략)
            throw new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage(), ErrorCode.NOT_FOUND);
        } else if (strategy.getStatusCode().equals("PUBLIC")) {
            // 2. 상태 코드가 PUBLIC이면 모든 요청에 대해 성공
            // getUserIdInSecurityContext()에서 로그인 상태인지 검증하기 때문에 PUBLIC 상태 먼저 검증 필요
            return true;
        } else if (strategy.getTrader().getId().equals(securityUtils.getUserIdInSecurityContext()) ||
                "USER_MANAGER".equals(securityUtils.getUserRoleInSecurityContext()) ||
                "TRADER_MANAGER".equals(securityUtils.getUserRoleInSecurityContext()) ||
                "ADMIN".equals(securityUtils.getUserRoleInSecurityContext())
        ) {
            // 3. 상태 코드가 NOT_USING_STATE, PUBLIC이 아니면서 트레이더 ID가 일치하거나 사용자 역할이 MANAGER인 경우 - 성공
            return true;
        } else {
            // 4. 나머지 경우 - 실패
            throw new StrategyBadRequestException(StrategyExceptionMessage.INVALID_MEMBER.getMessage(), ErrorCode.NOT_FOUND);
        }
    }
}
