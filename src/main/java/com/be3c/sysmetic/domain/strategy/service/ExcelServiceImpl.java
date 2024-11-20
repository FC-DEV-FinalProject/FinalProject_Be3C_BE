package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.DailyTransactionInputDataDto;
import com.be3c.sysmetic.domain.strategy.entity.Daily;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.repository.DailyRepository;
import com.be3c.sysmetic.domain.strategy.repository.StrategyRepository;
import com.be3c.sysmetic.domain.strategy.util.DoubleHandler;
import com.be3c.sysmetic.domain.strategy.util.StrategyCalculator;
import com.be3c.sysmetic.global.util.file.exception.InvalidFileFormatException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.EmptyFileException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ExcelServiceImpl implements ExcelService {

    final DailyRepository dailyRepository;
    final StrategyRepository strategyRepository;
    final DoubleHandler doubleHandler;
    final StrategyCalculator strategyCalculator;

    /**
     * 엑셀 파일을 읽어서 일간 데이터를 DB에 저장
     * @param file 규정된 컬럼 형식의 엑셀 파일
     * @param strategyId 일간 데이터는 전략id 해당 전략에 대한 데이터
     */
    @Transactional
    @Override
    public void uploadExcel(MultipartFile file, Long strategyId) {

        Strategy strategy = strategyRepository.findById(strategyId).get();

        if (!isValidExcelFile(file)) {
            throw new InvalidFileFormatException("엑셀 업로드 실패: 파일 형식이 올바르지 않습니다.");
        }

//        int batchSize = 500; // 배치 작업 - 2차개발 추가
//        List<DailyTransactionDataDto> newDtos = new ArrayList<>(batchSize);

        /* 엑셀 to DTO */
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);   // 첫번째 시트만 읽음

            List<DailyTransactionInputDataDto> newDtos = new ArrayList<>(sheet.getPhysicalNumberOfRows());

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                Date firstColumn = row.getCell(0).getDateCellValue();
                Double secondColumn = row.getCell(1).getNumericCellValue();
                Double thirdColumn = row.getCell(2).getNumericCellValue();

                LocalDate date = firstColumn.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                if (date.isAfter(LocalDate.now())) {
                    throw new InvalidFileFormatException("엑셀 업로드 실패: 미래의 데이터는 입력할 수 없습니다.");
                }
                Double depositWithdrawalAmount = doubleHandler.cutDouble(secondColumn);
                Double profitLossAmount = doubleHandler.cutDouble(thirdColumn);

                newDtos.add(new DailyTransactionInputDataDto(date, depositWithdrawalAmount, profitLossAmount));

            }

            /* 날짜 순 정렬 보장하기 */
            boolean isSorted = true;
            for (int i = 1; i < newDtos.size(); i++) {
                if (newDtos.get(i).date().isBefore(newDtos.get(i - 1).date())) {
                    isSorted = false;
                    break;
                }
            }

            if (!isSorted) {
                newDtos.sort(Comparator.comparing(DailyTransactionInputDataDto::date));
            }


            /* 비교해서 save */
            List<Daily> saveTargets = new ArrayList<>();

            List<Daily> existingEntities = dailyRepository.findAll(Sort.by(Sort.Order.asc("date"))); // date로 잘라서 가져오게 수정하자

            for (int i = 0; i < newDtos.size(); i++) {

                DailyTransactionInputDataDto newDto = newDtos.get(i);

                LocalDate newDailyDate = newDto.date();

                int existingIndex = Collections.binarySearch(existingEntities, newDto.toEntity(strategy));

                /* Add completely new data */
                if (existingIndex < 0) {

                    Daily daily = Daily.builder()
                            .strategy(strategy)
                            .date(newDailyDate)
                            .depositWithdrawalAmount(newDto.depositWithdrawalAmount())
                            .profitLossAmount(newDto.profitLossAmount())
                            .build();

                    saveTargets.add(daily);

                } else {
                    /* Add updated data */

                    int insertPoint = -(existingIndex + 1);
                    Daily existingEntity = existingEntities.get(insertPoint);

                    Daily.DailyBuilder entityBuilder = Daily.builder();
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
                        saveTargets.add(entityBuilder
                                .strategy(existingEntity.getStrategy())
                                .id(existingEntity.getId())
                                .date(existingEntity.getDate())
                                .build());
                    }
                }

            }

