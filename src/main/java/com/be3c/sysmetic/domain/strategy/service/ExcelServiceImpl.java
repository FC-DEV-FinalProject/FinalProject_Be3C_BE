package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.DailyTransactionInputDataDto;
import com.be3c.sysmetic.domain.strategy.entity.Daily;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.repository.DailyRepository;
import com.be3c.sysmetic.domain.strategy.util.DoubleHandler;
import com.be3c.sysmetic.domain.strategy.util.StrategyCalculator;
import com.be3c.sysmetic.global.util.file.exception.InvalidFileFormatException;
import org.apache.poi.EmptyFileException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class ExcelServiceImpl implements ExcelService {

    final DailyRepository dailyRepository;
    final DoubleHandler doubleHandler;
    final StrategyCalculator strategyCalculator;    // 합친 후 추가

    public ExcelServiceImpl(DailyRepository dailyRepository, DoubleHandler doubleHandler, StrategyCalculator strategyCalculator) {
        this.dailyRepository = dailyRepository;
        this.doubleHandler = doubleHandler;
        this.strategyCalculator = strategyCalculator;
    }


    /**
     * 엑셀 파일을 읽어서 일간 데이터를 DB에 저장
     * @param file 규정된 컬럼 형식의 엑셀 파일
     * @param strategyId 일간 데이터는 전략id 해당 전략에 대한 데이터
     */
    @Transactional
    @Override
    public void uploadExcel(MultipartFile file, Long strategyId) {

        if (!isValidExcelFile(file)) {
            throw new InvalidFileFormatException("엑셀 업로드 실패: 파일 형식이 올바르지 않습니다.");
        }

//        int batchSize = 500; // 배치 작업 - 2차개발 추가
//        List<DailyTransactionDataDto> newDtos = new ArrayList<>(batchSize);

        /* 엑셀 to DTO */
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            int numberOfSheets = workbook.getNumberOfSheets();  // 여러개 해야되나?
            Sheet sheet = workbook.getSheetAt(0);

            List<DailyTransactionInputDataDto> newDtos = new ArrayList<>(sheet.getPhysicalNumberOfRows());

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                Date firstColumn = row.getCell(0).getDateCellValue();
                Double secondColumn = row.getCell(1).getNumericCellValue();
                Double thirdColumn = row.getCell(2).getNumericCellValue();

                LocalDate date = firstColumn.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                if(date.isAfter(LocalDate.now())){
                    throw new InvalidFileFormatException("엑셀 업로드 실패: 미래의 데이터는 입력할 수 없습니다.");
                }
                Double depositWithdrawalAmount = doubleHandler.cutDouble(secondColumn);
                Double profitLossAmount = doubleHandler.cutDouble(thirdColumn);

                newDtos.add(new DailyTransactionInputDataDto(date, depositWithdrawalAmount, profitLossAmount));

            }


            /* 비교해서 save */
            List<Daily> saveTarget = new ArrayList<>();

            List<Daily> existingEntities = dailyRepository.findAll();
            Map<LocalDate, Daily> existingEntityMap = new HashMap<>();
            for (Daily existingEntity : existingEntities) {
                existingEntityMap.put(existingEntity.getDate(), existingEntity);
            }

            for (DailyTransactionInputDataDto newDto : newDtos) {

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
                boolean buildFlag = false;

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
        } catch (IOException e) {
            throw new InvalidFileFormatException("엑셀 업로드 실패: 파일 읽기에 실패했습니다.", e);
        } catch (EmptyFileException e) {
            throw new InvalidFileFormatException("엑셀 업로드 실패: 파일 내용이 올바르지 않습니다.", e);
        }
    }


    /**
     * 전략의 일간 데이터를 엑셀 파일로 리턴
     * @param strategyId 일간 데이터는 전략id 해당 전략에 대한 데이터
     * @return 엑셀 파일
     */
    @Override
    public InputStream downloadExcel(Long strategyId) {

        List<Daily> entities = dailyRepository.findAll(); // 전략 아이디로 찾아야 함...!!

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Daily Transaction Data");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("날짜");
            headerRow.createCell(1).setCellValue("입출금");
            headerRow.createCell(2).setCellValue("일 손익");

            int rowNum = 1;
            for (Daily daily : entities) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(daily.getDate());
                row.createCell(1).setCellValue(daily.getDepositWithdrawalAmount());
                row.createCell(2).setCellValue(daily.getProfitLossAmount());
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            workbook.write(byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();

            return new ByteArrayInputStream(byteArray);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 전략의 일간 데이터를 일간 통계와 함께 엑셀 파일로 리턴
     * @param strategyId 일간 데이터는 전략id 해당 전략에 대한 데이터
     * @return 엑셀 파일
     */
    @Override
    public InputStream downloadExcelWithStatistics(Long strategyId) {

        List<Daily> entities = dailyRepository.findAll(); // 전략 아이디로 찾아야 함...!!

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Daily Transaction Data");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("날짜");
            headerRow.createCell(1).setCellValue("원금");
            headerRow.createCell(2).setCellValue("입출금");
            headerRow.createCell(3).setCellValue("일 손익");
            headerRow.createCell(4).setCellValue("일 손익률");
            headerRow.createCell(5).setCellValue("누적 손익");
            headerRow.createCell(6).setCellValue("누적 손익률");

            int rowNum = 1;
            for (Daily daily : entities) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(daily.getDate());
                row.createCell(1).setCellValue(daily.getPrincipal());
                row.createCell(2).setCellValue(daily.getDepositWithdrawalAmount());
                row.createCell(3).setCellValue(daily.getProfitLossAmount());
                row.createCell(4).setCellValue(daily.getProfitLossRate());
                row.createCell(5).setCellValue(daily.getAccumulatedProfitLossAmount());
                row.createCell(6).setCellValue(daily.getAccumulatedProfitLossRate());
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            workbook.write(byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();

            return new ByteArrayInputStream(byteArray);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Excel 2007 이상 파일이 존재하는지 확인 (.xlsx)
     * @param file 검증할 파일
     * @return 사용 가능한 파일일 시 true
     */
    private boolean isValidExcelFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.contains("spreadsheetml.sheet");
    }
}
