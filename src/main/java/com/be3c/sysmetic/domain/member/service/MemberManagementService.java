package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.dto.MemberGetResponseDto;
import com.be3c.sysmetic.global.common.response.PageResponse;

public interface MemberManagementService {
    PageResponse<MemberGetResponseDto> findMemberPage(String role, Integer page, String searchType, String searchKeyword);

    void changeRoleCode(Long memberId, boolean changeRoleCode);
}
