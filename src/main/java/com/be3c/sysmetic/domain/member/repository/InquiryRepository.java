package com.be3c.sysmetic.domain.member.repository;

import com.be3c.sysmetic.domain.member.entity.Inquiry;
import com.be3c.sysmetic.domain.member.entity.InquiryStatus;
import com.be3c.sysmetic.global.util.admin.dto.AdminInquiryResponseDto;
import com.be3c.sysmetic.global.util.admin.dto.AdminNoticeResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InquiryRepository extends JpaRepository<Inquiry, Long>, InquiryRepositoryCustom {

    List<Inquiry> findByInquiryTitle(String inquiryTitle);

    // 상태별 문의 조회
    Page<Inquiry> findByInquiryStatus(InquiryStatus inquiryStatus, Pageable pageable);

    // 일반회원별 문의 조회
    Page<Inquiry> findByInquirerId(Long inquirerId, Pageable pageable);

    // 일반회원별 상태별 문의 조회
    Page<Inquiry> findByInquirerIdAndInquiryStatus(Long inquirerId, InquiryStatus inquiryStatus, Pageable pageable);

    // 트레이더별 문의 조회
    @Query("select i from Inquiry i where i.strategy.trader.id = :traderId")
    Page<Inquiry> findByTraderId(@Param("traderId") Long traderId, Pageable pageable);

    // 트레이더별 상태별 문의 조회
    @Query("select i from Inquiry i where i.strategy.trader.id = :traderId and i.inquiryStatus = :inquiryStatus")
    Page<Inquiry> findByTraderIdAndInquiryStatus(@Param("traderId") Long traderId, @Param("inquiryStatus") InquiryStatus inquiryStatus, Pageable pageable);

    // 목록에서 삭제
    @Modifying(clearAutomatically = true)
    @Query("delete Inquiry i where i.id in :idList")
    int bulkDelete(@Param("idList") List<Long> idList);

    @Query("""
        SELECT 
            count(i)
        FROM Inquiry i
        JOIN InquiryAnswer a ON a.inquiry.id = i.id
    """)
    Long findAnsweredInquiry();
}
