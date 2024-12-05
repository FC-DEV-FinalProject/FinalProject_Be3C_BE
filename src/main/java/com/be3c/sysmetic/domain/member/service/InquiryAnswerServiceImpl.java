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
public class InquiryAnswerServiceImpl implements InquiryAnswerService {

    private final InquiryAnswerRepository inquiryAnswerRepository;
    private final InquiryRepository inquiryRepository;

    // 문의별 문의답변 조회
    @Override
    public InquiryAnswer findThatInquiryAnswer(Long inquiryId) {
        return inquiryAnswerRepository.findByInquiryId(inquiryId).orElseThrow(() -> new EntityNotFoundException("문의 답변이 없습니다."));
    }

    //등록
    @Override
    @Transactional
    public boolean registerInquiryAnswer(Long inquiryId, String answerTitle, String answerContent) {

        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(() -> new EntityNotFoundException("문의가 없습니다."));

        InquiryAnswer inquiryAnswer = InquiryAnswer.createInquiryAnswer(inquiry, answerTitle, answerContent);
        inquiryAnswerRepository.save(inquiryAnswer);

        inquiry.setInquiryStatus(InquiryStatus.closed);
        inquiryRepository.save(inquiry);

        return true;
    }
}
