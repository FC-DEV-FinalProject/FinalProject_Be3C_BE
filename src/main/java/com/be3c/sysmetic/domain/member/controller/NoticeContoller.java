package com.be3c.sysmetic.domain.member.controller;

import com.be3c.sysmetic.domain.member.dto.*;
import com.be3c.sysmetic.domain.member.entity.Notice;
import com.be3c.sysmetic.domain.member.service.NoticeService;
import com.be3c.sysmetic.global.common.response.APIResponse;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import com.be3c.sysmetic.global.common.response.PageResponse;
import com.be3c.sysmetic.global.common.response.SuccessCode;
import com.be3c.sysmetic.global.util.SecurityUtils;
import com.be3c.sysmetic.global.util.file.dto.FileReferenceType;
import com.be3c.sysmetic.global.util.file.dto.FileRequest;
import com.be3c.sysmetic.global.util.file.dto.FileWithInfoResponse;
import com.be3c.sysmetic.global.util.file.service.FileService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1")
public class NoticeContoller implements NoticeControllerDocs {

    private final SecurityUtils securityUtils;

    private final NoticeService noticeService;
    private final FileService fileService;

    private final Integer pageSize = 10; // 한 페이지 크기

    /*
        관리자 공지사항 등록 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 공지사항이 등록에 성공했을 때 : OK
        3. 공지사항이 등록에 실패했을 때 : INTERNAL_SERVER_ERROR
        4. 데이터의 형식이 올바르지 않음 : BAD_REQUEST
        5. 등록하는 관리자 정보를 찾지 못했을 때 : NOT_FOUND
     */
    @Override
//    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PostMapping("/admin/notice/write")
    public ResponseEntity<APIResponse<Long>> saveAdminNotice(
            @RequestPart(value = "NoticeSaveRequestDto") NoticeSaveRequestDto noticeSaveRequestDto,
            @RequestPart(value = "fileList", required = false) List<MultipartFile> fileList,
            @RequestPart(value = "imageList", required = false) List<MultipartFile> imageList) {

//        Long userId = securityUtils.getUserIdInSecurityContext();

        if(fileList.size() > 3) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.fail(ErrorCode.BAD_REQUEST, "등록하려는 파일이 3개 초과입니다."));
        }

        if(imageList.size() > 5) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.fail(ErrorCode.BAD_REQUEST, "등록하려는 이미지가 3개 초과입니다."));
        }

        try {
            if (noticeService.registerNotice(
                    noticeSaveRequestDto.getUserId(),
//                    userId,
                    noticeSaveRequestDto.getNoticeTitle(),
                    noticeSaveRequestDto.getNoticeContent(),
//                    noticeSaveRequestDto.getIsAttachment(),
                    noticeSaveRequestDto.getIsOpen(),
                    fileList,
                    imageList)) {

                return ResponseEntity.status(HttpStatus.OK)
                        .body(APIResponse.success());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(APIResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.fail(ErrorCode.NOT_FOUND));
        }
    }


    /*
        관리자 공지사항 조회 / 검색 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 공지사항 데이터 조회에 성공했을 때 : OK
        3. 파라미터 데이터의 형식이 올바르지 않음 : BAD_REQUEST
     */
    @Override
//    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping("/admin/notice")
    public ResponseEntity<APIResponse<PageResponse<NoticeAdminListOneShowResponseDto>>> showAdminNotice(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "searchType", required = false, defaultValue = "title") String searchType,
            @RequestParam(value = "searchText", required = false) String searchText) {

        if (page <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.fail(ErrorCode.BAD_REQUEST, "페이지가 1 이하입니다."));
        }

        if (!(searchType.equals("title") || searchType.equals("content") || searchType.equals("all") || searchType.equals("writer"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.fail(ErrorCode.BAD_REQUEST, "쿼리 파라미터 searchType이 올바르지 않습니다."));
        }

        Page<Notice> noticeList = noticeService.findNoticeAdmin(searchType, searchText, page-1);

        List<NoticeAdminListOneShowResponseDto> noticeAdminDtoList = noticeList.stream()
                .map(NoticeContoller::noticeToNoticeAdminListOneShowResponseDto).collect(Collectors.toList());

        PageResponse<NoticeAdminListOneShowResponseDto> adminNoticePage = PageResponse.<NoticeAdminListOneShowResponseDto>builder()
                .currentPage(page)
                .pageSize(pageSize)
                .totalElement(noticeList.getTotalElements())
                .totalPages(noticeList.getTotalPages())
                .content(noticeAdminDtoList)
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(adminNoticePage));
    }

    public static NoticeAdminListOneShowResponseDto noticeToNoticeAdminListOneShowResponseDto(Notice notice) {

        String writerNickname = notice.getWriter().getNickname();
        if (writerNickname == null) {
            writerNickname = "탈퇴한 회원입니다.";
        }

        return NoticeAdminListOneShowResponseDto.builder()
                .noticeId(notice.getId())
                .noticeTitle(notice.getNoticeTitle())
                .writerNickname(writerNickname)
                .writeDate(notice.getWriteDate())
                .hits(notice.getHits())
                .isAttachment(notice.getIsAttachment())
                .isOpen(notice.getIsOpen())
                .build();
    }


    /*
        관리자 공지사항 목록 공개여부 수정 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 공개여부 수정에 성공했을 때 : OK
        3. 공개여부 수정에 실패했을 때 : INTERNAL_SERVER_ERROR
        4. 해당 공지사항을 찾지 못했을 때 : NOT_FOUND
     */
    @Override
