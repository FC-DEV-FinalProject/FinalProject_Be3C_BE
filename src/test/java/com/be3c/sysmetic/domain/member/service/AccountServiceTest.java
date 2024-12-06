package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.exception.MemberBadRequestException;
import com.be3c.sysmetic.domain.member.exception.MemberExceptionMessage;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("이메일 반환 - 성공")
    void findEmail_ShouldReturnEmail_WhenValidNameAndPhoneNumber() {
        // Given
        String name = "John";
        String phoneNumber = "01012345678";
        List<String> emailList = List.of("john@example.com");
        when(memberRepository.findEmailByNameAndPhoneNumber(name, phoneNumber)).thenReturn(emailList);

        // When
        String result = accountService.findEmail(name, phoneNumber);

        // Then
        assertThat(result).isEqualTo("john@example.com");
        verify(memberRepository, times(1)).findEmailByNameAndPhoneNumber(name, phoneNumber);
    }

    @Test
    @DisplayName("이메일 반환 - 실패 (회원 정보 없음)")
    void findEmail_ShouldThrowException_WhenNoMatchingMember() {
        // Given
        String name = "Jane";
        String phoneNumber = "01087654321";
        when(memberRepository.findEmailByNameAndPhoneNumber(name, phoneNumber)).thenReturn(Collections.emptyList());

        // When & Then
        assertThatThrownBy(() -> accountService.findEmail(name, phoneNumber))
                .isInstanceOf(MemberBadRequestException.class)
                .hasMessage(MemberExceptionMessage.NOT_FOUND_MEMBER.getMessage());
        verify(memberRepository, times(1)).findEmailByNameAndPhoneNumber(name, phoneNumber);
    }

    @Test
    @DisplayName("이메일 존재 확인 - 성공")
    void isPresentEmail_ShouldReturnTrue_WhenEmailExists() {
        // Given
        String email = "john@example.com";
        when(memberRepository.existsByEmail(email)).thenReturn(true);

        // When
        boolean result = accountService.isPresentEmail(email);

        // Then
        assertThat(result).isTrue();
        verify(memberRepository, times(1)).existsByEmail(email);
    }

    @Test
    @DisplayName("이메일 존재 확인 - 실패 (이메일 없음)")
    void isPresentEmail_ShouldThrowException_WhenEmailDoesNotExist() {
        // Given
        String email = "nonexistent@example.com";
        when(memberRepository.existsByEmail(email)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> accountService.isPresentEmail(email))
                .isInstanceOf(MemberBadRequestException.class)
                .hasMessage(MemberExceptionMessage.NOT_FOUND_MEMBER.getMessage());
        verify(memberRepository, times(1)).existsByEmail(email);
    }

    @Test
    @DisplayName("비밀번호 일치 여부 확인 - 성공")
    void isPasswordMatch_ShouldReturnTrue_WhenPasswordsMatch() {
        // Given
        String password = "password123";
        String rewritePassword = "password123";

        // When
        boolean result = accountService.isPasswordMatch(password, rewritePassword);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("비밀번호 일치 여부 확인 - 실패")
    void isPasswordMatch_ShouldThrowException_WhenPasswordsDoNotMatch() {
        // Given
        String password = "password123";
        String rewritePassword = "differentPassword";

        // When & Then
        assertThatThrownBy(() -> accountService.isPasswordMatch(password, rewritePassword))
                .isInstanceOf(MemberBadRequestException.class)
                .hasMessage(MemberExceptionMessage.PASSWORD_MISMATCH.getMessage());
    }

    @Test
    @DisplayName("비밀번호 재설정 - 성공")
    void resetPassword_ShouldReturnTrue_WhenPasswordIsUpdated() {
        // Given
        String email = "john@example.com";
        String password = "newPassword123!";
        String rewritePassword = "newPassword123!";
        when(memberRepository.updatePasswordByEmail(eq(email), anyString())).thenReturn(1);

        // When
        boolean result = accountService.resetPassword(email, password, rewritePassword);

        // Then
        assertThat(result).isTrue();
        verify(memberRepository, times(1)).updatePasswordByEmail(eq(email), anyString());
    }

    @Test
    @DisplayName("비밀번호 재설정 - 실패")
    void resetPassword_ShouldThrowException_WhenPasswordUpdateFails() {
        // Given
        String email = "john@example.com";
        String password = "newPassword123";
        String rewritePassword = "newPassword123!";
        when(memberRepository.updatePasswordByEmail(eq(email), anyString())).thenReturn(0);

        // When & Then
        assertThatThrownBy(() -> accountService.resetPassword(email, password, rewritePassword))
                .isInstanceOf(MemberBadRequestException.class)
                .hasMessage(MemberExceptionMessage.FAIL_PASSWORD_CHANGE.getMessage());
        verify(memberRepository, times(1)).updatePasswordByEmail(eq(email), anyString());
    }
}
