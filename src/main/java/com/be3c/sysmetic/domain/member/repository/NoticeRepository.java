package com.be3c.sysmetic.domain.member.repository;

import com.be3c.sysmetic.domain.member.entity.Notice;
import com.be3c.sysmetic.global.util.admin.dto.AdminNoticeResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> , NoticeRepositoryCustom {
    // NoticeRepositoryCustom에 QueryDSL로 검색 메소드

    // 목록에서 삭제
    @Modifying(clearAutomatically = true)
    @Query("delete Notice n where n.id in :idList")
    int bulkDelete(@Param("idList") List<Long> idList);

    // 제목으로 찾기
    List<Notice> findByNoticeTitle(String noticeTitle);

    @Query("""
        SELECT new com.be3c.sysmetic.global.util.admin.dto.AdminNoticeResponseDto(
            n.id, n.noticeTitle, n.writeDate
            )
        FROM
            Notice n
        ORDER BY
            n.writeDate DESC
    """)
    Page<AdminNoticeResponseDto> findAdminMainNotice(Pageable pageable);
}
