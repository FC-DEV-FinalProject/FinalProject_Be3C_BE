package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.dto.NoticeAdminListShowRequestDto;
import com.be3c.sysmetic.domain.member.entity.Notice;
import org.springframework.data.domain.Page;

import java.util.List;

public interface NoticeService {

    // 등록
    Long registerNotice(Long writerId, String noticeTitle, String noticeContent, Integer isAttachment, Integer isOpen);

    // 관리자 검색 조회
    // 검색 (사용: title, content, all, writer) (설명: 제목, 내용, 제목+내용, 작성자)
    Page<Notice> findNoticeAdmin(String searchType, String searchText, Integer page);

    // 관리자 공지사항 목록 공개여부 수정
    void modifyNoticeClosed(Long noticeId);

    // 공지사항 조회 후 조회수 상승
    void upHits(Long noticeId);

    // 문의 아이디로 문의 조회
    Notice findNoticeById(Long noticeId);

    // 관리자 문의 수정
    void modifyNotice(Long noticeId, String noticeTitle, String noticeContent, Long correctorId, Integer isAttatchment, Integer isOpen);

    // 관리자 문의 삭제
    void deleteAdminNotice(Long noticeId);

    // 관리자 문의 목록 삭제
    Integer deleteAdminNoticeList(List<Long> noticeIdList);

    // 회원 검색 조회
    // 검색 (조건: 제목+내용)
    Page<Notice> findNotice(String searchText, Integer page);
}
