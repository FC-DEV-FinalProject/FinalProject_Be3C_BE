package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.entity.InquiryAnswer;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface InquiryAnswerService {

    // 문의별 문의답변 조회
    InquiryAnswer findThatInquiryAnswer(Long inquiryId);

    //등록
    @Transactional
    boolean registerInquiryAnswer(Long inquiryId, String answerTitle, String answerContent);
}
