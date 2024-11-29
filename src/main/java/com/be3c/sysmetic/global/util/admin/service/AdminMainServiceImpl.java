package com.be3c.sysmetic.global.util.admin.service;

import com.be3c.sysmetic.domain.member.repository.InquiryRepository;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.domain.member.repository.NoticeRepository;
import com.be3c.sysmetic.domain.strategy.repository.StrategyApprovalRepository;
import com.be3c.sysmetic.domain.strategy.repository.StrategyRepository;
import com.be3c.sysmetic.global.util.admin.dto.*;
import com.google.analytics.data.v1beta.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AdminMainServiceImpl implements AdminMainService {

    @Value("${google.property.id}")
    private String propertyId;

    private final BetaAnalyticsDataClient analyticsDataClient; // 스프링 빈으로 주입됨

    private final MemberRepository memberRepository;

    private final StrategyRepository strategyRepository;

    private final StrategyApprovalRepository strategyApprovalRepository;

    private final InquiryRepository inquiryRepository;

    private final NoticeRepository noticeRepository;

    @Override
    public AdminMainResponseDto getAdminMain() {
        return AdminMainResponseDto.builder()
                .runReportResponseDto(getRunReportResponseDto())
                .strategyCount(getStrategyCount())
                .memberCount(getMemberRatio())
                .adminNoticeResponse(getAdminNoticeResponseDto())
                .adminInquiryResponseDto(getInquiryResponseDto())
                .build();
    }

    private RunReportResponseDto getRunReportResponseDto() {
        return RunReportResponseDto.builder()
                .allReportResponse(getReport(LocalDate.of(2000, 1, 1), LocalDate.now())) // 전체 기간
                .monthlyReportResponse(getReport(LocalDate.now().minusMonths(1), LocalDate.now())) // 최근 1개월
                .weeklyReportResponse(getReport(LocalDate.now().minusWeeks(1), LocalDate.now())) // 최근 1주
                .dayReportResponse(getReport(LocalDate.now().minusDays(1), LocalDate.now())) // 최근 1일
                .build();
    }

    private RunReportResponse getReport(LocalDate startDate, LocalDate endDate) {
        RunReportRequest request = RunReportRequest.newBuilder()
                .setProperty("properties/" + propertyId) // Analytics 속성 ID
                .addDateRanges(DateRange.newBuilder()
                        .setStartDate(startDate.toString())
                        .setEndDate(endDate.toString()))
                .addDimensions(Dimension.newBuilder().setName("country"))
                .addMetrics(Metric.newBuilder().setName("activeUsers"))
                .build();
        RunReportResponse runReportResponse = analyticsDataClient.runReport(request);

        runReportResponse.getRowsList().forEach(row -> {
            log.info("Country: {}", row.getDimensionValues(0).getValue());
            log.info("Active Users: {}", row.getMetricValues(0).getValue());
        });

        return analyticsDataClient.runReport(request); // 데이터 요청
    }

    private MemberCountResponseDto getMemberRatio() {
        Long userCount = memberRepository.countUser();
        Long traderCount = memberRepository.countTrader();
        Long managerCount = memberRepository.countManager();

        return MemberCountResponseDto.builder()
                .userMemberCount(userCount)
                .TraderMemberCount(traderCount)
                .ManagerMemberCount(managerCount)
                .totalMemberCount(userCount + traderCount)
                .build();
    }

    private AdminInquiryResponseDto getInquiryResponseDto() {
        Long inquiryCount = inquiryRepository.count();
        Long answeredInquiry = inquiryRepository.findAnsweredInquiry();

        return AdminInquiryResponseDto.builder()
                .inquiryCount(inquiryCount)
                .answeredInquiryCount(answeredInquiry)
                .waitingInquiryCount(inquiryCount - answeredInquiry)
                .build();
    }

    private List<AdminNoticeResponseDto> getAdminNoticeResponseDto() {
        Pageable pageable = PageRequest.of(0, 5);

        return noticeRepository.findAdminMainNotice(pageable).getContent();
    }

    private StrategyCountResponseDto getStrategyCount() {
        Long strategyCount = strategyRepository.count();
        Long openStrategyCount = strategyRepository.countOpenStatus();
        Long approvalOpenStrategyCount = strategyApprovalRepository.countWaitingStrategyCount();

        return StrategyCountResponseDto.builder()
                .totalStrategyCount(strategyCount)
                .openStrategyCount(openStrategyCount)
                .waitingStrategyCount(approvalOpenStrategyCount)
                .build();
    }
}