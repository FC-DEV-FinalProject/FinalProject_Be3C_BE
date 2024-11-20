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
@RequestMapping("/api/excel")
@RequiredArgsConstructor
public class ExcelController {

    final ExcelService excelService;


    @PostMapping("/upload")
    public ResponseEntity<APIResponse<String>> uploadExcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam("strategyId") Long strategyId) {

        excelService.uploadExcel(file, strategyId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(APIResponse.create("엑셀 파일 입력 완료"));
    }


    @GetMapping("/download/daily")
    public ResponseEntity<APIResponse<InputStreamResource>> downloadDailyExcel(
            @RequestParam("strategyId") Long strategyId) {

        InputStream inputStream = excelService.downloadDailyExcel(strategyId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=Daily_Data.xlsx");
        headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(APIResponse.success(new InputStreamResource(inputStream)));
    }


    @GetMapping("/download/daily/statistics")
    public ResponseEntity<APIResponse<InputStreamResource>> downloadDailyExcelWithStatistics(
            @RequestParam("strategyId") Long strategyId) {

        InputStream inputStream = excelService.downloaDailyExcelWithStatistics(strategyId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=Daily_Data.xlsx");
        headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(APIResponse.success(new InputStreamResource(inputStream)));
    }


    @GetMapping("/download/monthly")
    public ResponseEntity<APIResponse<InputStreamResource>> downloadMonthlyExcel(
            @RequestParam("strategyId") Long strategyId) {

        InputStream inputStream = excelService.downloadMonthlyExcel(strategyId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=Daily_Data.xlsx");
        headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(APIResponse.success(new InputStreamResource(inputStream)));
    }
}