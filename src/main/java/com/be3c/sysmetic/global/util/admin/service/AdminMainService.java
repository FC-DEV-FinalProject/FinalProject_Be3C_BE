package com.be3c.sysmetic.global.util.admin.service;

import com.be3c.sysmetic.global.util.admin.dto.AdminMainResponseDto;
import com.be3c.sysmetic.global.util.admin.dto.MemberCountResponseDto;
import com.be3c.sysmetic.global.util.admin.dto.RunReportResponseDto;
import com.google.analytics.data.v1beta.RunReportResponse;

import java.time.LocalDate;
import java.util.Map;

public interface AdminMainService {
    AdminMainResponseDto getAdminMain();
    RunReportResponseDto getAnalytics(String period);
}
