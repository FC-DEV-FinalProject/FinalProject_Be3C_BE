package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.dto.FolderPostRequestDto;
import com.be3c.sysmetic.domain.member.dto.FolderPutRequestDto;
import com.be3c.sysmetic.domain.member.entity.Folder;
import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.exception.ResourceLimitExceededException;
import com.be3c.sysmetic.domain.member.repository.FolderRepository;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.global.common.Code;
import com.be3c.sysmetic.global.config.security.CustomUserDetails;
import com.be3c.sysmetic.global.exception.ConflictException;
import com.be3c.sysmetic.global.util.SecurityUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(locations = "/application-test.properties")
@Slf4j
@SpringBootTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FolderServiceTest {

    @Autowired
    private FolderService folderService;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private MemberRepository memberRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();
        folderRepository.deleteAll();

        entityManager.createNativeQuery("ALTER TABLE member AUTO_INCREMENT = 1")
                .executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE folder AUTO_INCREMENT = 1")
                .executeUpdate();

        Member member = Member.builder()
                .email("test@test.com")
                .password(bCryptPasswordEncoder.encode("encodedPassword"))
                // 초기값 설정
                .id(1L)
                .roleCode("USER")
                .name("테스트")
                .nickname("테스트")
                .phoneNumber("01012341234")
                .usingStatusCode("US001")
                .totalFollow(0)
                .totalStrategyCount(0)
                .birth(LocalDate.of(2000, 1, 1))
                .receiveInfoConsent("Y")
                .infoConsentDate(LocalDateTime.now())
                .receiveMarketingConsent("Y")
                .marketingConsentDate(LocalDateTime.now())
                .build();

        memberRepository.save(member);

        // 권한 설정
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        // CustomUserDetails 생성
        CustomUserDetails userDetails = new CustomUserDetails(
                1L, // memberId
                "test@example.com", // email
                "USER", // role
                authorities // 권한 목록,
        );

        // Authentication 객체 생성
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // SecurityContext에 Authentication 객체 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @DisplayName("폴더 생성 테스트 - 성공")
    @Order(1)
    public void testCreateFolder() {
        Long userId = securityUtils.getUserIdInSecurityContext();

        folderService.insertFolder(FolderPostRequestDto.builder()
                .name("테스트폴더")
                .checkDupl(true)
                .build());

        folderRepository.findByMemberIdAndStatusCode(userId, Code.USING_STATE.getCode());
    }

    @Test
    @DisplayName("폴더 생성 테스트 - 실패 : 폴더 5개 이상 만들기 시도")
    @Order(2)
    public void testCreateFolderSix() {
        // given
        Long userId = securityUtils.getUserIdInSecurityContext();

        for(int i = 0; i < 5; i++) {
            folderService.insertFolder(FolderPostRequestDto.builder()
                            .name("테스트" + i)
                            .checkDupl(true)
                            .build());
        }

        //when
        assertEquals(folderRepository.countFoldersByUser(userId, Code.USING_STATE.getCode()), 5);

        //then
        assertThrows(ResourceLimitExceededException.class, () -> {
            folderService.insertFolder(FolderPostRequestDto.builder()
                    .name("테스트" + 6)
                    .checkDupl(true)
                    .build());
        });
    }

    @Test
    @DisplayName("폴더 생성 테스트 - 실패 : 중복된 이름의 폴더 추가 시도")
    @Order(3)
    public void testInsertFolderDuplName() {
        folderService.insertFolder(FolderPostRequestDto.builder()
                .name("테스트폴더")
                .checkDupl(true)
                .build());

        assertThrows(ConflictException.class, () -> {
            folderService.insertFolder(FolderPostRequestDto.builder()
                    .name("테스트폴더")
                    .checkDupl(true)
                    .build());
        });
    }

    @Test
    @DisplayName("폴더 생성 테스트 - 실패 : 중복 체크 미 완료 요청")
    @Order(4)
    public void testInsertFolderNotDuplCheckRequest() {
        assertThrows(IllegalStateException.class, () -> {
            folderService.insertFolder(FolderPostRequestDto.builder()
                    .name("테스트폴더")
                    .checkDupl(false)
                    .build());
        });
    }

    @Test
    @DisplayName("폴더 수정 테스트 - 성공")
    @Order(5)
    public void testUpdateFolder() {
        // given
        folderService.insertFolder(FolderPostRequestDto.builder()
                .name("테스트폴더")
                .checkDupl(true)
                .build());

        FolderPutRequestDto folderPutRequestDto = FolderPutRequestDto.builder()
                .folderId(1L)
                .folderName("수정된테스트폴더")
                .checkDupl(true)
                .build();

        folderService.updateFolder(folderPutRequestDto);

        Folder folder = folderRepository.findByIdAndStatusCode(
                1L,
                Code.USING_STATE.getCode()
        ).orElseThrow(EntityNotFoundException::new);

        assertEquals(folder.getName(), folderPutRequestDto.getFolderName());
    }

    @Test
    @DisplayName("폴더 수정 테스트 - 실패 : 중복 체크 미실시 요청")
    @Order(6)
    public void testUpdateFolderNotDuplCheckRequest() {
        folderService.insertFolder(FolderPostRequestDto.builder()
                .name("테스트폴더")
                .checkDupl(true)
                .build());

        assertThrows(IllegalStateException.class, () -> {
            folderService.updateFolder(FolderPutRequestDto.builder()
                            .folderId(1L)
                    .folderName("테스트폴더")
                    .checkDupl(false)
                    .build());
        });
    }

    @Test
    @DisplayName("폴더 수정 테스트 - 실패 : 중복된 이름 존재")
    @Order(6)
    public void testUpdateFolderDuplNameRequest() {
        folderService.insertFolder(FolderPostRequestDto.builder()
                .name("테스트폴더")
                .checkDupl(true)
                .build());

        folderService.insertFolder(FolderPostRequestDto.builder()
                .name("중복테스트폴더")
                .checkDupl(true)
                .build());

        assertThrows(ConflictException.class, () -> {
            folderService.updateFolder(FolderPutRequestDto.builder()
                    .folderId(1L)
                    .folderName("중복테스트폴더")
                    .checkDupl(true)
                    .build());
        });
    }

    @Test
    @DisplayName("폴더 수정 테스트 - 실패 : 미존재 폴더 수정 시도")
    @Order(7)
    public void testUpdateFolderNotExist() {
        assertThrows(EntityNotFoundException.class, () -> {
            folderService.updateFolder(FolderPutRequestDto.builder()
                    .folderId(1L)
                    .folderName("중복테스트폴더")
                    .checkDupl(true)
                    .build());
        });
    }

    @Test
    @DisplayName("폴더 삭제 테스트 - 성공")
    @Order(8)
    public void testDeleteFolder() {
        folderService.insertFolder(FolderPostRequestDto.builder()
                .name("테스트폴더")
                .checkDupl(true)
                .build());

        folderService.insertFolder(FolderPostRequestDto.builder()
                .name("테스트폴더2")
                .checkDupl(true)
                .build());

        folderService.deleteFolder(1L);
    }

    @Test
    @DisplayName("폴더 삭제 테스트 - 실패 : 1개 남은 폴더 삭제 시도")
    @Order(9)
    public void testDeleteLastFolder() {
        folderService.insertFolder(FolderPostRequestDto.builder()
                .name("테스트폴더")
                .checkDupl(true)
                .build());

        assertThrows(IllegalStateException.class, () -> {
            folderService.deleteFolder(1L);
        });
    }

    @Test
    @DisplayName("폴더 삭제 테스트 - 실패 : 존재하지 않는 폴더 삭제 시도")
    @Order(10)
    public void testDeleteNotExistFolder() {
        folderService.insertFolder(FolderPostRequestDto.builder()
                .name("테스트폴더")
                .checkDupl(true)
                .build());

        assertThrows(EntityNotFoundException.class, () -> {
            folderService.deleteFolder(2L);
        });
    }
}
