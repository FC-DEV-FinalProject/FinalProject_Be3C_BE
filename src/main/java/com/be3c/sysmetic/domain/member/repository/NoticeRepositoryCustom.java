package com.be3c.sysmetic.domain.member.repository;

import com.be3c.sysmetic.domain.member.entity.Notice;

import java.util.List;

public interface NoticeRepositoryCustom {

    List<Notice> findAdminNoticeSearch(String searchType, String searchText);
    Long findAdminNoticeSearchCount(String searchType, String searchText);

    List<Notice> findNoticeSearch(String searchText);
    Long findNoticeSearchCount(String searchText);
}
