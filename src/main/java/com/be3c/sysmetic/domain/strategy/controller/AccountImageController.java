package com.be3c.sysmetic.domain.strategy.controller;

import com.be3c.sysmetic.domain.strategy.dto.AccountImageResponseDto;
import com.be3c.sysmetic.domain.strategy.service.AccountImageServiceImpl;
import com.be3c.sysmetic.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@RestController
public class AccountImageController {

    private final AccountImageServiceImpl accountImageService;

    @GetMapping("/strategy/account-image")
    public ResponseEntity<ApiResponse> getAccountImage(@RequestParam Long strategyId, @RequestParam int page) {
        Page<AccountImageResponseDto> accountImages = accountImageService.findAccountImage(strategyId, page);
        return ResponseEntity.ok(ApiResponse.success(accountImages));
    }

    @DeleteMapping("/strategy/account-image")
    public ResponseEntity<ApiResponse> deleteAccountImage(@RequestParam Long accountImageId) {
        accountImageService.deleteAccountImage(accountImageId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @PostMapping("/strategy/account-image")
    public ResponseEntity<ApiResponse> saveAccountImage(@RequestParam Long strategyId, @RequestBody String title) {
        accountImageService.saveAccountImage(strategyId, title);
        return ResponseEntity.ok(ApiResponse.success());
    }

}
