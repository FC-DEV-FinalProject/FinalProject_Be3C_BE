package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.dto.NoticeExistFileImageRequestDto;
import com.be3c.sysmetic.domain.member.entity.Inquiry;
import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.entity.Notice;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.domain.member.repository.NoticeRepository;
import com.be3c.sysmetic.global.util.file.dto.FileReferenceType;
import com.be3c.sysmetic.global.util.file.dto.FileRequest;
import com.be3c.sysmetic.global.util.file.service.FileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.be3c.sysmetic.domain.member.message.NoticeDeleteFailMessage.NOT_FOUND_NOTICE;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeServiceImpl implements NoticeService {

    private final MemberRepository memberRepository;
    private final NoticeRepository noticeRepository;
    private final FileService fileService;

    // 등록
    @Override
    @Transactional
    public boolean registerNotice(Long writerId, String noticeTitle, String noticeContent,
                                  Boolean fileExists, Boolean imageExists, Boolean isOpen,
                                    List<MultipartFile> fileList, List<MultipartFile> imageList) {

        Member writer = memberRepository.findById(writerId).orElseThrow(() -> new EntityNotFoundException("회원이 없습니다."));

        Notice notice = Notice.createNotice(noticeTitle, noticeContent, writer, fileExists, imageExists, isOpen);

        noticeRepository.save(notice);

        if (fileList.isEmpty() == fileExists) {
            throw new IllegalArgumentException("파일 리스트와 fileExist 값이 맞지 않습니다.");
        }

        if (imageList.isEmpty() == imageExists) {
            throw new IllegalArgumentException("이미지 리스트와 imageExist 값이 맞지 않습니다.");
        }

        if(fileExists) {
            for (MultipartFile file : fileList) {
                fileService.uploadAnyFile(file, new FileRequest(FileReferenceType.NOTICE_BOARD_FILE, notice.getId()));
            }
        }

        if(imageExists) {
            for (MultipartFile image : imageList) {
                fileService.uploadImage(image, new FileRequest(FileReferenceType.NOTICE_BOARD_IMAGE, notice.getId()));
            }
        }

        return true;
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
    public boolean modifyNoticeClosed(Long noticeId) {

        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new EntityNotFoundException("공지사항이 없습니다."));

        if (!notice.getIsOpen()) {
            notice.setIsOpen(true);
        } else {
            notice.setIsOpen(false);
        }

        return true;
    }


    // 공지사항 조회 후 조회수 상승
    @Override
    @Transactional
    public void upHits(Long noticeId) {

        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new EntityNotFoundException("공지사항이 없습니다."));

        notice.setHits(notice.getHits() + 1);
    }


    // 문의 아이디로 문의 조회
    @Override
    public Notice findNoticeById(Long noticeId) {
        return noticeRepository.findById(noticeId).orElseThrow(() -> new EntityNotFoundException("공지사항이 없습니다."));
    }


    // 관리자 공지사항 수정
    @Override
    @Transactional
    public boolean modifyNotice(Long noticeId, String noticeTitle, String noticeContent, Long correctorId,
                                Boolean fileExists, Boolean imageExists, Boolean isOpen,
                                List<NoticeExistFileImageRequestDto> existFileDtoList, List<NoticeExistFileImageRequestDto> existImageDtoList,
                                List<MultipartFile> newFileList, List<MultipartFile> newImageList) {

        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new EntityNotFoundException("공지사항이 없습니다."));

        notice.setNoticeTitle(noticeTitle);
        notice.setNoticeContent(noticeContent);
        notice.setCorrectorId(correctorId);
        notice.setCorrectDate(LocalDateTime.now());
        notice.setFileExists(fileExists);
        notice.setImageExists(imageExists);
        notice.setIsOpen(isOpen);

        int countFile = 0;
        for (NoticeExistFileImageRequestDto n : existFileDtoList) {
            if (!n.getExist()) {
                fileService.deleteFileById(n.getFileId());
            } else {
                countFile++;
            }
        }
        int newFileListSize;
        if (newFileList == null) {
            newFileListSize = 0;
        } else {
            newFileListSize = newFileList.size();
        }
        countFile = countFile + newFileListSize;
        if (countFile > 3) {
            throw new IllegalArgumentException("파일이 3개 이상입니다.");
        }
        if ((countFile == 0) == fileExists) {
            throw new IllegalArgumentException("이미지 리스트와 fileExists 값이 맞지 않습니다.");
        }

        int countImage = 0;
        for (NoticeExistFileImageRequestDto n : existImageDtoList) {
            if (!n.getExist()) {
                fileService.deleteFileById(n.getFileId());
            } else {
                countImage++;
            }
        }
        int newImageListSize;
        if (newImageList == null) {
            newImageListSize = 0;
        } else {
            newImageListSize = newImageList.size();
        }
        countImage = countImage + newImageListSize;
        if (countImage > 5) {
            throw new IllegalArgumentException("이미지가 5개 이상입니다.");
        }
        if ((countImage == 0) == imageExists) {
            throw new IllegalArgumentException("이미지 리스트와 imageExists 값이 맞지 않습니다.");
        }


        if(!(newFileList == null || newFileList.isEmpty())) {
            for (MultipartFile file : newFileList) {
                fileService.uploadAnyFile(file, new FileRequest(FileReferenceType.NOTICE_BOARD_FILE, notice.getId()));
            }
        }

        if(!(newImageList == null || newImageList.isEmpty())) {
            for (MultipartFile image : newImageList) {
                fileService.uploadImage(image, new FileRequest(FileReferenceType.NOTICE_BOARD_IMAGE, notice.getId()));
            }
        }

        return true;
    }


    // 관리자 문의 삭제
    @Override
    @Transactional
    public boolean deleteAdminNotice(Long noticeId) {

        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new EntityNotFoundException("공지사항이 없습니다."));

        noticeRepository.delete(notice);

        return true;
    }


    // 관리자 공지사항 목록 삭제
    @Override
    @Transactional
    public Map<Long, String> deleteAdminNoticeList(List<Long> noticeIdList) {

        if (noticeIdList == null || noticeIdList.isEmpty()) {
            throw new IllegalArgumentException("공지가 한 개도 선택되지 않았습니다.");
        }

        Map<Long, String> failDelete = new HashMap<>();

        for (Long noticeId : noticeIdList) {
            try {
                noticeRepository.findById(noticeId);
            }
            catch (EntityNotFoundException e) {
                failDelete.put(noticeId, NOT_FOUND_NOTICE.getMessage());
            }
        }

        noticeRepository.bulkDelete(noticeIdList);

        return failDelete;
    }


    // 일반 검색 조회
    // 검색 (조건: 제목+내용)
    @Override
    public Page<Notice> findNotice(String searchText, Integer page) {

        return noticeRepository.noticeSearchWithBooleanBuilder(searchText, PageRequest.of(page, 10));
    }


    // 이전글 제목 조회
    @Override
    public String findPreviousNoticeTitle(Long noticeId) {
        List<Notice> previousNoticeList = noticeRepository.findPreviousNotice(noticeId, PageRequest.of(0, 1));

        String previousNoticeTitle;
        if (previousNoticeList.isEmpty()) {
            previousNoticeTitle = null;
        } else {
            Notice previousNotice = previousNoticeList.get(0);
            previousNoticeTitle = previousNotice.getNoticeTitle();
        }

        return previousNoticeTitle;
    }

    // 이전글 작성일 조회
    @Override
    public LocalDateTime findPreviousNoticeWriteDate(Long noticeId) {
        List<Notice> previousNoticeList = noticeRepository.findPreviousNotice(noticeId, PageRequest.of(0, 1));

        LocalDateTime previousNoticeWriteDate;
        if (previousNoticeList.isEmpty()) {
            previousNoticeWriteDate = null;
        } else {
            Notice previousNotice = previousNoticeList.get(0);
            previousNoticeWriteDate = previousNotice.getWriteDate();
        }

        return previousNoticeWriteDate;
    }

    // 다음글 제목 조회
    @Override
    public String findNextNoticeTitle(Long noticeId) {
        List<Notice> nextNoticeList = noticeRepository.findNextNotice(noticeId, PageRequest.of(0, 1));

        String nextNoticeTitle;
        if (nextNoticeList.isEmpty()) {
            nextNoticeTitle = null;
        } else {
            Notice previousNotice = nextNoticeList.get(0);
            nextNoticeTitle = previousNotice.getNoticeTitle();
        }

        return nextNoticeTitle;
    }

    // 다음글 제목 조회
    @Override
    public LocalDateTime findNextNoticeWriteDate(Long noticeId) {
        List<Notice> nextNoticeList = noticeRepository.findNextNotice(noticeId, PageRequest.of(0, 1));

        LocalDateTime nextNoticeWriteDate;
        if (nextNoticeList.isEmpty()) {
            nextNoticeWriteDate = null;
        } else {
            Notice previousNotice = nextNoticeList.get(0);
            nextNoticeWriteDate = previousNotice.getWriteDate();
        }

        return nextNoticeWriteDate;
    }
}
