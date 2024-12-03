package com.be3c.sysmetic.domain.strategy.controller;

import com.be3c.sysmetic.domain.strategy.service.ExcelService;
import com.be3c.sysmetic.global.common.response.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RestController
@RequestMapping("/v1/excel")
@RequiredArgsConstructor
public class ExcelControllerImpl implements ExcelController {

    final ExcelService excelService;

    @Override
    @GetMapping("/daily")
    public ResponseEntity<APIResponse<String>> getExcelExample() {

        String url = excelService.getExcelFormUrl();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(APIResponse.success(url));
    }

    @Override
    @PostMapping(value = "/daily/{strategyId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<APIResponse<String>> uploadExcel(
            @RequestParam("file") MultipartFile file,
            @PathVariable Long strategyId) {

        excelService.uploadExcel(file, strategyId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(APIResponse.create());
    }

    @Override
    @GetMapping("/daily/{strategyId}")
    public ResponseEntity<APIResponse<InputStreamResource>> downloadDailyExcel(
            @PathVariable Long strategyId) {

        InputStream inputStream = excelService.downloadDailyExcel(strategyId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=Daily_Data.xlsx");
        headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(APIResponse.success(new InputStreamResource(inputStream)));
    }

    @Override
    @GetMapping("/daily/statistics/{strategyId}")
    public ResponseEntity<APIResponse<InputStreamResource>> downloadDailyExcelWithStatistics(
            @PathVariable Long strategyId) {

        InputStream inputStream = excelService.downloadDailyExcelWithStatistics(strategyId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=Daily_Data_Statistics.xlsx");
        headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(APIResponse.success(new InputStreamResource(inputStream)));
    }

    @Override
    @GetMapping("/monthly/{strategyId}")
    public ResponseEntity<APIResponse<InputStreamResource>> downloadMonthlyExcel(
            @PathVariable Long strategyId) {

        InputStream inputStream = excelService.downloadMonthlyExcel(strategyId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=Monthly_Data.xlsx");
        headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(APIResponse.success(new InputStreamResource(inputStream)));
    }
}
