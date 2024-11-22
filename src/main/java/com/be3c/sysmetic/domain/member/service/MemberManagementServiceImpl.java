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
    public void changeRoleCode(Long memberId, boolean changeRoleCode) {
        // (changeRoleCode) true = 매니저로 변경, false = 일반회원/트레이더로 변경
        Member member = memberRepository.findById(memberId).orElseThrow(EntityNotFoundException::new);
        String roleCode = member.getRoleCode();

        // RC001(일반) <-> RC003
        // RC002(트레이더) <-> RC004
        if(roleCode.equals("RC001") && changeRoleCode) {
            memberRepository.updateRoleCode(memberId, "RC003");
        }
        if(roleCode.equals("RC002") && changeRoleCode) {
            memberRepository.updateRoleCode(memberId, "RC004");
        }
        if(roleCode.equals("RC003") && !changeRoleCode) {
            memberRepository.updateRoleCode(memberId, "RC001");
        }
        if(roleCode.equals("RC004") && !changeRoleCode) {
            memberRepository.updateRoleCode(memberId, "RC002");
        }
    }

}