//    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PutMapping("/admin/notice/{noticeId}/closed")
    public ResponseEntity<APIResponse<Long>> modifyNoticeClosed(
            @PathVariable Long noticeId) {

        try {

            if (noticeService.modifyNoticeClosed(noticeId)) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(APIResponse.success());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(APIResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR));
        }
        catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.fail(ErrorCode.NOT_FOUND));
        }
    }


    /*
        관리자 공지사항 상세 조회 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 공지사항의 상세 데이터 조회에 성공했을 때 : OK
        3. 해당 공지사항을 찾지 못했을 때 : NOT_FOUND
        4. 파라미터 데이터의 형식이 올바르지 않음 : BAD_REQUEST
     */
    @Override
//    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping("/admin/notice/{noticeId}/view")
    public ResponseEntity<APIResponse<NoticeDetailAdminShowResponseDto>> showAdminNoticeDetail(
            @PathVariable Long noticeId,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "searchType", required = false, defaultValue = "title") String searchType,
            @RequestParam(value = "searchText", required = false) String searchText) {

        if (page <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.fail(ErrorCode.BAD_REQUEST, "페이지가 1 이하입니다."));
        }

        if (!(searchType.equals("title") || searchType.equals("content") || searchType.equals("all") || searchType.equals("writer"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.fail(ErrorCode.BAD_REQUEST, "쿼리 파라미터 searchType이 올바르지 않습니다."));
        }

        try {
            noticeService.upHits(noticeId);

            Notice notice = noticeService.findNoticeById(noticeId);
            Notice previousNotice = noticeService.findNoticeById(noticeId-1);
            Notice nextNotice = noticeService.findNoticeById(noticeId+1);

            String writerNickname = notice.getWriter().getNickname();
            if (writerNickname == null) {
                writerNickname = "탈퇴한 회원입니다.";
            }

            List<FileWithInfoResponse> fileList = fileService.getFileWithInfos(new FileRequest(FileReferenceType.NOTICE_BOARD_FILE, notice.getId()));
            List<FileWithInfoResponse> imageList = fileService.getFileWithInfos(new FileRequest(FileReferenceType.NOTICE_BOARD_IMAGE, notice.getId()));

            List<NoticeDetailFileShowResponseDto> fileDtoList = new ArrayList<>();
            for (FileWithInfoResponse f : fileList) {
                NoticeDetailFileShowResponseDto noticeDetailFileShowResponseDto = NoticeDetailFileShowResponseDto.builder()
                        .fileId(f.id())
                        .fileSize(f.fileSize())
                        .originalName(f.originalName())
                        .path(f.url())
                        .build();
                fileDtoList.add(noticeDetailFileShowResponseDto);
            }

            List<NoticeDetailImageShowResponseDto> imageDtoList = new ArrayList<>();
            for (FileWithInfoResponse f : imageList) {
                NoticeDetailImageShowResponseDto noticeDetailImageShowResponseDto = NoticeDetailImageShowResponseDto.builder()
                        .fileId(f.id())
                        .path(f.url())
                        .build();
                imageDtoList.add(noticeDetailImageShowResponseDto);
            }

            NoticeDetailAdminShowResponseDto noticeDetailAdminShowResponseDto = NoticeDetailAdminShowResponseDto.builder()
                    .page(page)
                    .searchType(searchType)
                    .searchText(searchText)
                    .noticeId(notice.getId())
                    .noticeTitle(notice.getNoticeTitle())
                    .noticeContent(notice.getNoticeContent())
                    .writeDate(notice.getWriteDate())
                    .correctDate(notice.getCorrectDate())
                    .writerNickname(writerNickname)
                    .hits(notice.getHits())
                    .isAttachment(notice.getIsAttachment())
                    .isOpen(notice.getIsOpen())
                    .fileDtoList(fileDtoList)
                    .imageDtoList(imageDtoList)
                    .previousTitle(previousNotice.getNoticeTitle())
                    .previousWriteDate(previousNotice.getWriteDate())
                    .nextTitle(nextNotice.getNoticeTitle())
                    .nextWriteDate(nextNotice.getWriteDate())
                    .build();

            return ResponseEntity.status(HttpStatus.OK)
                    .body(APIResponse.success(noticeDetailAdminShowResponseDto));
        }
        catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.fail(ErrorCode.NOT_FOUND));
        }
    }


    /*
        관리자 공지사항 수정 화면 조회 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 공지사항 수정 화면 조회에 성공했을 때 : OK
        3. 공지사항 수정 화면 조회에 실패했을 때 : NOT_FOUND
        4. 파라미터 데이터의 형식이 올바르지 않음 : BAD_REQUEST
     */
    @Override
