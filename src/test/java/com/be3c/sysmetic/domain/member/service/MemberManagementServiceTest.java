package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.dto.MemberGetResponseDto;
import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.entity.MemberSearchRole;
import com.be3c.sysmetic.domain.member.entity.MemberSearchType;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.global.common.response.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
class MemberManagementServiceTest {
    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberManagementServiceImpl memberManagementService;

    // 1. 회원 목록 조회 테스트
    @Test
    void findMemberPage_shouldReturnPageResponse() {
        // Given
        MemberSearchRole role = MemberSearchRole.USER; // 가정된 역할 Enum
        Integer page = 1;
        MemberSearchType searchType = MemberSearchType.NAME; // 가정된 검색 Enum
        String searchKeyword = "test";
        Pageable pageable = PageRequest.of(page - 1, 10);

        MemberGetResponseDto memberDto1 = MemberGetResponseDto.builder()
                .id(1L)
                .roleCode("RC001")
                .email("test1e@example.com")
                .name("test1")
                .nickname("test1")
                .build();
        MemberGetResponseDto memberDto2 = MemberGetResponseDto.builder()
                .id(2L)
                .roleCode("RC001")
                .email("john.doe@example.com")
                .name("test2")
                .nickname("test2")
                .build();

        List<MemberGetResponseDto> mockContent = List.of(memberDto1, memberDto2);

        Page<MemberGetResponseDto> mockPage = new PageImpl<>(mockContent, pageable, mockContent.size());

        Mockito.when(memberRepository.findMembers(
                role.getCode(), searchType.getCode(), searchKeyword, pageable)
        ).thenReturn(mockPage);

        // When
        PageResponse<MemberGetResponseDto> response = memberManagementService.findMemberPage(
                role, page, searchType, searchKeyword
        );

        // Then
        assertNotNull(response);
        assertEquals(2, response.getContent().size());
        assertEquals(1, response.getCurrentPage());
        assertEquals(2, response.getTotalElement());
        Mockito.verify(memberRepository, times(1))
                .findMembers(role.getCode(), searchType.getCode(), searchKeyword, pageable);
    }

    // 2. 회원 등급 변경 테스트 - 관리자 지정
    @Test
    void changeRoleCode_shouldUpdateRoleToManager() {
        // Given
        Long memberId = 1L;
        boolean hasManagerRights = true; // 관리자 지정
        Member mockMember = Member.builder()
                .id(1L)
                .name("test1")
                .roleCode("RC001")
                .email("test1@test.com")
                .build();

        Mockito.when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        Mockito.when(memberRepository.updateRoleCode(memberId, "RC003")).thenReturn(1);

        // When
        memberManagementService.changeRoleCode(memberId, hasManagerRights);

        // Then
        Mockito.verify(memberRepository, times(1)).findById(memberId);
        Mockito.verify(memberRepository, times(1)).updateRoleCode(memberId, "RC003");
    }

    // 3. 회원 등급 변경 테스트 - 관리자 해지
    @Test
    void changeRoleCode_shouldDemoteFromManager() {
        // Given
        Long memberId = 2L;
        boolean hasManagerRights = false; // 관리자 해지
        Member mockMember = Member.builder()
                .id(2L)
                .name("John Doe")
                .roleCode("RC003")
                .email("john.doe@example.com")
                .build();

        Mockito.when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        Mockito.when(memberRepository.updateRoleCode(memberId, "RC001")).thenReturn(1);

        // When
        memberManagementService.changeRoleCode(memberId, hasManagerRights);

        // Then
        Mockito.verify(memberRepository, times(1)).findById(memberId);
        Mockito.verify(memberRepository, times(1)).updateRoleCode(memberId, "RC001");
    }

    // 4. 회원 정보가 없는 경우 예외 처리 테스트
    @Test
    void changeRoleCode_shouldThrowEntityNotFoundException() {
        // Given
        Long memberId = 3L;
        boolean hasManagerRights = true;

        Mockito.when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            memberManagementService.changeRoleCode(memberId, hasManagerRights);
        });

        assertEquals("회원 정보를 찾을 수 없습니다. 회원 ID: 3", exception.getMessage());
        Mockito.verify(memberRepository, times(1)).findById(memberId);
    }
}