/**
 *
 *
 * 계산 항목을 추가하는 걸로 바꾸기
 *
 *
 */

            /* 계산하기 */
            for (int i = 0; i < saveTargets.size(); i++) {

                Daily saveTarget = saveTargets.get(i);

                LocalDate targetDate = saveTarget.getDate();

                /* 계산을 위한 기존 데이터 찾기 */
                int indexSearchResult = Collections.binarySearch(existingEntities, saveTarget);
                int insertPoint = indexSearchResult < 0 ? -(indexSearchResult + 1) : indexSearchResult;

                // 첫번째 요소인지 명시, 첫번째 요소가 아니라면 데이터 찾아오기
                boolean isFirstElement = (insertPoint == 0);
                Optional<Daily> formerExistingData = (insertPoint == 0) ?
                        Optional.empty() :
                        Optional.ofNullable(existingEntities.get(insertPoint - 1));

                boolean isFirstSaveTarget = (i == 0);


                Daily calculateStadard = null;

                if (!isFirstSaveTarget && !isFirstElement) {
                    Daily formerSaveTarget = saveTargets.get(i - 1);

                    boolean flag = isClosestDateDate1(targetDate,
                            formerExistingData.get().getDate(),
                            formerSaveTarget.getDate());

                    calculateStadard = flag ? formerExistingData.get() : formerSaveTarget;
                } else if (!isFirstSaveTarget && isFirstElement) {
                    calculateStadard = saveTargets.get(i - 1);
                } else if (isFirstSaveTarget && !isFirstElement) {
                    calculateStadard = formerExistingData.get();
                } else if (isFirstSaveTarget && isFirstElement) {
                    calculateStadard = Daily.builder()
                            .currentBalance(0.0)
                            .build();
                }


                Double deposit = saveTarget.getDepositWithdrawalAmount();
                Double profit = saveTarget.getProfitLossAmount();

                boolean isFirst = isFirstSaveTarget && isFirstElement;

                saveTarget.setPrincipal(
                        strategyCalculator.getPrincipal(
                                isFirst,
                                deposit,
                                profit,
                                calculateStadard.getCurrentBalance()
                        ));
                saveTarget.setCurrentBalance(
                        strategyCalculator.getCurrentBalance(
                                isFirst,
                                calculateStadard.getCurrentBalance(),
                                deposit,
                                profit
                        )
                );
                saveTarget.setStandardAmount(
                        strategyCalculator.getStandardAmount(
                                isFirst,
                                deposit,
                                profit,
                                calculateStadard.getCurrentBalance(),
                                calculateStadard.getPrincipal()));
                saveTarget.setProfitLossRate(
                        strategyCalculator.getDailyProfitLossRate(
                                isFirst,
                                deposit,
                                profit,
                                calculateStadard.getCurrentBalance(),
                                calculateStadard.getPrincipal(),
                                calculateStadard.getStandardAmount()
                        ));
                saveTarget.setAccumulatedProfitLossAmount(
                        strategyCalculator.getAccumulatedProfitLossAmount(
                                saveTarget.getStrategy().getId(),
                                profit,
                                calculateStadard.getProfitLossAmount()
                        )
                );
                saveTarget.setAccumulatedProfitLossRate(
                        strategyCalculator.getAccumulatedProfitLossRate(
                                saveTarget.getStrategy().getId(),
                                saveTarget.getProfitLossRate()
                        )
                );
            }

            dailyRepository.saveAll(saveTargets);

        } catch (
                IOException e) {
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


    /**
     * 이전 날짜 중 가장 최신인 것을 기준으로 계산하기 위한 식
     * @param standardDate 기준 날짜
     * @param date1 기준 날짜와 비교할 날짜1
     * @param date2 기준 날짜와 비교할 날짜2
     * @return 날짜1이 더 가까우면 true
     */
    private boolean isClosestDateDate1(LocalDate standardDate, LocalDate date1, LocalDate date2) {

        long daysDifference1 = Math.abs(standardDate.toEpochDay() - date1.toEpochDay());
        long daysDifference2 = Math.abs(standardDate.toEpochDay() - date2.toEpochDay());

        return daysDifference1 <= daysDifference2;
    }
}
