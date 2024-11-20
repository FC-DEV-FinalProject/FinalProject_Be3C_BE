package com.be3c.sysmetic.domain.strategy.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface ExcelService {
    void uploadExcel(MultipartFile file, Long strategyId);
    InputStream downloadDailyExcel(Long strategyId);
    InputStream downloaDailyExcelWithStatistics(Long strategyId);
    InputStream downloadMonthlyExcel(Long strategyId);
}