//    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping("/admin/notice/{noticeId}/modify")
    public ResponseEntity<APIResponse<NoticeShowModifyPageResponseDto>> showModifyAdminNotice(
            @PathVariable Long noticeId,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "searchType", required = false, defaultValue = "title") String searchType,
            @RequestParam(value = "searchText", required = false) String searchText) {

        if (page <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.fail(ErrorCode.BAD_REQUEST, "페이지가 1 이하입니다."));
        }

        if (!(searchType.equals("title") || searchType.equals("content") || searchType.equals("all") || searchType.equals("writer"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.fail(ErrorCode.BAD_REQUEST, "쿼리 파라미터 searchType이 올바르지 않습니다."));
        }

        try {
            Notice notice = noticeService.findNoticeById(noticeId);

            List<FileWithInfoResponse> fileList = fileService.getFileWithInfos(new FileRequest(FileReferenceType.NOTICE_BOARD_FILE, notice.getId()));
            List<FileWithInfoResponse> imageList = fileService.getFileWithInfos(new FileRequest(FileReferenceType.NOTICE_BOARD_IMAGE, notice.getId()));

            List<NoticeDetailFileShowResponseDto> fileDtoList = new ArrayList<>();
            for (FileWithInfoResponse f : fileList) {
                NoticeDetailFileShowResponseDto noticeDetailFileShowResponseDto = NoticeDetailFileShowResponseDto.builder()
                        .fileId(f.id())
                        .fileSize(f.fileSize())
                        .originalName(f.originalName())
                        .path(f.url())
                        .build();
                fileDtoList.add(noticeDetailFileShowResponseDto);
            }

            List<NoticeDetailImageShowResponseDto> imageDtoList = new ArrayList<>();
            for (FileWithInfoResponse f : imageList) {
                NoticeDetailImageShowResponseDto noticeDetailImageShowResponseDto = NoticeDetailImageShowResponseDto.builder()
                        .fileId(f.id())
                        .path(f.url())
                        .build();
                imageDtoList.add(noticeDetailImageShowResponseDto);
            }

            NoticeShowModifyPageResponseDto noticeShowModifyPageResponseDto = NoticeShowModifyPageResponseDto.builder()
                    .page(page)
                    .searchType(searchType)
                    .searchText(searchText)
                    .noticeId(notice.getId())
                    .noticeTitle(notice.getNoticeTitle())
                    .noticeContent(notice.getNoticeContent())
                    .isAttachment(notice.getIsAttachment())
                    .isOpen(notice.getIsOpen())
                    .fileDtoList(fileDtoList)
                    .imageDtoList(imageDtoList)
                    .build();

            return ResponseEntity.status(HttpStatus.OK)
                    .body(APIResponse.success(noticeShowModifyPageResponseDto));
        }
        catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.fail(ErrorCode.NOT_FOUND));
        }
    }


    /*
        관리자 공지사항 수정 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 공지사항 수정에 성공했을 때 : OK
        3. 공지사항 수정에 실패했을 때 : INTERNAL_SERVER_ERROR
        4. 해당 공지사항을 찾지 못했을 때 : NOT_FOUND
        5. 데이터의 형식이 올바르지 않음 : BAD_REQUEST
            +) 공지사항 수정 화면에 들어온 시간이 해당 공지사항 최종수정일시보다 작음
     */
    @Override
