package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.dto.MemberGetResponseDto;
import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.entity.MemberRole;
import com.be3c.sysmetic.domain.member.entity.MemberSearchRole;
import com.be3c.sysmetic.domain.member.entity.MemberSearchType;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.global.common.response.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MemberManagementServiceImpl implements MemberManagementService {

    private final MemberRepository memberRepository;

    // 1. 회원 목록 조회
    @Override
    public PageResponse<MemberGetResponseDto> findMemberPage(MemberSearchRole role, Integer page, MemberSearchType searchType, String searchKeyword) {
        Pageable pageable = PageRequest.of(page-1, 10);
        Page<MemberGetResponseDto> members = memberRepository.findMembers(role.getCode(), searchType.getCode(), searchKeyword, pageable);

        return PageResponse.<MemberGetResponseDto>builder()
                .totalPages(members.getTotalPages())
                .totalElement(members.getTotalElements())
                .pageSize(members.getNumberOfElements())
                .currentPage(page)
                .content(members.getContent())
                .build();
    }

    // 2. 회원 등급 변경
    @Override
    public void changeRoleCode(Long memberId, boolean hasManagerRights) {
        /*
            관리자 지정하는 경우 (hasManagerRights = true)
                RC001 이나 RC002 인 경우, 변경O - RC001 > RC003, RC002 > RC004
                RC003 이나 RC004 인 경우, 변경X
            관리자 해지하는 경우 (hasManagerRights = false)
                RC001 이나 RC002 인 경우, 변경X
                RC003 이나 RC004 인 경우, 변경O - RC003 > RC001, RC004 > RC002
         */
        // 회원 조회
        Member member = memberRepository.findById(memberId).orElseThrow(() ->
                new EntityNotFoundException("회원 정보를 찾을 수 없습니다. 회원 ID: " + memberId)
        );

        // 현재 회원등급
        MemberRole currentRoleCode = MemberRole.getEnumRole(member.getRoleCode());

        // 변경될 회원등급  (관리자 지정 / 해지)
        MemberRole updateRole = hasManagerRights ? currentRoleCode.promoteToManager() : currentRoleCode.demoteFromManager();

        // 역할이 달라진 경우 업데이트
        if(!currentRoleCode.equals(updateRole)) {
            int updated = memberRepository.updateRoleCode(memberId, updateRole.getCode());
            if(updated == 0) {
                throw new IllegalArgumentException("회원의 등급변경에 실패했습니다. 회원 ID: " + memberId);
            }
        }
    }

}
