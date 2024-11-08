package com.be3c.sysmetic.domain.member.controller;

import com.be3c.sysmetic.domain.member.dto.FolderGetRequestDto;
import com.be3c.sysmetic.domain.member.dto.FolderGetResponseDto;
import com.be3c.sysmetic.domain.member.dto.FolderPostRequestDto;
import com.be3c.sysmetic.domain.member.dto.FolderPutRequestDto;
import com.be3c.sysmetic.domain.member.entity.Folder;
import com.be3c.sysmetic.domain.member.service.FolderService;
import com.be3c.sysmetic.global.common.response.ApiResponse;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import com.be3c.sysmetic.global.util.CustomUserDetails;
import com.be3c.sysmetic.global.util.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FolderController {

    private final FolderService folderService;

    private final SecurityUtils securityUtils;

    /*
        해당 유저의 폴더 목록 중에서 겹치는 이름이 존재하는지 확인하는 api
     */
    /*
        해당 유저의 폴더 목록을 반환하는 api
        트레이더는 해당 메서드에 접근 불가능.
     */
    // @PreAuthorize("hasRole('ROLE_USER') and !hasRole('ROLE_TRADER')")
    @GetMapping("/member/folder")
    public ResponseEntity<ApiResponse<List<Folder>>> getAllFolder(

    ) throws Exception {
        try {
            List<Folder> folderList = folderService.getUserFolders(
                    securityUtils.getUserIdInSecurityContext());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.success(folderList));
        } catch (NoSuchElementException | EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail(ErrorCode.BAD_REQUEST, "잘못된 요청입니다."));
        } catch (AuthenticationCredentialsNotFoundException | UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.fail(ErrorCode.FORBIDDEN, e.getMessage()));
        }
    }

    /*
        폴더명 중복 체크
     */
    // @PreAuthorize("hasRole('ROLE_USER') and !hasRole('ROLE_TRADER')")
    @GetMapping("/member/folder/availability")
    public ResponseEntity<ApiResponse<String>> getDuplCheck(
            @RequestParam String folderName
    ) throws Exception {
        try {
            folderService.duplCheck(
                    securityUtils.getUserIdInSecurityContext(),
                    folderName);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.success());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.fail(ErrorCode.DUPLICATE_RESOURCE));
        } catch (AuthenticationCredentialsNotFoundException | UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.fail(ErrorCode.FORBIDDEN, e.getMessage()));
        }
    }

    /*
        폴더 추가 메서드
     */
    @PostMapping("/member/folder/")
    public ResponseEntity<ApiResponse<String>> postFolder(
            @RequestBody FolderPostRequestDto folderPostRequestDto
    ) throws Exception {
        try {
            folderService.insertFolder(folderPostRequestDto, securityUtils.getUserIdInSecurityContext());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.success());
        } catch (HttpStatusCodeException e) {
            if(e.getStatusCode() == HttpStatus.CONFLICT) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(ApiResponse.fail(ErrorCode.DUPLICATE_RESOURCE));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail(ErrorCode.BAD_REQUEST, e.getMessage()));
        } catch (ResponseStatusException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail(ErrorCode.BAD_REQUEST, e.getMessage()));
        } catch (AuthenticationCredentialsNotFoundException | UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.fail(ErrorCode.FORBIDDEN, e.getMessage()));
        }
    }

    /*
        폴더명 수정 메서드
     */
    // @PreAuthorize("hasRole('ROLE_USER') and !hasRole('ROLE_TRADER')")
    @PutMapping("/member/folder")
    public ResponseEntity<ApiResponse<String>> putFolder(
            @RequestBody FolderPutRequestDto folderPutRequestDto
    ) throws Exception {
        try {
            if(folderService
                    .updateFolder(
                            folderPutRequestDto,
                            securityUtils.getUserIdInSecurityContext())) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(ApiResponse.success());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail(
                            ErrorCode.BAD_REQUEST,
                            "알 수 없는 이유로 폴더명 수정에 실패했습니다."));
        } catch(IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail(ErrorCode.BAD_REQUEST, e.getMessage()));
        } catch (AuthenticationCredentialsNotFoundException |
                 UsernameNotFoundException |
                 ResponseStatusException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.fail(ErrorCode.FORBIDDEN, e.getMessage()));
        }
    }
}
