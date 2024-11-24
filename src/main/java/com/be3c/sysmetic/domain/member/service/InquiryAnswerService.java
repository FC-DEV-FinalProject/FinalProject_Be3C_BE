package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.entity.Inquiry;
import com.be3c.sysmetic.domain.member.entity.InquiryAnswer;
import com.be3c.sysmetic.domain.member.entity.InquiryStatus;
import com.be3c.sysmetic.domain.member.repository.InquiryAnswerRepository;
import com.be3c.sysmetic.domain.member.repository.InquiryRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
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
        return inquiryAnswerRepository.findById(inquiryAnswerId).orElseThrow(EntityNotFoundException::new); // 등록할 전략을 찾지 못했을 때 : NOT_FOUND
    }

    // 문의답변 전체 조회
    public List<InquiryAnswer> findAllInquiryAnswers() {
        return inquiryAnswerRepository.findAll();
    }

    // 문의별 문의답변 조회
    public InquiryAnswer findThatInquiryAnswer(Long inquiryId) {
        return inquiryAnswerRepository.findByInquiryId(inquiryId).orElseThrow(EntityNotFoundException::new); // 등록할 전략을 찾지 못했을 때 : NOT_FOUND
    }

    //등록
    @Transactional
    public Long registerInquiryAnswer(Long inquiryId, String answerTitle, String answerContent) {

        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(EntityNotFoundException::new); // 등록할 전략을 찾지 못했을 때 : NOT_FOUND

        InquiryAnswer inquiryAnswer = InquiryAnswer.createInquiryAnswer(inquiry, answerTitle, answerContent);
        inquiryAnswerRepository.save(inquiryAnswer);

        inquiry.setInquiryStatus(InquiryStatus.closed);
        inquiryRepository.save(inquiry);

        return inquiryAnswer.getId();
    }
}
