package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.entity.Inquiry;
import com.be3c.sysmetic.domain.member.entity.InquiryStatus;
import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.repository.InquiryRepository;
import com.be3c.sysmetic.domain.member.repository.InquirySearch;
import com.be3c.sysmetic.domain.strategy.entity.Method;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.Test;
//import org.junit.jupiter.api.Test;
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
public class InquiryServiceTest {

    @PersistenceContext
    EntityManager em;

    @Autowired InquiryService inquiryService;
    @Autowired InquiryRepository inquiryRepository;

//    @Test
//    public void 문의_등록() throws Exception {
//        //given
//        Strategy strategy = createStrategy();
//        Member member = createMember();
//
//        Inquiry inquiry = Inquiry.createInquiry(strategy, member, "질문1", "내용1");
//
//        //when
//        Long savedId = inquiryService.saveInquiry(inquiry);
//
//        //then
//        assertEquals(inquiry, inquiryRepository.findOne(savedId));
//
//    }

    @Test
    public void 전체_조회() throws Exception {
        //given
        Strategy strategy = createStrategy();
        Member member = createMember();

        Inquiry inquiry1 = Inquiry.createInquiry(strategy, member, "질문1", "내용1");
        inquiryService.saveInquiry(inquiry1);

        Inquiry inquiry2 = Inquiry.createInquiry(strategy, member, "질문2", "내용2");
        inquiryService.saveInquiry(inquiry2);

        //when
        List<Inquiry> inquiryList = inquiryService.findAllInquiries();

        //then
        assertEquals(2, inquiryList.size());

    }

    @Test
    public void 회원별_조회() throws Exception {
        //given
        Strategy strategy = createStrategy();
        Member member = createMember();

        Long memberId = member.getId();

        Inquiry inquiry1 = Inquiry.createInquiry(strategy, member, "질문1", "내용1");
        inquiryService.saveInquiry(inquiry1);

        Inquiry inquiry2 = Inquiry.createInquiry(strategy, member, "질문2", "내용2");
        inquiryService.saveInquiry(inquiry2);

        //when
        List<Inquiry> inquiryList = inquiryService.findMemberInquiries(memberId);

        //then
        assertEquals(2, inquiryList.size());

    }

    @Test
    public void 상태별_조회() throws Exception {
        //given
        Strategy strategy = createStrategy();
        Member member1 = createMember();
        Member member2 = createMember();

        Inquiry inquiry1 = Inquiry.createInquiry(strategy, member1, "질문1", "내용1");
        inquiryService.saveInquiry(inquiry1);

        Inquiry inquiry2 = Inquiry.createInquiry(strategy, member2, "질문2", "내용2");
        inquiryService.saveInquiry(inquiry2);

        Inquiry inquiry3 = Inquiry.createInquiry(strategy, member1, "질문3", "내용3");
        inquiryService.saveInquiry(inquiry3);

        //when
        List<Inquiry> inquiryListIncomplete = inquiryService.findStatusInquires(InquiryStatus.INCOMPLETE);
        List<Inquiry> inquiryListComplete = inquiryService.findStatusInquires(InquiryStatus.COMPLETE);

        //then
        assertEquals(2, inquiryListIncomplete.size());
        assertEquals(1, inquiryListComplete.size());

    }

    @Test
    public void 회원별_상태별_조회() throws Exception {
        //given
        Strategy strategy = createStrategy();
        Member member = createMember();

        Long memberId = member.getId();

        Inquiry inquiry1 = Inquiry.createInquiry(strategy, member, "질문1", "내용1");
        inquiryService.saveInquiry(inquiry1);

        Inquiry inquiry2 = Inquiry.createInquiry(strategy, member, "질문2", "내용2");
        inquiryService.saveInquiry(inquiry2);

        Inquiry inquiry3 = Inquiry.createInquiry(strategy, member, "질문3", "내용3");
        inquiryService.saveInquiry(inquiry3);

        //when
        List<Inquiry> inquiryListIncomplete = inquiryService.findMemberStatusInquires(memberId, InquiryStatus.INCOMPLETE);
        List<Inquiry> inquiryListComplete = inquiryService.findMemberStatusInquires(memberId, InquiryStatus.COMPLETE);

        //then
        assertEquals(2, inquiryListIncomplete.size());
        assertEquals(1, inquiryListComplete.size());

    }

