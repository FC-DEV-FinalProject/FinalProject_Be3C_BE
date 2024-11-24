package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.dto.InquiryAdminListShowRequestDto;
import com.be3c.sysmetic.domain.member.dto.InquiryListShowRequestDto;
import com.be3c.sysmetic.domain.member.entity.Inquiry;
import com.be3c.sysmetic.domain.member.entity.InquiryStatus;
import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.repository.InquiryRepository;
import com.be3c.sysmetic.domain.strategy.entity.Method;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.Test;
//import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Test
    public void 문의_등록() throws Exception {
        //given
        Strategy strategy = createStrategy();
        Member member = createMember();

        //when
        Long savedId = inquiryService.registerInquiry(member.getId(), strategy.getId(), "문의제목1", "문의내용1");

        //then
        assertEquals("문의제목1", inquiryRepository.findById(savedId).get().getInquiryTitle());
        assertEquals("문의내용1", inquiryRepository.findById(savedId).get().getInquiryContent());
    }

    @Test
    public void 문의_수정() throws Exception {
        //given
        Strategy strategy = createStrategy();
        Member member = createMember();
        Long savedId = inquiryService.registerInquiry(member.getId(), strategy.getId(), "문의제목1", "문의내용1");

        //when
        inquiryService.modifyInquiry(savedId, "수정문의제목1", "수정문의내용1");

        //then
        assertEquals("수정문의제목1", inquiryRepository.findById(savedId).get().getInquiryTitle());
        assertEquals("수정문의내용1", inquiryRepository.findById(savedId).get().getInquiryContent());
    }

    @Test
    public void 문의_삭제() throws Exception {
        //given
        Strategy strategy = createStrategy();
        Member member = createMember();
        Long savedId = inquiryService.registerInquiry(member.getId(), strategy.getId(), "문의제목1", "문의내용1");

        //when
        inquiryService.deleteInquiry(savedId);

        //then
        assertTrue(inquiryRepository.findById(savedId).isEmpty());

    }

    @Test
    public void 문의_목록_삭제() throws Exception {
        //given
        Strategy strategy1 = createStrategy();
        Strategy strategy2 = createStrategy();
        Member member = createMember();

        inquiryService.registerInquiry(member.getId(), strategy1.getId(), "질문1", "내용1");
        inquiryService.registerInquiry(member.getId(), strategy2.getId(), "질문2", "내용2");
        inquiryService.registerInquiry(member.getId(), strategy2.getId(), "질문3", "내용3");
        inquiryService.registerInquiry(member.getId(), strategy2.getId(), "질문4", "내용4");
        inquiryService.registerInquiry(member.getId(), strategy1.getId(), "질문5", "내용5");
        inquiryService.registerInquiry(member.getId(), strategy1.getId(), "질문6", "내용6");

        //when
        List<Long> idList = new ArrayList<>();
        idList.add(1L);
        idList.add(2L);
        idList.add(3L);
        idList.add(4L);
        inquiryService.deleteAdminInquiryList(idList);

        //then
        assertTrue(inquiryRepository.findById(1L).isEmpty());
        assertTrue(inquiryRepository.findById(2L).isEmpty());
        assertTrue(inquiryRepository.findById(3L).isEmpty());
        assertTrue(inquiryRepository.findById(4L).isEmpty());
        assertTrue(inquiryRepository.findById(5L).isPresent());
        assertTrue(inquiryRepository.findById(6L).isPresent());

    }

    @Test
    public void 관리자_문의_검색() throws Exception {
        //given
        InquiryAdminListShowRequestDto inquirySearch1 = new InquiryAdminListShowRequestDto();
        inquirySearch1.setTab(InquiryStatus.unclosed);
        inquirySearch1.setSearchType("strategy");
        inquirySearch1.setSearchText("삼성전");

        InquiryAdminListShowRequestDto inquirySearch2 = new InquiryAdminListShowRequestDto();
        inquirySearch2.setTab(InquiryStatus.closed);
        inquirySearch2.setSearchType("strategy");
        inquirySearch2.setSearchText("");

        Strategy strategy = createStrategy();
        Member member = createMember();

        inquiryService.registerInquiry(member.getId(), strategy.getId(), "질문1", "내용1");
        inquiryService.registerInquiry(member.getId(), strategy.getId(), "질문2", "내용2");
        inquiryService.registerInquiry(member.getId(), strategy.getId(), "질문3", "내용3");
        Long inquiryId1 = inquiryService.registerInquiry(member.getId(), strategy.getId(), "질문4", "내용4");
        Inquiry inquiry1 = inquiryRepository.findById(inquiryId1).get();
        inquiry1.setInquiryStatus(InquiryStatus.closed);
        Long inquiryId2 = inquiryService.registerInquiry(member.getId(), strategy.getId(), "질문5", "내용5");
        Inquiry inquiry2 = inquiryRepository.findById(inquiryId2).get();
        inquiry2.setInquiryStatus(InquiryStatus.closed);

        //when
        int page = 1;
        Page<Inquiry> inquiryList1 = inquiryService.findInquiresAdmin(inquirySearch1, page-1);
        Page<Inquiry> inquiryList2 = inquiryService.findInquiresAdmin(inquirySearch2, page-1);

        //then
        assertEquals(3, inquiryList1.getNumberOfElements());
        assertEquals(2, inquiryList2.getNumberOfElements());
    }

    @Test
    public void 문의_검색() throws Exception {
        //given
        Strategy strategy1 = createStrategy();
        Strategy strategy2 = createStrategy();
        strategy2.setName("LG전자");
        Member member1 = createMember();
        Member member2 = createMember();

        InquiryListShowRequestDto inquirySearch1 = new InquiryListShowRequestDto();
        inquirySearch1.setSort("registrationDate");
        inquirySearch1.setTab(InquiryStatus.all);

        InquiryListShowRequestDto inquirySearch2 = new InquiryListShowRequestDto();
        inquirySearch2.setSort("strategyName");
        inquirySearch2.setTab(InquiryStatus.all);

        InquiryListShowRequestDto inquirySearch3 = new InquiryListShowRequestDto();
        inquirySearch3.setInquirerId(member1.getId());
        inquirySearch3.setSort("strategyName");
        inquirySearch3.setTab(InquiryStatus.all);

        inquiryService.registerInquiry(member1.getId(), strategy1.getId(), "질문1", "내용1");
        inquiryService.registerInquiry(member1.getId(), strategy2.getId(), "질문2", "내용2");
        inquiryService.registerInquiry(member1.getId(), strategy2.getId(), "질문3", "내용3");
        inquiryService.registerInquiry(member2.getId(), strategy2.getId(), "질문4", "내용4");
        inquiryService.registerInquiry(member2.getId(), strategy1.getId(), "질문5", "내용5");
        inquiryService.registerInquiry(member2.getId(), strategy1.getId(), "질문6", "내용6");

        //when
        int page = 1;
        Page<Inquiry> inquiryList1 = inquiryService.findInquires(inquirySearch1, page-1);
        Page<Inquiry> inquiryList2 = inquiryService.findInquires(inquirySearch2, page-1);
        Page<Inquiry> inquiryList3 = inquiryService.findInquires(inquirySearch3, page-1);

        // then
        assertEquals(6, inquiryList1.getNumberOfElements());
        assertEquals("질문6", inquiryList1.getContent().get(0).getInquiryTitle());
        assertEquals("질문2", inquiryList2.getContent().get(0).getInquiryTitle());
        assertEquals("질문2", inquiryList3.getContent().get(0).getInquiryTitle());
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