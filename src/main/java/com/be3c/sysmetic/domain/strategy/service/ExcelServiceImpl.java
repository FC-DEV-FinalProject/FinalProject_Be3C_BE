package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.DailyTransactionDataDto;
import com.be3c.sysmetic.domain.strategy.entity.Daily;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.repository.DailyRepository;
import com.be3c.sysmetic.domain.strategy.util.DoubleHandler;
import com.be3c.sysmetic.global.util.file.exception.InvalidFileFormatException;
import org.apache.poi.EmptyFileException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class ExcelServiceImpl implements ExcelService {

    final DailyRepository dailyRepository;
    final DoubleHandler doubleHandler;

    public ExcelServiceImpl(DailyRepository dailyRepository, DoubleHandler doubleHandler) {
        this.dailyRepository = dailyRepository;
        this.doubleHandler = doubleHandler;
    }

    @Transactional
    @Override
    public List<DailyTransactionDataDto> uploadExcel(MultipartFile file, Long strategyId) {
        String type = file.getContentType();
        if (!type.contains("spreadsheetml.sheet")) // Excel 2007 이상 파일 .xlsx
            throw new InvalidFileFormatException("엑셀 업로드 실패: 파일 형식이 올바르지 않습니다.");

//        int batchSize = 500; // 배치 작업 - 2차개발 추가
//        List<DailyTransactionDataDto> newDtos = new ArrayList<>(batchSize);

        /* 엑셀 to DTO */
        List<DailyTransactionDataDto> newDtos = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            int numberOfSheets = workbook.getNumberOfSheets();  // 여러개 해야되나?
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;     // 헤더 체크 해야되나?

                Date firstColumn = row.getCell(0).getDateCellValue();
                double secondColumn = row.getCell(1).getNumericCellValue();
                double thirdColumn = row.getCell(2).getNumericCellValue();

                LocalDate date = firstColumn.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                Double depositWithdrawalAmount = Double.valueOf(secondColumn);
                Double profitLossAmount = Double.valueOf(thirdColumn);
                depositWithdrawalAmount = doubleHandler.cutDouble(depositWithdrawalAmount);
                profitLossAmount = doubleHandler.cutDouble(profitLossAmount);

                DailyTransactionDataDto dto = new DailyTransactionDataDto(date, depositWithdrawalAmount, profitLossAmount);
                newDtos.add(dto);

            }


            /* 비교해서 save */
            List<Daily> saveTarget = new ArrayList<>();

            List<Daily> existingEntities = dailyRepository.findAll();
            Map<LocalDate, Daily> existingEntityMap = new HashMap<>();
            for (Daily existingEntity : existingEntities) {
                existingEntityMap.put(existingEntity.getDate(), existingEntity);
            }

            for (DailyTransactionDataDto newDto : newDtos) {

                Daily existingEntity = existingEntityMap.get(newDto.date());

                /* Add completely new data */
                if (existingEntity == null) {
                    saveTarget.add(Daily.builder()
                            .strategy(Strategy.builder()
                                    .id(strategyId).build())
                            .date(newDto.date())
                            .depositWithdrawalAmount(newDto.depositWithdrawalAmount())
                            .profitLossAmount(newDto.profitLossAmount())
                            .build());
                    continue;
                }

                /* Add updated data */
                Daily.DailyBuilder entityBuilder = Daily.builder()
                        .strategy(existingEntity.getStrategy())
                        .id(existingEntity.getId())
                        .date(existingEntity.getDate());
                Boolean buildFlag = false;


                if (!doubleHandler.compare(newDto.depositWithdrawalAmount(), existingEntity.getDepositWithdrawalAmount())) {
                    entityBuilder.depositWithdrawalAmount(newDto.depositWithdrawalAmount());
                    buildFlag = true;
                }

                if (!doubleHandler.compare(newDto.profitLossAmount(), existingEntity.getProfitLossAmount())) {
                    entityBuilder.profitLossAmount(newDto.profitLossAmount());
                    buildFlag = true;
                }

                if (buildFlag) {
                    saveTarget.add(entityBuilder.build());
                }
            }

            dailyRepository.saveAll(saveTarget);

            // 남은 데이터 처리
//            if (!dataList.isEmpty()) {
//                processData(dataList, dailyDtos);
//            }

            return null;

        } catch (IOException e) {
            throw new InvalidFileFormatException("엑셀 업로드 실패: 파일 읽기에 실패했습니다.", e);
        } catch (EmptyFileException e) {
            throw new InvalidFileFormatException("엑셀 업로드 실패: 파일 내용이 올바르지 않습니다.", e);
        }
    }

    @Override
    public List<DailyTransactionDataDto> downloadExcel() {
        return null;
    }

}
