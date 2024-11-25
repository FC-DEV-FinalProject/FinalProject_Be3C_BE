package com.be3c.sysmetic.domain.member.repository;

import com.be3c.sysmetic.domain.member.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoticeRepositoryCustom {

    Page<Notice> adminNoticeSearchWithBooleanBuilder(String searchType, String searchText, Pageable pageable);

    Page<Notice> noticeSearchWithBooleanBuilder(String searchText, Pageable pageable);
}
