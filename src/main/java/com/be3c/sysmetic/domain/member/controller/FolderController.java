package com.be3c.sysmetic.domain.member.controller;

import com.be3c.sysmetic.domain.member.dto.FolderGetRequestDto;
import com.be3c.sysmetic.domain.member.entity.Folder;
import com.be3c.sysmetic.domain.member.service.FolderService;
import com.be3c.sysmetic.global.common.response.ApiResponse;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import com.be3c.sysmetic.global.util.CustomUserDetails;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FolderController {

    private final FolderService folderService;

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
            Long userId = getUserIdInSecurityContext();

            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.success(folderService.getUserFolders(userId)));
        } catch (NoSuchElementException | EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail(ErrorCode.BAD_REQUEST, "잘못된 요청입니다."));
        }
    }

    // @PreAuthorize("hasRole('ROLE_USER') and !hasRole('ROLE_TRADER')")
    @GetMapping("/member/folder/availability")
    public ResponseEntity<ApiResponse<String>> getDuplCheck(
            @RequestParam String name
    ) throws Exception {
        try {
            Long userId = getUserIdInSecurityContext();
            folderService.duplCheck(userId, name);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.success());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.fail(ErrorCode.DUPLICATE_RESOURCE));
        }
    }

    private Long getUserIdInSecurityContext() throws AuthenticationException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if(principal instanceof UserDetails userDetails) {
            return ((CustomUserDetails) userDetails).getUserId();
        }
        return null;
    }
}
