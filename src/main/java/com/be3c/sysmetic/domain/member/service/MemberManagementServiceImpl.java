package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.dto.MemberGetResponseDto;
import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.global.common.response.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MemberManagementServiceImpl implements MemberManagementService {

    private final MemberRepository memberRepository;

    @Override
    public PageResponse<MemberGetResponseDto> findMemberPage(String role, Integer page, String searchType, String searchKeyword) {
        Pageable pageable = PageRequest.of(page-1, 10);
        Page<MemberGetResponseDto> members = memberRepository.findMembers(role, searchType, searchKeyword, pageable);

        if(members.hasContent()) {
            return PageResponse.<MemberGetResponseDto>builder()
                    .totalPages(members.getTotalPages())
                    .totalElement(members.getTotalElements())
                    .pageSize(members.getNumberOfElements())
                    .currentPage(page)
                    .content(members.getContent())
                    .build();
        }
        throw new NoSuchElementException();
    }

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
        Member member = memberRepository.findById(memberId).orElseThrow(EntityNotFoundException::new);
        String roleCode = member.getRoleCode();

        final String ROLE_USER = "RC001";
        final String ROLE_TRADER = "RC002";
        final String ROLE_MANAGER_USER = "RC003";
        final String ROLE_MANAGER_TRADER = "RC004";

        if (hasManagerRights) {
            // 관리자 지정
            if (ROLE_USER.equals(roleCode)) {
                memberRepository.updateRoleCode(memberId, ROLE_MANAGER_USER);
            } else if (ROLE_TRADER.equals(roleCode)) {
                memberRepository.updateRoleCode(memberId, ROLE_MANAGER_TRADER);
            }
        } else {
            // 관리자 해지
            if (ROLE_MANAGER_USER.equals(roleCode)) {
                memberRepository.updateRoleCode(memberId, ROLE_USER);
            } else if (ROLE_MANAGER_TRADER.equals(roleCode)) {
                memberRepository.updateRoleCode(memberId, ROLE_TRADER);
            }
        }
    }

}