    @Test
    public void 문의_등록() throws Exception {
        //given
        Strategy strategy = createStrategy();
        Member member = createMember();

        //when
        Long savedId = inquiryService.registerInquiry(member.getId(), strategy.getId(), "질문1", "내용1");

        //then
        assertEquals("질문1", inquiryRepository.findOne(savedId).getInquiryTitle());
    }

    @Test
    public void 문의_수정() throws Exception {
        //given

        //when

        //then

    }

    @Test
    public void 문의_삭제() throws Exception {
        //given

        //when

        //then

    }

    @Test
    public void 문의_검색() throws Exception {
        //given
        InquirySearch inquirySearch = new InquirySearch();
        inquirySearch.setStrategyKeyword("삼성");
//        inquirySearch.setQuestionerKeyword(" ");
//        inquirySearch.setTraderKeyword(" ");

        Strategy strategy = createStrategy();
        Member member = createMember();

        Inquiry inquiry1 = Inquiry.createInquiry(strategy, member, "질문1", "내용1");
        inquiryService.saveInquiry(inquiry1);

        Inquiry inquiry2 = Inquiry.createInquiry(strategy, member, "질문2", "내용2");
        inquiryService.saveInquiry(inquiry2);

        Inquiry inquiry3 = Inquiry.createInquiry(strategy, member, "질문3", "내용3");
        inquiryService.saveInquiry(inquiry3);

        //when
        List<Inquiry> inquiryList = inquiryService.findInquiresByStrategyQuestionerTrader(inquirySearch);

        //then
        assertEquals(3, inquiryList.size());
    }

    private Member createMember() {
        Member member = new Member();
//        member.setId(1L);
        member.setRoleCode("UR001");
        member.setEmail("user@gmail.com");
        member.setPassword("123456");
        member.setName("송중기");
        member.setNickname("유시진11");
        member.setPhoneNumber("01012345678");
        member.setUsingStatusCode("MS002");
        member.setTotalFollow(39);
        member.setReceiveInfoConsent("Y");
        member.setInfoConsentDate(LocalDateTime.now());
        member.setReceiveMarketingConsent("Y");
        member.setMarketingConsentDate(LocalDateTime.now());
        member.setCreatedBy(1L);
        member.setCreatedDate(LocalDateTime.now());
        member.setModifiedBy(1L);
        member.setModifiedDate(LocalDateTime.now());
        em.persist(member);
        return member;
    }

    private Method createMethod() {
        Method method = new Method();
        method.setName("Manual");
        method.setStatusCode("StatusCode");
        method.setExplanation("Explanation");
        method.setMethodCreatedDate(LocalDateTime.now());
        method.setCreatedBy(1L);
        method.setCreatedDate(LocalDateTime.now());
        method.setModifiedBy(1L);
        method.setModifiedDate(LocalDateTime.now());
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
        strategy.setMinOperationAmount(1.1);
        strategy.setContent("내용");
        strategy.setFollowerCount(36L);
        strategy.setKpRatio(2.2);
        strategy.setSmScore(3.3);
        strategy.setStrategyCreatedDate(LocalDateTime.now());
        strategy.setStrategyModifiedDate(LocalDateTime.now());
        strategy.setCreatedBy(1L);
        strategy.setCreatedDate(LocalDateTime.now());
        strategy.setModifiedBy(1L);
        strategy.setModifiedDate(LocalDateTime.now());
        em.persist(strategy);
        return strategy;
    }
}