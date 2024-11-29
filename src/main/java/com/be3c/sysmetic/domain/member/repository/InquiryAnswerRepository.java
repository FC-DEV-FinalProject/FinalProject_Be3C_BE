package com.be3c.sysmetic.domain.member.repository;

import com.be3c.sysmetic.domain.member.entity.InquiryAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InquiryAnswerRepository extends JpaRepository<InquiryAnswer, Long> {

    List<InquiryAnswer> findByAnswerTitle(String inquiryAnswerTitle);

    Optional<InquiryAnswer> findByInquiryId(Long inquiryId);
}
