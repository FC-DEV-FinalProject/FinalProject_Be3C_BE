package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.dto.NoticeAdminListShowRequestDto;
import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.entity.Notice;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.domain.member.repository.NoticeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeServiceImpl implements NoticeService {

    private final MemberRepository memberRepository;
    private final NoticeRepository noticeRepository;

    // 등록
    @Override
    @Transactional
    public Long registerNotice(Long writerId, String noticeTitle, String noticeContent, Integer isAttachment, Integer isOpen) {

        Member writer = memberRepository.findById(writerId).orElseThrow(EntityNotFoundException::new); // 등록할 전략을 찾지 못했을 때 : NOT_FOUND

        Notice notice = Notice.createNotice(noticeTitle, noticeContent, writer, isAttachment, isOpen);

        noticeRepository.save(notice);

        return notice.getId();
    }


    // 관리자 검색 조회
    // 검색 (사용: title, content, all, writer) (설명: 제목, 내용, 제목+내용, 작성자)
    @Override
    public Page<Notice> findNoticeAdmin(String searchType, String searchText, Integer page) {

        return noticeRepository.adminNoticeSearchWithBooleanBuilder(searchType, searchText, PageRequest.of(page, 10));
    }


    // 관리자 공지사항 목록 공개여부 수정
    @Override
    @Transactional
    public void modifyNoticeClosed(Long noticeId) {

        Notice notice = noticeRepository.findById(noticeId).orElseThrow(EntityNotFoundException::new); // 등록할 전략을 찾지 못했을 때 : NOT_FOUND

        if (notice.getIsOpen() == 0) {
            notice.setIsOpen(1);
        } else {
            notice.setIsOpen(0);
        }
    }


    // 공지사항 조회 후 조회수 상승
    @Override
    @Transactional
    public void upHits(Long noticeId) {

        Notice notice = noticeRepository.findById(noticeId).orElseThrow(EntityNotFoundException::new); // 등록할 전략을 찾지 못했을 때 : NOT_FOUND

        notice.setHits(notice.getHits() + 1);
    }


    // 문의 아이디로 문의 조회
    @Override
    public Notice findNoticeById(Long noticeId) {
        return noticeRepository.findById(noticeId).orElseThrow(EntityNotFoundException::new);
    }


    // 관리자 공지사항 수정
    @Override
    @Transactional
    public void modifyNotice(Long noticeId, String noticeTitle, String noticeContent, Long correctorId, Integer isAttatchment, Integer isOpen) {

        Notice notice = noticeRepository.findById(noticeId).orElseThrow(EntityNotFoundException::new);

        notice.setNoticeTitle(noticeTitle);
        notice.setNoticeContent(noticeContent);
        notice.setCorrectorId(correctorId);
        notice.setIsAttatchment(isAttatchment);
        notice.setIsOpen(isOpen);
    }


    // 관리자 문의 삭제
    @Override
    @Transactional
    public void deleteAdminNotice(Long noticeId) {

        Notice notice = noticeRepository.findById(noticeId).orElseThrow(EntityNotFoundException::new);

        noticeRepository.delete(notice);
    }


    // 관리자 공지사항 목록 삭제
    @Override
    @Transactional
    public Integer deleteAdminNoticeList(List<Long> noticeIdList) {

        return noticeRepository.bulkDelete(noticeIdList);
    }


    // 회원 검색 조회
    // 검색 (조건: 제목+내용)
    @Override
    public Page<Notice> findNotice(String searchText, Integer page) {

        return noticeRepository.noticeSearchWithBooleanBuilder(searchText, PageRequest.of(page, 10));
    }
}
