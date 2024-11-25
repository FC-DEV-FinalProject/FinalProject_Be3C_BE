package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.repository.NoticeRepository;
import com.be3c.sysmetic.domain.strategy.entity.Method;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
        Long savedId = noticeService.registerNotice(member.getId(), "공지제목1", "공지내용1", 0, 1);

        //then
        assertEquals("공지제목1", noticeRepository.findById(savedId).get().getNoticeTitle());
        assertEquals("공지내용1", noticeRepository.findById(savedId).get().getNoticeContent());
    }

    @Test
    public void 공지사항_수정() throws Exception {
        //given
        Member member = createMember();
        Long savedId = noticeService.registerNotice(member.getId(), "공지제목1", "공지내용1", 0, 1);

        //when
        noticeService.modifyNotice(savedId, "수정공지제목1", "수정공지내용1", 10L, 0, 1);

        //then
        assertEquals("수정공지제목1", noticeRepository.findById(savedId).get().getNoticeTitle());
        assertEquals("수정공지내용1", noticeRepository.findById(savedId).get().getNoticeContent());
        assertEquals(10L, noticeRepository.findById(savedId).get().getCorrectorId());
    }

    @Test
    public void 공지사항_공개여부_수정() throws Exception {
        //given
        Member member = createMember();
        Long savedId = noticeService.registerNotice(member.getId(), "공지제목1", "공지내용1", 0, 1);

        //when
        noticeService.modifyNoticeClosed(savedId);

        //then
        assertEquals(0, noticeRepository.findById(savedId).get().getIsOpen());
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