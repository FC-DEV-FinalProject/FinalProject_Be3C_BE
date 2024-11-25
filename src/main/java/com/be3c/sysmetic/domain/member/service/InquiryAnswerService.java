package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.entity.InquiryAnswer;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface InquiryAnswerService {

    Long saveInquiryAnswer(InquiryAnswer inquiryAnswer);

    // 문의답변 단건 조회
    InquiryAnswer findOneInquiryAnswer(Long inquiryAnswerId);

    // 문의답변 전체 조회
    List<InquiryAnswer> findAllInquiryAnswers();

    // 문의별 문의답변 조회
    InquiryAnswer findThatInquiryAnswer(Long inquiryId);

    //등록
    @Transactional
    Long registerInquiryAnswer(Long inquiryId, String answerTitle, String answerContent);
}
