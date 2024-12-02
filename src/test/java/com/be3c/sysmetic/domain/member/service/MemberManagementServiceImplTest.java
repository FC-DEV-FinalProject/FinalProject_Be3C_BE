package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.dto.MemberGetResponseDto;
import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.entity.MemberSearchRole;
import com.be3c.sysmetic.domain.member.entity.MemberSearchType;
import com.be3c.sysmetic.domain.member.exception.MemberBadRequestException;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.global.common.response.PageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MemberManagementServiceImplTest {

    @InjectMocks
    private MemberManagementServiceImpl memberManagementService;

    @Mock
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 회원목록_조회_성공_테스트() {
        // Given
        MemberSearchRole role = MemberSearchRole.ALL;
        int page = 1;
        MemberSearchType searchType = MemberSearchType.EMAIL;
        String searchKeyword = "test";

        Pageable pageable = PageRequest.of(page - 1, 10);
        List<MemberGetResponseDto> content = List.of(
                new MemberGetResponseDto(1L, "RC001", "test1@test.com", "Test1", "Nickname1", null, "01012345678"),
                new MemberGetResponseDto(2L, "RC002", "test2@test.com", "Test2", "Nickname2", null, "01098765432")
        );

        Page<MemberGetResponseDto> membersPage = new PageImpl<>(content, pageable, content.size());
        when(memberRepository.findMembers(anyString(), anyString(), anyString(), any(Pageable.class))).thenReturn(membersPage);

        // When
        PageResponse<MemberGetResponseDto> response = memberManagementService.findMemberPage(role, page, searchType, searchKeyword);

        // Then
        assertThat(response.getContent()).hasSize(2);
        assertThat(response.getContent().get(0).getEmail()).isEqualTo("test1@test.com");
        assertThat(response.getContent().get(1).getEmail()).isEqualTo("test2@test.com");
        verify(memberRepository, times(1)).findMembers(anyString(), anyString(), anyString(), any(Pageable.class));
    }

    @Test
    void 회원등급_변경_성공_테스트_관리자_지정() {
        // Given
        Long memberId = 1L;
        boolean hasManagerRights = true;

        Member member = Member.builder()
                .id(memberId)
                .roleCode("RC001") // 일반 회원
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(memberRepository.updateRoleCode(anyLong(), eq("RC003"))).thenReturn(1); // RC001 -> RC003 변경 성공

        // When
        memberManagementService.changeRoleCode(memberId, hasManagerRights);

        // Then
        verify(memberRepository, times(1)).updateRoleCode(memberId, "RC003");
    }

    @Test
    void 회원등급_변경_실패_회원없음() {
        // Given
        Long memberId = 1L;
        boolean hasManagerRights = true;

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> memberManagementService.changeRoleCode(memberId, hasManagerRights))
                .isInstanceOf(MemberBadRequestException.class)
                .hasMessageContaining("회원 ID: " + memberId);
        verify(memberRepository, times(0)).updateRoleCode(anyLong(), anyString());
    }

    @Test
    void 회원등급_변경_실패_업데이트_실패() {
        // Given
        Long memberId = 1L;
        boolean hasManagerRights = false;

        Member member = Member.builder()
                .id(memberId)
                .roleCode("RC003") // 관리자
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(memberRepository.updateRoleCode(anyLong(), eq("RC001"))).thenReturn(0); // RC003 -> RC001 실패

        // When & Then
        assertThatThrownBy(() -> memberManagementService.changeRoleCode(memberId, hasManagerRights))
                .isInstanceOf(MemberBadRequestException.class)
                .hasMessageContaining("회원 ID: " + memberId);
        verify(memberRepository, times(1)).updateRoleCode(memberId, "RC001");
    }
}
