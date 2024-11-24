package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.entity.Inquiry;
import com.be3c.sysmetic.domain.member.entity.InquiryAnswer;
import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.repository.InquiryAnswerRepository;
import com.be3c.sysmetic.domain.strategy.entity.Method;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
//import org.junit.jupiter.api.Test;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class InquiryAnswerServiceTest {

    @PersistenceContext
    EntityManager em;

    @Autowired InquiryAnswerService inquiryAnswerService;
    @Autowired InquiryAnswerRepository inquiryAnswerRepository;

    @Test
    public void 문의_답변_등록() throws Exception {
        //given
        Inquiry inquiry = createInquiry("문의제목1", "문의내용1");

        InquiryAnswer inquiryAnswer = InquiryAnswer.createInquiryAnswer(inquiry, "답변제목1", "답변내용1");

        //when
        Long savedId = inquiryAnswerService.saveInquiryAnswer(inquiryAnswer);

        //then
        assertEquals(inquiryAnswer, inquiryAnswerRepository.findById(savedId).get());

    }

    @Test
    public void 전체_조회() throws Exception {
        //given
        Inquiry inquiry1 = createInquiry("문의제목1", "문의내용1");
        Inquiry inquiry2 = createInquiry("문의제목2", "문의내용2");

        InquiryAnswer inquiryAnswer1 = InquiryAnswer.createInquiryAnswer(inquiry1, "답변제목1", "답변내용1");
        inquiryAnswerService.saveInquiryAnswer(inquiryAnswer1);

        InquiryAnswer inquiryAnswer2 = InquiryAnswer.createInquiryAnswer(inquiry2, "답변제목2", "답변내용2");
        inquiryAnswerService.saveInquiryAnswer(inquiryAnswer2);

        //when
        List<InquiryAnswer> inquiryAnswerList = inquiryAnswerService.findAllInquiryAnswers();

        //then
        assertEquals(2, inquiryAnswerList.size());

    }

    @Test
    public void 문의별_조회() throws Exception {
        //given
        Inquiry inquiry1 = createInquiry("문의제목1", "문의내용1");
        Inquiry inquiry2 = createInquiry("문의제목2", "문의내용2");

        Long inquiryId1 = inquiry1.getId();
        Long inquiryId2 = inquiry2.getId();

        InquiryAnswer inquiryAnswer1 = InquiryAnswer.createInquiryAnswer(inquiry1, "답변제목1", "답변내용1");
        inquiryAnswerService.saveInquiryAnswer(inquiryAnswer1);

        InquiryAnswer inquiryAnswer2 = InquiryAnswer.createInquiryAnswer(inquiry2, "답변제목2", "답변내용2");
        inquiryAnswerService.saveInquiryAnswer(inquiryAnswer2);

        //when
        InquiryAnswer inquiryAnswerList1 = inquiryAnswerService.findThatInquiryAnswer(inquiryId1);
        InquiryAnswer inquiryAnswerList2 = inquiryAnswerService.findThatInquiryAnswer(inquiryId2);

        //then
        assertEquals("답변내용1", inquiryAnswerList1.getAnswerContent());
        assertEquals("답변내용2", inquiryAnswerList2.getAnswerContent());

    }

    @Test
    public void 답변_등록() throws Exception {
        //given
        Inquiry inquiry = createInquiry("문의제목1", "문의내용1");

        //when
        Long savedId = inquiryAnswerService.registerInquiryAnswer(inquiry.getId(), "답변제목1", "답변내용1");

        //then
        assertEquals("답변제목1", inquiryAnswerRepository.findById(savedId).get().getAnswerTitle());
        assertEquals("답변내용1", inquiryAnswerRepository.findById(savedId).get().getAnswerContent());

    }


    private Inquiry createInquiry(String inquiryTitle, String inquiryContent) {
        Inquiry inquiry = Inquiry.createInquiry(createStrategy(), createMember(), inquiryTitle, inquiryContent);
        em.persist(inquiry);
        return inquiry;
    }

    private Member createMember() {
        Member member = new Member();
//        member.setId(1L);
        member.setRoleCode("UR001");
        member.setEmail("user@gmail.com");
        member.setPassword("123456");
        member.setName("송중기");
        member.setNickname("유시진11");
        member.setBirth(LocalDateTime.now());
        member.setPhoneNumber("01012345678");
        member.setUsingStatusCode("MS002");
        member.setTotalFollow(39);
        member.setTotalStrategyCount(100);
        member.setReceiveInfoConsent("Y");
        member.setInfoConsentDate(LocalDateTime.now());
        member.setReceiveMarketingConsent("Y");
        member.setMarketingConsentDate(LocalDateTime.now());
        em.persist(member);
        return member;
    }

    private Method createMethod() {
        Method method = new Method();
        method.setName("Manual");
        method.setStatusCode("StatusCode");
//        method.setExplanation("Explanation");
//        method.setMethodCreatedDate(LocalDateTime.now());
        em.persist(method);
        return method;
    }

    private Strategy createStrategy() {
        Strategy strategy = new Strategy();
        strategy.setTrader(createMember());
        strategy.setMethod(createMethod());
        strategy.setStatusCode("ST002");
        strategy.setName("삼성전자");
        strategy.setCycle('D');
//        strategy.setMinOperationAmount(1.1);
        strategy.setContent("내용");
        strategy.setFollowerCount(36L);
        strategy.setKpRatio(2.2);
        strategy.setSmScore(3.3);
        strategy.setStrategyCreatedDate(LocalDateTime.now());
        strategy.setStrategyModifiedDate(LocalDateTime.now());
        em.persist(strategy);
        return strategy;
    }
}