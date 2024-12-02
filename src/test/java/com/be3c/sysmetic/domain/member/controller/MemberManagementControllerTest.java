package com.be3c.sysmetic.domain.member.controller;

import com.be3c.sysmetic.domain.member.dto.MemberGetResponseDto;
import com.be3c.sysmetic.domain.member.entity.MemberSearchRole;
import com.be3c.sysmetic.domain.member.entity.MemberSearchType;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.domain.member.service.MemberManagementServiceImpl;
import com.be3c.sysmetic.global.common.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor
@ExtendWith(MockitoExtension.class)
class MemberManagementControllerTest {
    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberManagementServiceImpl memberService;

    @Test
    @DisplayName("유효한 입력값으로 멤버 리스트를 페이징 처리하여 반환")
    void findMemberPage_ShouldReturnPagedMembers_WhenValidInputsProvided() {
        // Given
        MemberSearchRole role = MemberSearchRole.USER;
        Integer page = 1;
        MemberSearchType searchType = MemberSearchType.NAME;
        String searchKeyword = "John";

        Pageable pageable = PageRequest.of(page - 1, 10);
        List<MemberGetResponseDto> memberList = List.of(
                 MemberGetResponseDto.builder().id(1L).name("John Doe").roleCode("USER").build(),
                MemberGetResponseDto.builder().id(2L).name("Jane Doe").roleCode("USER").build()
        );
        Page<MemberGetResponseDto> pageResult = new PageImpl<>(memberList, pageable, memberList.size());

        when(memberRepository.findMembers(role.getCode(), searchType.getCode(), searchKeyword, pageable))
                .thenReturn(pageResult);

        // When
        PageResponse<MemberGetResponseDto> response = memberService.findMemberPage(role, page, searchType, searchKeyword);

        // Then
        assertEquals(1, response.getTotalPages());
        assertEquals(2, response.getTotalElement());
        assertEquals(2, response.getPageSize());
        assertEquals(1, response.getCurrentPage());
        assertEquals("John Doe", response.getContent().get(0).getName());
        assertEquals("Jane Doe", response.getContent().get(1).getName());
    }

    @Test
    @DisplayName("검색 결과가 없는 경우 빈 페이지 반환")
    void findMemberPage_ShouldReturnEmptyPage_WhenNoMembersFound() {
        // Given
        MemberSearchRole role = MemberSearchRole.USER;
        Integer page = 1;
        MemberSearchType searchType = MemberSearchType.NAME;
        String searchKeyword = "NonExistent";

        Pageable pageable = PageRequest.of(page - 1, 10);
        Page<MemberGetResponseDto> emptyPage = Page.empty(pageable);

        when(memberRepository.findMembers(role.getCode(), searchType.getCode(), searchKeyword, pageable))
                .thenReturn(emptyPage);

        // When
        PageResponse<MemberGetResponseDto> response = memberService.findMemberPage(role, page, searchType, searchKeyword);

        // Then
        assertEquals(0, response.getTotalPages());
        assertEquals(0, response.getTotalElement());
        assertTrue(response.getContent().isEmpty());
    }

    @Test
    @DisplayName("잘못된 페이지 번호 입력 시 IllegalArgumentException 발생")
    void findMemberPage_ShouldThrowException_WhenPageIsInvalid() {
        // Given
        MemberSearchRole role = MemberSearchRole.USER;
        Integer invalidPage = 0;
        MemberSearchType searchType = MemberSearchType.NAME;
        String searchKeyword = "John";

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> memberService.findMemberPage(role, invalidPage, searchType, searchKeyword));
    }
}
