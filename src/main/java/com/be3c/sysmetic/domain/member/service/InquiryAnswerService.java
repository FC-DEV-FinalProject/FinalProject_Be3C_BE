package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.entity.Inquiry;
import com.be3c.sysmetic.domain.member.entity.InquiryAnswer;
import com.be3c.sysmetic.domain.member.repository.InquiryAnswerRepository;
import com.be3c.sysmetic.domain.member.repository.InquiryRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InquiryAnswerService {

    private final EntityManager em;

    private final InquiryAnswerRepository inquiryAnswerRepository;
    private final InquiryRepository inquiryRepository;

    @Transactional
    public Long saveInquiryAnswer(InquiryAnswer inquiryAnswer) {
        inquiryAnswerRepository.save(inquiryAnswer);
        return inquiryAnswer.getId();
    }

    // 문의답변 단건 조회
    public InquiryAnswer findOneInquiryAnswer(Long inquiryAnswerId) {
        return inquiryAnswerRepository.findOne(inquiryAnswerId);
    }

    // 문의답변 전체 조회
    public List<InquiryAnswer> findAllInquiryAnswers() {
        return inquiryAnswerRepository.findAll();
    }

    // 문의별 문의답변 조회
    public List<InquiryAnswer> findThatInquiryAnswers(Long inquiryId) {
        return inquiryAnswerRepository.findByInquiryId(inquiryId);
    }

    //등록
    @Transactional
    public Long registerInquiryAnswer(Long inquiryId, String answerContent) {
        Inquiry inquiry = inquiryRepository.findOne(inquiryId);

        InquiryAnswer inquiryAnswer = InquiryAnswer.createInquiryAnswer(inquiry, answerContent);

        inquiryAnswerRepository.save(inquiryAnswer);

        return inquiryAnswer.getId();
    }

    //페이지네이션
}
