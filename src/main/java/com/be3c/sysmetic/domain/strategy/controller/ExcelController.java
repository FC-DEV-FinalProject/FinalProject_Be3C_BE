package com.be3c.sysmetic.domain.strategy.controller;

import com.be3c.sysmetic.global.common.response.APIResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

public interface ExcelController {

    @Operation(
            summary = "엑셀 파일 업로드",
            description = "전략 ID에 해당하는 전략의 엑셀 파일을 업로드합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "엑셀 파일 업로드 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = APIResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 (파일 형식 불일치 등)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = APIResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "전략을 찾을 수 없음",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = APIResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = APIResponse.class)
                            )
                    )
            }
    )
    @PostMapping("/daily/{strategyId}")
    ResponseEntity<APIResponse<String>> uploadExcel(
            @RequestParam("file") MultipartFile file,
            @PathVariable Long strategyId);

    @Operation(
            summary = "일별 통계 엑셀 다운로드",
            description = "전략 ID에 해당하는 일별 통계 엑셀 파일을 다운로드합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "엑셀 파일 다운로드 성공",
                            content = @Content(
                                    mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                                    schema = @Schema(implementation = InputStreamResource.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "전략을 찾을 수 없음",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = APIResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = APIResponse.class)
                            )
                    )
            }
    )
    @GetMapping("/daily/{strategyId}")
    ResponseEntity<APIResponse<InputStreamResource>> downloadDailyExcel(
            @PathVariable Long strategyId);

    @Operation(
            summary = "통계 포함 일별 엑셀 다운로드",
            description = "전략 ID에 해당하는 일별 통계 포함 엑셀 파일을 다운로드합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "엑셀 파일 다운로드 성공",
                            content = @Content(
                                    mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                                    schema = @Schema(implementation = InputStreamResource.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "전략을 찾을 수 없음",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = APIResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = APIResponse.class)
                            )
                    )
            }
    )
    @GetMapping("/daily/statistics/{strategyId}")
    ResponseEntity<APIResponse<InputStreamResource>> downloadDailyExcelWithStatistics(
            @PathVariable Long strategyId);

    @Operation(
            summary = "월별 엑셀 다운로드",
            description = "전략 ID에 해당하는 월별 엑셀 파일을 다운로드합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "엑셀 파일 다운로드 성공",
                            content = @Content(
                                    mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                                    schema = @Schema(implementation = InputStreamResource.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "전략을 찾을 수 없음",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = APIResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = APIResponse.class)
                            )
                    )
            }
    )
    @GetMapping("/monthly/{strategyId}")
    ResponseEntity<APIResponse<InputStreamResource>> downloadMonthlyExcel(
            @PathVariable Long strategyId);

}
