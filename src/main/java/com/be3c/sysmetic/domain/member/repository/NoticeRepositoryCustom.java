package com.be3c.sysmetic.domain.member.repository;

import com.be3c.sysmetic.domain.member.entity.Notice;

import java.util.List;

public interface NoticeRepositoryCustom {

    List<Notice> adminNoticeSearchWithBooleanBuilder(String searchType, String searchText);
    Long adminNoticeCountWithBooleanBuilder(String searchType, String searchText);

    List<Notice> noticeSearchWithBooleanBuilder(String searchText);
    Long noticeCountWithBooleanBuilder(String searchText);
}
