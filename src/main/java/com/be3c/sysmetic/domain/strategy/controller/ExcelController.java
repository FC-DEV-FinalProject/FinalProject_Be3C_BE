package com.be3c.sysmetic.domain.strategy.controller;

import com.be3c.sysmetic.domain.strategy.service.ExcelService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RestController
@RequestMapping("/api/excel")
public class ExcelController {

    final ExcelService excelService;

    public ExcelController(ExcelService excelService) {
        this.excelService = excelService;
    }


    @PostMapping("/upload")
    public ResponseEntity<?> uploadExcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam("strategyId") Long strategyId) {

        excelService.uploadExcel(file, strategyId);

        return ResponseEntity.status(HttpStatus.CREATED).body("엑셀 파일 입력 완료");
    }


    @GetMapping("/download/daily")
    public ResponseEntity<?> downloadDailyExcel(
            @RequestParam("strategyId") Long strategyId) {

        InputStream inputStream = excelService.downloadExcel(strategyId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=Daily_Data.xlsx");
        headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(inputStream));
    }


    @GetMapping("/download/daily/statistics")
    public ResponseEntity<?> downloadDailyExcelWithStatistics(
            @RequestParam("strategyId") Long strategyId) {

        InputStream inputStream = excelService.downloadExcelWithStatistics(strategyId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=Daily_Data.xlsx");
        headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(inputStream));
    }
}