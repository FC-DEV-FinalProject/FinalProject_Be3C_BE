package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.entity.Inquiry;
import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.entity.Notice;
import com.be3c.sysmetic.domain.member.repository.NoticeRepository;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(locations = "/application-test.properties")
@SpringBootTest
@Transactional
class NoticeServiceTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    NoticeService noticeService;
    @Autowired
    NoticeRepository noticeRepository;

    @Test
    public void 공지사항_등록() throws Exception {
        //given
        Member member = createMember();

        //when
        noticeService.registerNotice(member.getId(), "공지제목1", "공지내용1", 0, 1);
        List<Notice> noticeList = noticeRepository.findByNoticeTitle("공지제목1");
        Notice notice = noticeList.get(0);

        //then
        assertEquals("공지내용1", notice.getNoticeContent());
    }

    @Test
    public void 공지사항_수정() throws Exception {
        //given
        Member member = createMember();
        noticeService.registerNotice(member.getId(), "공지제목1", "공지내용1", 0, 1);

        //when
        List<Notice> noticeList = noticeRepository.findByNoticeTitle("공지제목1");
        Notice notice = noticeList.get(0);
        noticeService.modifyNotice(notice.getId(), "수정공지제목1", "수정공지내용1", 10L, 0, 1);

        //then
        assertEquals("수정공지제목1", notice.getNoticeTitle());
        assertEquals("수정공지내용1", notice.getNoticeContent());
        assertEquals(10L, notice.getCorrectorId());
    }

    @Test
    public void 공지사항_공개여부_수정() throws Exception {
        //given
        Member member = createMember();
        noticeService.registerNotice(member.getId(), "공지제목1", "공지내용1", 0, 1);

        //when
        List<Notice> noticeList = noticeRepository.findByNoticeTitle("공지제목1");
        Notice notice = noticeList.get(0);
        noticeService.modifyNoticeClosed(notice.getId());

        //then
        assertEquals(0, notice.getIsOpen());
    }

    @Test
    public void 공지사항_검색() throws Exception {
        //given
        Member member = createMember();
        noticeService.registerNotice(member.getId(), "공지제목1", "공지내용1", 0, 1);
        noticeService.registerNotice(member.getId(), "공지제목2", "공지내용2", 0, 1);
        noticeService.registerNotice(member.getId(), "공지제목3", "공지내용3", 0, 1);
        noticeService.registerNotice(member.getId(), "공지제목4", "공지내용4", 0, 1);
        noticeService.registerNotice(member.getId(), "공지제목5", "공지내용5", 0, 1);

        //when
        int page = 1;
        Page<Notice> noticeList1 = noticeService.findNotice("내용", page-1);
        Page<Notice> noticeList2 = noticeService.findNotice("3", page-1);

        //then
        assertEquals(5, noticeList1.getNumberOfElements());
        assertEquals("공지제목5", noticeList1.getContent().get(0).getNoticeTitle());
        assertEquals(1, noticeList2.getNumberOfElements());
    }


    private Member createMember() {
        Member member = new Member();
//        member.setId(1L);
        member.setRoleCode("UR001");
        member.setEmail("user@gmail.com");
        member.setPassword("123456");
        member.setName("송중기");
        member.setNickname("유시진11");
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