//    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PutMapping("/admin/notice/{noticeId}/modify")
    public ResponseEntity<APIResponse<Long>> modifyAdminNotice(
            @PathVariable Long noticeId,
            @RequestPart @Valid NoticeModifyRequestDto noticeModifyRequestDto,
            @RequestPart(value = "newFileList", required = false) List<MultipartFile> newFileList,
            @RequestPart(value = "newImageList", required = false) List<MultipartFile> newImageList) {

        LocalDateTime modifyInModifyPageTime = noticeModifyRequestDto.getModifyInModifyPageTime();
        if (modifyInModifyPageTime == null) {
            String str = "3000-11-05 13:47:13.248";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
            modifyInModifyPageTime = LocalDateTime.parse(str, formatter);
        }

        Long userId = securityUtils.getUserIdInSecurityContext();

        try {
            Notice notice = noticeService.findNoticeById(noticeId);

            if (modifyInModifyPageTime.isBefore(notice.getCorrectDate())) {

                if (noticeService.modifyNotice(
                        noticeId,
                        noticeModifyRequestDto.getNoticeTitle(),
                        noticeModifyRequestDto.getNoticeContent(),
                        userId,
                        noticeModifyRequestDto.getIsOpen(),
                        noticeModifyRequestDto.getExistFileDtoList(),
                        noticeModifyRequestDto.getExistImageDtoList(),
                        newFileList,
                        newImageList)) {
                    return ResponseEntity.status(HttpStatus.OK)
                            .body(APIResponse.success());
                }
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(APIResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(APIResponse.fail(ErrorCode.BAD_REQUEST, "공지사항 수정 화면에 들어온 시간이 해당 공지사항 최종수정일시보다 작습니다."));
            }
        }
        catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.fail(ErrorCode.NOT_FOUND));
        }
    }


    /*
        관리자 공지사항 삭제 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 공지사항 삭제에 성공했을 때 : OK
        3. 공지사항 삭제에 실패했을 때 : INTERNAL_SERVER_ERROR
        4. 해당 공지사항을 찾지 못했을 때 : NOT_FOUND
     */
    @Override
//    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @DeleteMapping("/admin/notice/{noticeId}/delete")
    public ResponseEntity<APIResponse<Long>> deleteAdminNotice(
            @PathVariable Long noticeId) {

        try {
            if (noticeService.deleteAdminNotice(noticeId)) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(APIResponse.success());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(APIResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR));
        }
        catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.fail(ErrorCode.NOT_FOUND));
        }
    }


    /*
        관리자 공지사항 목록 삭제 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 공지사항 목록 삭제에 성공했을 때 : OK
        3. 해당 공지사항을 찾지 못했을 때 : NOT_FOUND
        4. 공지사항 중 삭제에 실패했을 때 : MULTI_STATUS
     */
    @Override
