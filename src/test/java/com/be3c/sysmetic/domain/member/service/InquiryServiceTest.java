package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.dto.InquiryAdminListShowRequestDto;
import com.be3c.sysmetic.domain.member.dto.InquiryListShowRequestDto;
import com.be3c.sysmetic.domain.member.entity.Inquiry;
import com.be3c.sysmetic.domain.member.entity.InquiryStatus;
import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.repository.InquiryRepository;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.domain.strategy.entity.Method;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(locations = "/application-test.properties")
@SpringBootTest
@Transactional
public class InquiryServiceTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    InquiryService inquiryService;
    @Autowired
    InquiryRepository inquiryRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void 문의_등록() throws Exception {
        //given
        Strategy strategy = createStrategy("삼성전자");
        Member member = createMember("닉네임");

        //when
        inquiryService.registerInquiry(member.getId(), strategy.getId(), "문의제목1", "문의내용1");
        Inquiry inquiry = inquiryRepository.findByInquiryTitle("문의제목1").get(0);

        //then
        assertEquals("문의내용1", inquiry.getInquiryContent());
    }

    @Test
    public void 문의_수정() throws Exception {
        //given
        Strategy strategy = createStrategy("삼성전자");
        Member member = createMember("닉네임");
        inquiryService.registerInquiry(member.getId(), strategy.getId(), "문의제목1", "문의내용1");

        //when
        Inquiry inquiry = inquiryRepository.findByInquiryTitle("문의제목1").get(0);
        inquiryService.modifyInquiry(inquiry.getId(), "수정문의제목1", "수정문의내용1");

        //then
        assertEquals("수정문의제목1", inquiry.getInquiryTitle());
        assertEquals("수정문의내용1", inquiry.getInquiryContent());
    }

    @Test
    public void 문의_삭제() throws Exception {
        //given
        Strategy strategy = createStrategy("삼성전자");
        Member member = createMember("닉네임");
        inquiryService.registerInquiry(member.getId(), strategy.getId(), "문의제목1", "문의내용1");

        //when
        Inquiry inquiry = inquiryRepository.findByInquiryTitle("문의제목1").get(0);
        inquiryService.deleteInquiry(inquiry.getId());

        //then
        assertTrue(inquiryRepository.findById(inquiry.getId()).isEmpty());

    }

    @Test
    public void 문의_목록_삭제() throws Exception {
        //given
        Strategy strategy1 = createStrategy("삼성전자");
        Strategy strategy2 = createStrategy("LG전자");
        Member member = createMember("닉네임");

        inquiryService.registerInquiry(member.getId(), strategy1.getId(), "질문1", "내용1");
        inquiryService.registerInquiry(member.getId(), strategy2.getId(), "질문2", "내용2");
        inquiryService.registerInquiry(member.getId(), strategy2.getId(), "질문3", "내용3");
        inquiryService.registerInquiry(member.getId(), strategy2.getId(), "질문4", "내용4");
        inquiryService.registerInquiry(member.getId(), strategy1.getId(), "질문5", "내용5");
        inquiryService.registerInquiry(member.getId(), strategy1.getId(), "질문6", "내용6");

        //when
        Inquiry inquiry1 = inquiryRepository.findByInquiryTitle("질문1").get(0);
        Inquiry inquiry2 = inquiryRepository.findByInquiryTitle("질문2").get(0);
        Inquiry inquiry3 = inquiryRepository.findByInquiryTitle("질문3").get(0);
        Inquiry inquiry4 = inquiryRepository.findByInquiryTitle("질문4").get(0);
        Inquiry inquiry5 = inquiryRepository.findByInquiryTitle("질문5").get(0);
        Inquiry inquiry6 = inquiryRepository.findByInquiryTitle("질문6").get(0);
        List<Long> idList = new ArrayList<>();
        idList.add(inquiry1.getId());
        idList.add(inquiry2.getId());
        idList.add(inquiry3.getId());
        idList.add(inquiry4.getId());
        inquiryService.deleteAdminInquiryList(idList);

        //then
        assertTrue(inquiryRepository.findById(inquiry1.getId()).isEmpty());
        assertTrue(inquiryRepository.findById(inquiry2.getId()).isEmpty());
        assertTrue(inquiryRepository.findById(inquiry3.getId()).isEmpty());
        assertTrue(inquiryRepository.findById(inquiry4.getId()).isEmpty());
        assertTrue(inquiryRepository.findById(inquiry5.getId()).isPresent());
        assertTrue(inquiryRepository.findById(inquiry6.getId()).isPresent());

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

        Strategy strategy = createStrategy("삼성전자");
        Member member = createMember("닉네임");

        inquiryService.registerInquiry(member.getId(), strategy.getId(), "질문1", "내용1");
        inquiryService.registerInquiry(member.getId(), strategy.getId(), "질문2", "내용2");
        inquiryService.registerInquiry(member.getId(), strategy.getId(), "질문3", "내용3");
        inquiryService.registerInquiry(member.getId(), strategy.getId(), "질문4", "내용4");
        Inquiry inquiry1 = inquiryRepository.findByInquiryTitle("질문4").get(0);
        inquiry1.setInquiryStatus(InquiryStatus.closed);
        inquiryService.registerInquiry(member.getId(), strategy.getId(), "질문5", "내용5");
        Inquiry inquiry2 = inquiryRepository.findByInquiryTitle("질문5").get(0);
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
        Strategy strategy1 = createStrategy("삼성전자");
        Strategy strategy2 = createStrategy("LG전자");
        Member member1 = createMember("닉네임1");
        Member member2 = createMember("닉네임2");

        InquiryListShowRequestDto inquirySearch1 = new InquiryListShowRequestDto();
        inquirySearch1.setSort("registrationDate");
        inquirySearch1.setTab(InquiryStatus.all);

        InquiryListShowRequestDto inquirySearch2 = new InquiryListShowRequestDto();
        inquirySearch2.setSort("strategyName");
        inquirySearch2.setTab(InquiryStatus.all);

        InquiryListShowRequestDto inquirySearch3 = new InquiryListShowRequestDto();
        Long memberId1 = memberRepository.findByNickname("닉네임1").get().getId();
        inquirySearch3.setInquirerId(memberId1);
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


    private Member createMember(String nickName) {
        Member member = new Member();
//        member.setId(1L);
        member.setRoleCode("UR001");
        member.setEmail("user@gmail.com");
        member.setPassword("123456");
        member.setName("송중기");
        member.setNickname(nickName);
        member.setBirth(LocalDateTime.now().toLocalDate());
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

    private Strategy createStrategy(String name) {
        Strategy strategy = new Strategy();
        strategy.setTrader(createMember("닉네임"));
        strategy.setMethod(createMethod());
        strategy.setStatusCode("ST002");
        strategy.setName(name);
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