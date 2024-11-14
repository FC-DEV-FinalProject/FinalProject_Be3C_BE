package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.repository.StrategyRepository;
import com.be3c.sysmetic.domain.strategy.util.StrategyCalculator;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.be3c.sysmetic.domain.strategy.entity.Daily;
import com.be3c.sysmetic.domain.strategy.repository.DailyRepository;
import com.be3c.sysmetic.domain.strategy.util.DoubleHandler;
import com.be3c.sysmetic.global.util.file.exception.InvalidFileFormatException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExcelServiceTest {

    private ExcelService excelService;

    @Mock
    private DailyRepository dailyRepository;

    @Mock
    private StrategyRepository strategyRepository;

    @Mock
    private DoubleHandler doubleHandler;

    @Mock
    private StrategyCalculator strategyCalculator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        excelService = new ExcelServiceImpl(dailyRepository, doubleHandler, strategyCalculator);
    }



    List<Row> createRow(int unit, Sheet sheet){

        List<Row> rows = new ArrayList<>(unit);

        for(int i = 0; i<unit; i++){
            Calendar calendar = Calendar.getInstance();
            calendar.set(2024, Calendar.NOVEMBER, 12);

            Row row = sheet.createRow(1);
            row.createCell(0).setCellValue(calendar);
            row.createCell(1).setCellValue(1000);
            row.createCell(2).setCellValue(200);

            rows.add(row);
        }

        return rows;
    }

    // startDate와 endDate 사이의 랜덤 날짜 생성
    private static Date generateRandomDate() {
        Random random = new Random();
        Calendar calendar = Calendar.getInstance();

        calendar.set(2024, Calendar.JANUARY, 1);
        long startDate = calendar.getTimeInMillis();

        calendar.set(2024, Calendar.OCTOBER, 31);
        long endDate = calendar.getTimeInMillis();

        long randomDateInMillis = startDate + (long) (random.nextDouble() * (endDate - startDate));

        return new Date(randomDateInMillis);
    }

    @Test // 정상 업로드 테스트
    void testUploadExcel_validFile() throws Exception {

        // 1. 엑셀 파일 생성
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Date");
        headerRow.createCell(1).setCellValue("DepositWithdrawalAmount");
        headerRow.createCell(2).setCellValue("ProfitLossAmount");

        Calendar calendar = Calendar.getInstance();
        calendar.set(2024, Calendar.NOVEMBER, 12);

        Row row1 = sheet.createRow(1);
        row1.createCell(0).setCellValue(calendar);
        row1.createCell(1).setCellValue(1000);
        row1.createCell(2).setCellValue(200);

        calendar.set(2024, Calendar.NOVEMBER, 13);

        Row row2 = sheet.createRow(2);
        row2.createCell(0).setCellValue(calendar);
        row2.createCell(1).setCellValue(2000);
        row2.createCell(2).setCellValue(400);

        // 엑셀 파일을 바이트 배열로 변환
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        byte[] content = baos.toByteArray();
        workbook.close();

        // MockMultipartFile로 변환
        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", content);

        // 2. Mock repository behavior
        Long strategyId = 1L;   // 참조할 전략id

        Daily oldEntity1 = Daily.builder()
                .id(1L)
                .strategy(Strategy.builder()
                        .id(strategyId).build())
                .date(LocalDate.of(2024, 11, 12))
                .depositWithdrawalAmount(1000.0)
                .profitLossAmount(200.0)
                .build();

        Daily oldEntity2 = Daily.builder()
                .id(2L)
                .strategy(Strategy.builder()
                        .id(strategyId).build())
                .date(LocalDate.of(2024, 11, 13))
                .depositWithdrawalAmount(2000.0)
                .profitLossAmount(400.0)
                .build();

        when(dailyRepository.findAll()).thenReturn(List.of(oldEntity1, oldEntity2));

        // 3. Mock doubleHandler behavior
//        when(doubleHandler.cutDouble(anyDouble())).thenAnswer(invocation -> invocation.getArgument(0));

        // 4. Call the service method
        excelService.uploadExcel(file, strategyId);

        System.out.println("dailyRepository.findAll() = " + dailyRepository.findAll());

        // 5. Verify results and interactions
        verify(dailyRepository, times(1)).saveAll(anyList());  // saveAll이 몇 번 호출되었는지 검증

        List<Daily> result = dailyRepository.findAll();
        assertNotNull(result);
        assertEquals(2, result.size());  // 두 개의 데이터가 성공적으로 추가된 경우
    }

    @Test // 잘못된 파일 형식으로 테스트
    void testUploadExcel_invalidFileFormat() {

        MockMultipartFile invalidFile = new MockMultipartFile("file", "invalid.txt", "text/plain", "Invalid file content".getBytes());

        Exception exception = assertThrows(InvalidFileFormatException.class, () -> {
            excelService.uploadExcel(invalidFile, 1L);
        });

        assertEquals("엑셀 업로드 실패: 파일 형식이 올바르지 않습니다.", exception.getMessage());
    }

    @Test // 빈 파일을 업로드할 경우
    void testUploadExcel_emptyFile() throws Exception {

        MockMultipartFile emptyFile = new MockMultipartFile("file", "empty.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[0]);

        Exception exception = assertThrows(InvalidFileFormatException.class, () -> {
            excelService.uploadExcel(emptyFile,1L);
        });

        assertEquals("엑셀 업로드 실패: 파일 내용이 올바르지 않습니다.", exception.getMessage());
    }



    /**
     *
     * 엑셀 업로드
     * ------------------------------------------------------------------------------------
     * 엑셀 다운로드
     *
     */


    @Test
    public void testDownloadExcelWithStatistics() throws Exception {
        // 1. 테스트 데이터 준비
        Daily daily1 = new Daily();
        daily1.setStrategy(Strategy.builder().id(1L).build());
        daily1.setDate(LocalDate.parse("2024-01-01"));
        daily1.setPrincipal(1000.0);
        daily1.setDepositWithdrawalAmount(500.0);
        daily1.setProfitLossAmount(50.0);
        daily1.setProfitLossRate(5.0);
        daily1.setAccumulatedProfitLossAmount(50.0);
        daily1.setAccumulatedProfitLossRate(5.0);

        Daily daily2 = new Daily();
        daily2.setStrategy(Strategy.builder().id(1L).build());
        daily2.setDate(LocalDate.parse("2024-01-02"));
        daily2.setPrincipal(2000.0);
        daily2.setDepositWithdrawalAmount((double) -300);
        daily2.setProfitLossAmount((double) -30);
        daily2.setProfitLossRate(-1.5);
        daily2.setAccumulatedProfitLossAmount(20.0);
        daily2.setAccumulatedProfitLossRate(1.0);

        List<Daily> mockDailyList = Arrays.asList(daily1, daily2);

        // 2. mock 객체 설정 (findAll 메서드가 호출되면 mock 데이터를 반환)
        when(dailyRepository.findAll()).thenReturn(mockDailyList);

        // 3. 메서드 실행
        InputStream inputStream = excelService.downloadExcelWithStatistics(1L);

        // 4. 엑셀 파일 검증
        assertNotNull(inputStream, "엑셀 파일이 null이어서는 안 됩니다.");

        // 엑셀 파일을 Workbook 객체로 읽어들임
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            assertNotNull(sheet, "엑셀 시트가 존재해야 합니다.");

            // 첫 번째 행 (헤더 행) 확인
            Row headerRow = sheet.getRow(0);
            assertEquals("날짜", headerRow.getCell(0).getStringCellValue());
            assertEquals("원금", headerRow.getCell(1).getStringCellValue());
            assertEquals("입출금", headerRow.getCell(2).getStringCellValue());
            assertEquals("일 손익", headerRow.getCell(3).getStringCellValue());
            assertEquals("일 손익률", headerRow.getCell(4).getStringCellValue());
            assertEquals("누적 손익", headerRow.getCell(5).getStringCellValue());
            assertEquals("누적 손익률", headerRow.getCell(6).getStringCellValue());

            // 두 번째 행 (데이터 행) 확인
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Row dataRow1 = sheet.getRow(1);
            assertEquals(sdf.parse("2024-01-01"), dataRow1.getCell(0).getDateCellValue());
            assertEquals(1000, dataRow1.getCell(1).getNumericCellValue(), 0.0);
            assertEquals(500, dataRow1.getCell(2).getNumericCellValue(), 0.0);
            assertEquals(50, dataRow1.getCell(3).getNumericCellValue(), 0.0);
            assertEquals(5.0, dataRow1.getCell(4).getNumericCellValue(), 0.0);
            assertEquals(50, dataRow1.getCell(5).getNumericCellValue(), 0.0);
            assertEquals(5.0, dataRow1.getCell(6).getNumericCellValue(), 0.0);

            Row dataRow2 = sheet.getRow(2);
            assertEquals(sdf.parse("2024-01-02"), dataRow2.getCell(0).getDateCellValue());
            assertEquals(2000, dataRow2.getCell(1).getNumericCellValue(), 0.0);
            assertEquals(-300, dataRow2.getCell(2).getNumericCellValue(), 0.0);
            assertEquals(-30, dataRow2.getCell(3).getNumericCellValue(), 0.0);
            assertEquals(-1.5, dataRow2.getCell(4).getNumericCellValue(), 0.0);
            assertEquals(20, dataRow2.getCell(5).getNumericCellValue(), 0.0);
            assertEquals(1.0, dataRow2.getCell(6).getNumericCellValue(), 0.0);
        }

        // 5. verify 메서드를 통해 dailyRepository.findAll()이 한 번만 호출되었는지 확인
        verify(dailyRepository, times(1)).findAll();
    }
}
