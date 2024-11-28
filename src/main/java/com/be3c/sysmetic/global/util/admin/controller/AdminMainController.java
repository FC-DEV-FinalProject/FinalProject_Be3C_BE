package com.be3c.sysmetic.global.util.admin.controller;

import com.be3c.sysmetic.global.common.response.APIResponse;
import com.be3c.sysmetic.global.util.admin.dto.AdminMainResponseDto;
import com.be3c.sysmetic.global.util.admin.service.AdminMainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AdminMainController {

    private final AdminMainService adminMainService;

    @GetMapping("/v1/admin/")
    public ResponseEntity<APIResponse<AdminMainResponseDto>> getAdminMain() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(adminMainService.getAdminMain()));
    }
}
