package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.entity.*;
import com.be3c.sysmetic.domain.member.repository.NoticeRepository;
import com.be3c.sysmetic.domain.strategy.entity.Method;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.security.BasicPermission;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    @Rollback(value = false)
    public void dummy_data() throws Exception {
        Member member1 = createMember("테스트1");
        Member member2 = createMember("테스트2");
        Member member3 = createMember("테스트3");
        Member member4 = createMember("테스트4");
        Member member5 = createMember("테스트5");
//        Member member6 = createMember("닉네임6");
//        Member member7 = createMember("테스트3");
//        Member member8 = createMember("테스트4");

//        for (int i = 1; i < 65; i++) {
//            Member member = createMember("닉네임");
//            Notice notice = Notice.builder()
//                    .noticeTitle("공지제목")
//                    .noticeContent("공지내용")
//                    .writer(member)
//                    .writerNickname(member.getNickname())
//                    .writeDate(LocalDateTime.now())
//                    .correctorId(member.getId())
//                    .correctDate(LocalDateTime.now())
//                    .hits(0L)
//                    .fileExists(false)
//                    .imageExists(false)
//                    .isOpen(true)
//                    .build();
//            noticeRepository.save(notice);
//        }

//        int countNotice = 65;
        int countNotice = 1;
        Member member = null;
        Boolean isOpen = null;
        for(int i = 3; i <= 5; i++) {
            if (i == 3) { member = member3; }
            else if (i == 4) { member = member4; }
            else if (i == 5) { member = member5; }
            for(int j = 1; j <= 2; j++) {
                if (j == 1) { isOpen = false; }
                if (j == 2) { isOpen = true; }
                for(int l = 1; l <= 10; l++) {
                    Notice notice = Notice.builder()
                            .noticeTitle("공지제목" + countNotice)
                            .noticeContent("공지내용" + countNotice)
                            .writer(member)
                            .writerNickname(member.getNickname())
                            .writeDate(LocalDateTime.now())
                            .correctorId(member.getId())
                            .correctDate(LocalDateTime.now())
                            .hits(0L)
                            .fileExists(false)
                            .imageExists(false)
                            .isOpen(isOpen)
                            .build();
                    noticeRepository.save(notice);
                    countNotice++;
                }
            }
        }
    }

    @Test
    public void 공지사항_등록() throws Exception {
        //given
        Member member = createMember("닉네임");

        //when
        noticeService.registerNotice(member.getId(), "공지제목1", "공지내용1", false, new ArrayList<>(), new ArrayList<>());
        List<Notice> noticeList = noticeRepository.findByNoticeTitle("공지제목1");
        Notice notice = noticeList.get(0);

        //then
        assertEquals("공지내용1", notice.getNoticeContent());
    }

    @Test
    public void 공지사항_수정() throws Exception {
        //given
        Member member = createMember("닉네임");
        noticeService.registerNotice(member.getId(), "공지제목1", "공지내용1", false, null, null);

        //when
        List<Notice> noticeList = noticeRepository.findByNoticeTitle("공지제목1");
        Notice notice = noticeList.get(0);
        noticeService.modifyNotice(notice.getId(), "수정공지제목1", "수정공지내용1", 10L, false, null, null, null, null);

        //then
        assertEquals("수정공지제목1", notice.getNoticeTitle());
        assertEquals("수정공지내용1", notice.getNoticeContent());
        assertEquals(10L, notice.getCorrectorId());
    }

    @Test
    public void 공지사항_공개여부_수정() throws Exception {
        //given
        Member member = createMember("닉네임");
        noticeService.registerNotice(member.getId(), "공지제목1", "공지내용1", false, new ArrayList<>(), new ArrayList<>());

        //when
        List<Notice> noticeList = noticeRepository.findByNoticeTitle("공지제목1");
        Notice notice = noticeList.get(0);
        noticeService.modifyNoticeClosed(notice.getId());

        //then
        assertEquals(true, notice.getIsOpen());
    }

    @Test
    public void 공지사항_검색() throws Exception {
        //given
        Member member = createMember("닉네임");
        noticeService.registerNotice(member.getId(), "Lorem", "ipsum", true, null, null);
        noticeService.registerNotice(member.getId(), "dolore", "consectetur", true, null, null);
        noticeService.registerNotice(member.getId(), "adipiscing", "eiusmod", true, null, null);
        noticeService.registerNotice(member.getId(), "incididunt", "dolore", true, null, null);

        //when
        int page = 0;
        Page<Notice> noticeList1 = noticeService.findNotice("dolore", page);
        Page<Notice> noticeList2 = noticeService.findNotice("consectetur", page);

        //then
        assertEquals(2, noticeList1.getTotalElements());
        assertEquals(1, noticeList2.getTotalElements());
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
        strategy.setTrader(createMember("닉네임1"));
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