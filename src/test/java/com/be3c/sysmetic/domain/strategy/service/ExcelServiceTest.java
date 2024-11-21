package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.domain.strategy.entity.Method;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.repository.MethodRepository;
import com.be3c.sysmetic.domain.strategy.repository.MonthlyRepository;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
public class ExcelServiceTest {

    @Autowired
    private ExcelService excelService;

    @Autowired
    private DailyRepository dailyRepository;

    @Autowired
    private MonthlyRepository monthlyRepository;

    @Autowired
    private StrategyRepository strategyRepository;

    @Autowired
    private MethodRepository methodRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DoubleHandler doubleHandler;

    @Autowired
    private StrategyCalculator strategyCalculator;

    @BeforeEach
    void setUp() {
        // Member 객체 초기화
        Member member = Member.builder()
                .roleCode("USER")
                .email("testuser@example.com")
                .password("password123")
                .name("Test User")
                .nickname("testuser")
                .phoneNumber("010-1234-5678")
                .usingStatusCode("ACTIVE")
                .totalFollow(100)
                .totalStrategyCount(5)
                .receiveInfoConsent("Y")
                .infoConsentDate(LocalDateTime.now())
                .receiveMarketingConsent("Y")
                .marketingConsentDate(LocalDateTime.now())
                .build();
        memberRepository.save(member);

        // Method 객체 초기화
        Method method = Method.builder()
                .name("Test Method")
                .statusCode("ACTIVE")
                .methodCreatedDate(LocalDateTime.now())
                .build();
        methodRepository.save(method);

        // Strategy 객체 초기화
        Strategy strategy = Strategy.builder()
                .trader(member)  // Member 객체 설정
                .method(method)   // Method 객체 설정
                .statusCode("ACTIVE")
                .name("Sample Strategy")
                .cycle('D')
                .content("This is a sample strategy content")
                .followerCount(0L)
                .kpRatio(0.0)
                .smScore(0.0)
                .strategyCreatedDate(LocalDateTime.now())
                .strategyModifiedDate(LocalDateTime.now())
                .build();
        strategyRepository.save(strategy);

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

        Strategy strategy = strategyRepository.findAll().get(0);
        Long strategyId = strategy.getId();   // 참조할 전략id

        // 4. Call the service method
        excelService.uploadExcel(file, strategyId);

        System.out.println("dailyRepository.findAll() = " + dailyRepository.findAll());

        List<Daily> result = dailyRepository.findAll();
        assertNotNull(result);
        assertEquals(2, result.size());  // 두 개의 데이터가 성공적으로 추가된 경우
    }

    @Test // 잘못된 파일 형식으로 테스트
    void testUploadExcel_invalidFileFormat() {
        Strategy strategy = strategyRepository.findAll().get(0);

        MockMultipartFile invalidFile = new MockMultipartFile("file", "invalid.txt", "text/plain", "Invalid file content".getBytes());

        Exception exception = assertThrows(InvalidFileFormatException.class, () -> {
            excelService.uploadExcel(invalidFile, strategy.getId());
        });

        assertEquals("엑셀 업로드 실패: 파일 형식이 올바르지 않습니다.", exception.getMessage());
    }

    @Test // 빈 파일을 업로드할 경우
    void testUploadExcel_emptyFile() {
        Strategy strategy = strategyRepository.findAll().get(0);

        MockMultipartFile emptyFile = new MockMultipartFile("file", "empty.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[0]);

        Exception exception = assertThrows(InvalidFileFormatException.class, () -> {
            excelService.uploadExcel(emptyFile,strategy.getId());
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
    public void testDownloadDailyExcelWithStatistics() throws Exception {
        Strategy strategy = strategyRepository.findAll().get(0);

        // 1. 테스트 데이터 준비
        Daily daily1 = Daily.builder()
                .strategy(strategy)
                .date(LocalDate.parse("2024-01-01"))
                .principal(1000.0)
                .currentBalance(1000.0)
                .standardAmount(1000.0)
                .depositWithdrawalAmount(500.0)
                .profitLossAmount(50.0)
                .profitLossRate(5.0)
                .accumulatedProfitLossAmount(50.0)
                .accumulatedProfitLossRate(5.0)
                .build();

        Daily daily2 = Daily.builder()
                .strategy(strategy)
                .date(LocalDate.parse("2024-01-02"))
                .principal(2000.0)
                .currentBalance(2000.0)
                .standardAmount(2000.0)
                .depositWithdrawalAmount(-300.0)
                .profitLossAmount(-30.0)
                .profitLossRate(-1.5)
                .accumulatedProfitLossAmount(20.0)
                .accumulatedProfitLossRate(1.0)
                .build();

        List<Daily> dailyList = Arrays.asList(daily1, daily2);
        dailyRepository.saveAll(dailyList);

        // 3. 메서드 실행
        InputStream inputStream = excelService.downloadDailyExcelWithStatistics(strategy.getId());

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
    }
}
