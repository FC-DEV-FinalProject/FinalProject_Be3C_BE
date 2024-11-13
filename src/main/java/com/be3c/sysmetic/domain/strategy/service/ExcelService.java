package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.DailyTransactionDataDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ExcelService {
    public List<DailyTransactionDataDto> uploadExcel(MultipartFile file, Long StrategyId);
    public List<DailyTransactionDataDto> downloadExcel();
}
