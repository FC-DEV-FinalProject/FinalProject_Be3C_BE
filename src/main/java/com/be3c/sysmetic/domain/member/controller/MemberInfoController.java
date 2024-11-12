package com.be3c.sysmetic.domain.member.controller;

import com.be3c.sysmetic.domain.member.dto.MemberPatchInfoRequestDto;
import com.be3c.sysmetic.domain.member.dto.MemberPutPasswordRequestDto;
import com.be3c.sysmetic.domain.member.service.MemberInfoService;
import com.be3c.sysmetic.global.common.response.ApiResponse;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import com.be3c.sysmetic.global.util.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MemberInfoController {

    private final MemberInfoService memberInfoService;
    private final SecurityUtils securityUtils;

    @PutMapping("/member/info/password")
    public ResponseEntity<ApiResponse<String>> putPassword(
            @RequestBody MemberPutPasswordRequestDto memberPutPasswordRequestDto,
            HttpServletRequest request
    ) throws Exception {
        try {
            if(memberInfoService
                    .changePassword(
                            memberPutPasswordRequestDto,
                            securityUtils.getUserIdInSecurityContext(),
                            request)
            ) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(ApiResponse.success());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail(ErrorCode.BAD_REQUEST, e.getMessage()));
        }
    }

    @PatchMapping("/member/info")
    public ResponseEntity<ApiResponse<String>> updateMemberInfo(
            @RequestBody MemberPatchInfoRequestDto memberPatchInfoRequestDto
    ) throws Exception {
        try {
            if(memberInfoService
                    .changeMemberInfo(
                            memberPatchInfoRequestDto,
                            securityUtils.getUserIdInSecurityContext()
                    )
            ) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(ApiResponse.success());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail(ErrorCode.BAD_REQUEST, e.getMessage()));
        }
    }
}
