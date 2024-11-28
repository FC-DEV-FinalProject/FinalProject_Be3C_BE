package com.be3c.sysmetic.global.util.admin.controller;

import com.be3c.sysmetic.global.util.admin.service.AdminMainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AdminMainController {

    private final AdminMainService adminMainService;
}
