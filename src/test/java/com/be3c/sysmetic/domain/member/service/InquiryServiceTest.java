package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.dto.InquiryAdminListShowRequestDto;
import com.be3c.sysmetic.domain.member.dto.InquiryListShowRequestDto;
import com.be3c.sysmetic.domain.member.entity.Inquiry;
import com.be3c.sysmetic.domain.member.entity.InquiryAnswer;
import com.be3c.sysmetic.domain.member.entity.InquiryStatus;
import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.repository.InquiryAnswerRepository;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
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
    @Autowired
    private InquiryAnswerRepository inquiryAnswerRepository;

//    @Test
//    @Rollback(value = false)
//    public void dummy_data() throws Exception {
//        Member member1 = createMember("닉네임1");
//        Member member2 = createMember("닉네임2");
//        Member member3 = createMember("닉네임3");
//        Member member4 = createMember("닉네임4");
//        Member member5 = createMember("닉네임5");
//        Member member6 = createMember("닉네임6");
//        Member member7 = createMember("닉네임7");
//        Member member8 = createMember("닉네임8");
//        Strategy strategy1 = createStrategy("삼성전자");
//        Strategy strategy2 = createStrategy("LG전자");
//        Strategy strategy3 = createStrategy("애플");
//
//        int countInquiry = 1;
//        int countInquiryAnswer = 1;
//        Strategy strategy = null;
//        Member member = null;
//        InquiryStatus inquiryStatus = null;
//        for(int i = 1; i <= 3; i++) {
//            if (i == 1) { strategy = strategy1; }
//            else if (i == 2) { strategy = strategy2; }
//            else if (i == 3) { strategy = strategy3; }
//            for(int j = 6; j <= 8; j++) {
//                if (j == 6) { member = member6; }
//                else if (j == 7) { member = member7; }
//                else if (j == 8) { member = member8; }
//                for(int k = 1; k <= 2; k++) {
//                    if (k == 1) { inquiryStatus = InquiryStatus.closed; }
//                    if (k == 2) { inquiryStatus = InquiryStatus.unclosed; }
//                    for(int l = 1; l <= 3; l++) {
//                        Inquiry inquiry = Inquiry.builder()
//                                .strategy(strategy)
//                                .strategyName(strategy.getName())
//                                .inquirer(member)
//                                .traderNickname(strategy.getTrader().getNickname())
//                                .inquirerNickname(member.getNickname())
//                                .inquiryStatus(inquiryStatus)
//                                .inquiryTitle("문의제목" + countInquiry)
//                                .inquiryContent("문의내용" + countInquiry)
//                                .inquiryRegistrationDate(LocalDateTime.now())
//                                .build();
//                        inquiryRepository.save(inquiry);
//                        countInquiry++;
//                        if (inquiryStatus == InquiryStatus.closed) {
//                            InquiryAnswer inquiryAnswer = InquiryAnswer.builder()
//                                    .inquiry(inquiry)
//                                    .answerTitle("답변제목" + countInquiryAnswer)
//                                    .answerContent("답변내용" + countInquiryAnswer)
//                                    .answerRegistrationDate(LocalDateTime.now())
//                                    .build();
//                            inquiryAnswerRepository.save(inquiryAnswer);
//                            countInquiryAnswer++;
//                        }
//                    }
//                }
//            }
//        }
//    }


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
        inquiryService.registerInquiry(member.getId(), strategy.getId(), "문의제목수정1", "문의내용수정1");

        //when
        Inquiry inquiry = inquiryRepository.findByInquiryTitle("문의제목수정1").get(0);
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
        inquiryService.registerInquiry(member.getId(), strategy.getId(), "문의제목삭제1", "문의내용삭제1");

        //when
        Inquiry inquiry = inquiryRepository.findByInquiryTitle("문의제목삭제1").get(0);
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
        int page = 0;
        Page<Inquiry> inquiryList1 = inquiryService.findInquiriesAdmin(inquirySearch1, page);
        Page<Inquiry> inquiryList2 = inquiryService.findInquiriesAdmin(inquirySearch2, page);

        //then
        assertEquals(3, inquiryList1.getNumberOfElements());
        assertEquals(inquiryRepository.findByInquiryStatus(InquiryStatus.closed, PageRequest.of(0, 100)).getTotalElements(), inquiryList2.getTotalElements());
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
        int page = 0;
        Page<Inquiry> inquiryList1 = inquiryService.findInquiries(inquirySearch1, page);
        Page<Inquiry> inquiryList2 = inquiryService.findInquiries(inquirySearch2, page);
        Page<Inquiry> inquiryList3 = inquiryService.findInquiries(inquirySearch3, page);

        // then
        assertEquals("질문6", inquiryList1.getContent().get(0).getInquiryTitle());
        assertEquals("질문4", inquiryList2.getContent().get(0).getInquiryTitle());
        assertEquals("질문3", inquiryList3.getContent().get(0).getInquiryTitle());
    }


    private Member createMember(String nickName) {
        Member member = new Member();
        member.setRoleCode("USER");
        member.setEmail("user@gmail.com");
        member.setPassword("123456");
        member.setName("송중기");
        member.setNickname(nickName);
        member.setBirth(LocalDateTime.now().toLocalDate());
        member.setPhoneNumber("01012345678");
        member.setUsingStatusCode("UR001");
        member.setTotalFollow(39);
        member.setTotalStrategyCount(100);
        member.setReceiveInfoConsent("true");
        member.setInfoConsentDate(LocalDateTime.now());
        member.setReceiveMarketingConsent("true");
        member.setMarketingConsentDate(LocalDateTime.now());
        em.persist(member);
        return member;
    }

    private Method createMethod() {
        Method method = new Method();
        method.setName("DAY");
        method.setStatusCode("PUBLIC");
        method.setMethodCreatedDate(LocalDateTime.now());
        em.persist(method);
        return method;
    }

    private Strategy createStrategy(String name) {
        Strategy strategy = new Strategy();
        strategy.setTrader(createMember("트레이더닉네임1"));
        strategy.setMethod(createMethod());
        strategy.setStatusCode("PUBLIC");
        strategy.setName(name);
        strategy.setCycle('P');
        strategy.setContent("전략내용");
        strategy.setFollowerCount(36L);
        strategy.setMdd(1.1);
        strategy.setKpRatio(2.2);
        strategy.setSmScore(3.3);
        strategy.setWinningRate(4.4);
        strategy.setAccumulatedProfitLossRate(5.5);
        strategy.setStrategyCreatedDate(LocalDateTime.now());
        strategy.setStrategyModifiedDate(LocalDateTime.now());
        em.persist(strategy);
        return strategy;
    }
}