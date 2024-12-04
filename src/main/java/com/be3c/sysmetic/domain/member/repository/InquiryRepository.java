package com.be3c.sysmetic.domain.member.repository;

import com.be3c.sysmetic.domain.member.entity.Inquiry;
import com.be3c.sysmetic.domain.member.entity.InquiryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

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

    // 트레이더별 문의id로 조회
    @Query("select i from Inquiry i where i.id = :inquiryId and i.traderId = :traderId order by i.id desc")
    List<Inquiry> findInquiryByTraderIdAndInquiryId(@Param("inquiryId") Long inquiryId, @Param("traderId") Long traderId, Pageable pageable);

    // 질문자별 문의id로 조회
    @Query("select i from Inquiry i where i.id = :inquiryId and i.inquirer.id = :inquirerId order by i.id desc")
    List<Inquiry> findInquiryByInquirerIdAndInquiryId(@Param("inquiryId") Long inquiryId, @Param("inquirerId") Long inquirerId, Pageable pageable);

    // 관리자 이전 문의 조회
    @Query("select i from Inquiry i where i.id < :inquiryId order by i.id desc")
    List<Inquiry> adminFindPreviousInquiry(@Param("inquiryId") Long inquiryId, Pageable pageable);

    // 관리자 다음 문의 조회
    @Query("select i from Inquiry i where i.id > :inquiryId order by i.id asc")
    List<Inquiry> adminFindNextInquiry(@Param("inquiryId") Long inquiryId, Pageable pageable);

    // 트레이더 이전 문의 조회
    @Query("select i from Inquiry i where i.id < :inquiryId and i.traderId = :traderId order by i.id desc")
    List<Inquiry> traderFindPreviousInquiry(@Param("inquiryId") Long inquiryId, @Param("traderId") Long traderId, Pageable pageable);

    // 트레이더 다음 문의 조회
    @Query("select i from Inquiry i where i.id > :inquiryId and i.traderId = :traderId order by i.id asc")
    List<Inquiry> traderFindNextInquiry(@Param("inquiryId") Long inquiryId, @Param("traderId") Long traderId, Pageable pageable);

    // 질문자 이전 문의 조회
    @Query("select i from Inquiry i where i.id < :inquiryId and i.inquirer.id = :inquirerId order by i.id desc")
    List<Inquiry> inquirerFindPreviousInquiry(@Param("inquiryId") Long inquiryId, @Param("inquirerId") Long inquirerId, Pageable pageable);

    // 질문자 다음 문의 조회
    @Query("select i from Inquiry i where i.id > :inquiryId and i.inquirer.id = :inquirerId order by i.id asc")
    List<Inquiry> inquirerFindNextInquiry(@Param("inquiryId") Long inquiryId, @Param("inquirerId") Long inquirerId, Pageable pageable);

    // 이전 문의 조회
    @Query("select i from Inquiry i where i.id < :inquiryId order by i.id desc")
    List<Inquiry> findPreviousInquiry(@Param("inquiryId") Long inquiryId, Pageable pageable);

    // 다음 문의 조회
    @Query("select i from Inquiry i where i.id > :inquiryId order by i.id asc")
    List<Inquiry> findNextInquiry(@Param("inquiryId") Long inquiryId, Pageable pageable);

    @Modifying
    @Query("DELETE FROM Inquiry i WHERE i.strategy.id = :strategyId")
    void deleteByStrategyId(@Param("strategyId") Long strategyId);

    @Query("SELECT count(*) FROM Inquiry i JOIN InquiryAnswer a ON a.id = i.inquiryAnswer.id")
    Long countAnsweredInquiry();
}
