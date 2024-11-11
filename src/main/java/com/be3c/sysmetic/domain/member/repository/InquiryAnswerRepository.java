package com.be3c.sysmetic.domain.member.repository;

import com.be3c.sysmetic.domain.member.entity.InquiryAnswer;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class InquiryAnswerRepository {

    private final EntityManager em;

    public void save(InquiryAnswer inquiryAnswer) {
        em.persist(inquiryAnswer);
    }

    public InquiryAnswer findOne(Long id) {
        return em.find(InquiryAnswer.class, id);
    }

    public List<InquiryAnswer> findAll() {
        return em.createQuery("select i from InquiryAnswer i", InquiryAnswer.class)
                .getResultList();
    }

    public List<InquiryAnswer> findByInquiryId(Long inquiryId) {
        return em.createQuery("select i from InquiryAnswer i where i.inquiry.id = :inquiryId", InquiryAnswer.class)
                .setParameter("inquiryId", inquiryId)
                .getResultList();
    }
}