//    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @DeleteMapping("/admin/notice/delete")
    public ResponseEntity<APIResponse<Map<Long, String>>> deleteAdminNoticeList(
            @RequestBody @Valid NoticeListDeleteRequestDto noticeListDeleteRequestDto) {

        List<Long> noticeIdList = noticeListDeleteRequestDto.getNoticeIds();

        try {
            Map<Long, String> deleteResult = noticeService.deleteAdminNoticeList(noticeIdList);

            if (deleteResult.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(APIResponse.success());
            }
            return ResponseEntity.status(HttpStatus.MULTI_STATUS)
                    .body(APIResponse.success(SuccessCode.OK, deleteResult));
        }
        catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.fail(ErrorCode.NOT_FOUND));
        }
    }


    /*
        공지사항 조회 / 검색 API
        1. 공지사항 데이터 조회에 성공했을 때 : OK
        2. 파라미터 데이터의 형식이 올바르지 않음 : BAD_REQUEST
     */
    @Override
    @GetMapping("/notice")
    public ResponseEntity<APIResponse<PageResponse<NoticeListOneShowResponseDto>>> showNotice(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "searchText", required = false) String searchText) {

        if (page <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.fail(ErrorCode.BAD_REQUEST, "페이지가 1 이하입니다."));
        }

        Page<Notice> noticeList = noticeService.findNotice(searchText, page-1);

        List<NoticeListOneShowResponseDto> noticeDtoList = noticeList.stream()
                .map(NoticeContoller::noticeToNoticeListOneShowResponseDto).collect(Collectors.toList());

        PageResponse<NoticeListOneShowResponseDto> adminNoticePage = PageResponse.<NoticeListOneShowResponseDto>builder()
                .currentPage(page)
                .pageSize(pageSize)
                .totalElement(noticeList.getTotalElements())
                .totalPages(noticeList.getTotalPages())
                .content(noticeDtoList)
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(adminNoticePage));
    }

    public static NoticeListOneShowResponseDto noticeToNoticeListOneShowResponseDto(Notice notice) {

        return NoticeListOneShowResponseDto.builder()
                .noticeId(notice.getId())
                .noticeTitle(notice.getNoticeTitle())
                .writeDate(notice.getWriteDate())
                .isAttachment(notice.getIsAttachment())
                .build();
    }


    /*
        공지사항 상세 조회 API
        1. 공지사항의 상세 데이터 조회에 성공했을 때 : OK
        2. 공지사항의 상세 데이터 조회에 실패했을 때 : NOT_FOUND
        3. 파라미터 데이터의 형식이 올바르지 않음 : BAD_REQUEST
     */
    @Override
    @GetMapping("/notice/{noticeId}/view")
    public ResponseEntity<APIResponse<NoticeDetailShowResponseDto>> showNoticeDetail(
            @PathVariable Long noticeId,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "searchText", required = false) String searchText) {

        if (page <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.fail(ErrorCode.BAD_REQUEST, "페이지가 1 이하입니다."));
        }

        try {
            Notice notice = noticeService.findNoticeById(noticeId);
            Notice previousNotice = noticeService.findNoticeById(noticeId-1);
            Notice nextNotice = noticeService.findNoticeById(noticeId+1);

            String writerNickname = notice.getWriter().getNickname();
            if (writerNickname == null) {
                writerNickname = "탈퇴한 회원입니다.";
            }

            List<FileWithInfoResponse> fileList = fileService.getFileWithInfos(new FileRequest(FileReferenceType.NOTICE_BOARD_FILE, notice.getId()));
            List<FileWithInfoResponse> imageList = fileService.getFileWithInfos(new FileRequest(FileReferenceType.NOTICE_BOARD_IMAGE, notice.getId()));

            List<NoticeDetailFileShowResponseDto> fileDtoList = new ArrayList<>();
            for (FileWithInfoResponse f : fileList) {
                NoticeDetailFileShowResponseDto noticeDetailFileShowResponseDto = NoticeDetailFileShowResponseDto.builder()
                        .fileId(f.id())
                        .fileSize(f.fileSize())
                        .originalName(f.originalName())
                        .path(f.url())
                        .build();
                fileDtoList.add(noticeDetailFileShowResponseDto);
            }

            List<NoticeDetailImageShowResponseDto> imageDtoList = new ArrayList<>();
            for (FileWithInfoResponse f : imageList) {
                NoticeDetailImageShowResponseDto noticeDetailImageShowResponseDto = NoticeDetailImageShowResponseDto.builder()
                        .fileId(f.id())
                        .path(f.url())
                        .build();
                imageDtoList.add(noticeDetailImageShowResponseDto);
            }

            NoticeDetailShowResponseDto noticeDetailShowResponseDto = NoticeDetailShowResponseDto.builder()
                    .page(page)
                    .searchText(searchText)
                    .noticeId(notice.getId())
                    .noticeTitle(notice.getNoticeTitle())
                    .noticeContent(notice.getNoticeContent())
                    .writeDate(notice.getWriteDate())
                    .correctDate(notice.getCorrectDate())
                    .writerNickname(writerNickname)
                    .hits(notice.getHits())
                    .isAttachment(notice.getIsAttachment())
                    .isOpen(notice.getIsOpen())
                    .fileDtoList(fileDtoList)
                    .imageDtoList(imageDtoList)
                    .previousTitle(previousNotice.getNoticeTitle())
                    .previousWriteDate(previousNotice.getWriteDate())
                    .nextTitle(nextNotice.getNoticeTitle())
                    .nextWriteDate(nextNotice.getWriteDate())
                    .build();

            return ResponseEntity.status(HttpStatus.OK)
                    .body(APIResponse.success(noticeDetailShowResponseDto));
        }
        catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.fail(ErrorCode.NOT_FOUND));
        }
    }
}