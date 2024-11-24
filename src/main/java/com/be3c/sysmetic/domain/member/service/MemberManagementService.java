package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.dto.MemberGetResponseDto;
import com.be3c.sysmetic.domain.member.entity.MemberSearchRole;
import com.be3c.sysmetic.domain.member.entity.MemberSearchType;
import com.be3c.sysmetic.global.common.response.PageResponse;

public interface MemberManagementService {
    PageResponse<MemberGetResponseDto> findMemberPage(MemberSearchRole role, Integer page, MemberSearchType searchType, String searchKeyword);

    void changeRoleCode(Long memberId, boolean hasManagerRights);
}
