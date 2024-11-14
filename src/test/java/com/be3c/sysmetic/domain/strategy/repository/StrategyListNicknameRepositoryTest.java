package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.strategy.dto.TraderNicknameListDto;
import com.be3c.sysmetic.domain.strategy.entity.Method;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = StrategyListNicknameRepositoryTest.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "/application-test.properties")
public class StrategyListNicknameRepositoryTest {


    @Autowired
    StrategyListRepository strategyListRepository;

    @Autowired
    StrategyRepository strategyRepository;

    @Autowired
    MethodRepository methodRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @BeforeEach
    public void init() {
        em.createNativeQuery("ALTER TABLE sysmetictest.strategy AUTO_INCREMENT = 1")
                .executeUpdate();
        // strategyListRepository 데이터 모두 삭제
        strategyListRepository.deleteAll();

        // 각 메서드를 호출하여 데이터 추가
        saveMember();
        saveMethod("Manual");
    }

    @Test
    @DisplayName("트레이더1이란 닉네임으로 첫 페이지 조회")
    @Transactional
    @Rollback(false)
    public void findByTraderNicknameTest() {
        // before : 현재 데이터베이스 비우기
        strategyListRepository.deleteAll();
        memberRepository.deleteAll();
        assertTrue(strategyListRepository.findAll().isEmpty());

        // 난수는 [1, 100]
        int randomNum = (int) (Math.random() * 100) + 1;
        System.out.println("randomNum = " + randomNum);
        // 트레이더 생성 후 저장 (트레이더 1번부터 시작)
        for (int i=0; i < randomNum; i++)
            saveMember("트레이더" + (i + 1));

        // 트레이더 수만큼 전략 생성 후 저장
        for (int i=0; i < randomNum; i++) {
            Strategy s = Strategy.builder()
                    .trader(getTrader("트레이더" + (i + 1)))
                    .method(getMethod())
                    .statusCode("ST001")
                    .name("전략" + (i+1))
                    .cycle('P')
                    .content("전략" + (i + 1) + " 소개 내용")
                    .followerCount((long) (Math.random() * 100))
                    .accumProfitLossRate(Math.random() * 100)
                    .build();
            strategyRepository.saveAndFlush(s);        // 저장할 때는 하나씩 등록하니까 StrategyRepository 사용해서 하나씩 등록
        }

        Pageable pageable = PageRequest.of(0, 10);
        // "트레이더1"를 포함하는 닉네임을 가지면 전부 조회되어야 함
        Page<TraderNicknameListDto> findTrader = strategyListRepository.findDistinctByTraderNickname("트레이더1", pageable);
        assertNotNull(findTrader);
        assertTrue(findTrader.hasContent());
        assertEquals(findTrader.getSize(), 10);

        for (TraderNicknameListDto t : findTrader) {
            System.out.println("nickname = " + t.getNickname());
            assertTrue(t.getNickname().contains("트레이더1"));
        }
    }


    @Test
    @DisplayName("특정 닉네임을 가진 트레이더 목록 전부 조회")
    @Transactional
    @Rollback(false)
    public void findAllByTraderNickname() {
        // before : 현재 데이터베이스 비우기
        strategyListRepository.deleteAll();
        memberRepository.deleteAll();
        assertTrue(strategyListRepository.findAll().isEmpty());

        // 난수는 [1, 50]
        int randomNum = (int) (Math.random() * 50) + 1;
        System.out.println("randomNum = " + randomNum);

        saveMember("강남부자");
        saveMember("여의도전략가");

        // 트레이더 수만큼 전략 생성 후 저장
        for (int i=0; i < randomNum; i++) {
            Strategy s = Strategy.builder()
                    .trader(i % 2 == 0 ? getTrader("강남부자") : getTrader("여의도전략가"))
                    .method(getMethod())
                    .statusCode("ST001")
                    .name("전략" + (i+1))
                    .cycle('P')
                    .content("전략" + (i + 1) + " 소개 내용")
                    .followerCount((long) ((Math.random() * 100) + 1))
                    .accumProfitLossRate(Math.random() * 100)
                    .build();
            strategyRepository.saveAndFlush(s);        // 저장할 때는 하나씩 등록하니까 StrategyRepository 사용해서 하나씩 등록
        }

        int expectedTotalPage = (int) Math.ceil(randomNum / 10.0);

        // "여의도"를 가진 트레이더는 전체 randonNum / 10 / 2의 올림 만큼의 페이지를 채울 수 있음
        for (int i=0; i < (int) Math.ceil(expectedTotalPage / 2.0) ; i++) {
            // Pageable 생성
            Pageable pageable = strategyListRepository.getPageable(i, "totalStrategyCount");

            // 트레이더 닉네임으로 조회
            Page<TraderNicknameListDto> page = strategyListRepository.findDistinctByTraderNickname("여의도", pageable);
            assertNotNull(page);

            for (TraderNicknameListDto t : page) {
                assertTrue(t.getNickname().contains("여의도"));
                assertNotNull(t.getTotalFollow());
                System.out.println("nickname = " + t.getNickname());
            }
            System.out.println("=====" + pageable.getPageNumber() + "=====");
        }
    }

    @Test
    @DisplayName("특정 닉네임을 가진 트레이더 존재하지 않음")
    @Transactional
    @Rollback(false)
    public void failToFindTrader(){
        // before : 현재 데이터베이스 비우기
        strategyListRepository.deleteAll();
        assertTrue(strategyListRepository.findAll().isEmpty());

        saveMember("강남");

        // 트레이더 수만큼 전략 생성 후 저장
        Strategy s = Strategy.builder()
                .trader(getTrader("강남"))
                .method(getMethod())
                .statusCode("ST001")
                .name("전략")
                .cycle('P')
                .content("전략 소개 내용")
                .followerCount((long) (Math.random() * 100))
                .accumProfitLossRate(Math.random() * 100)
                .build();
        strategyRepository.saveAndFlush(s);

        Pageable pageable = PageRequest.of(0, 10);
        Page<TraderNicknameListDto> page = strategyListRepository.findDistinctByTraderNickname("여의도", pageable);
        assertFalse(page.hasContent());
    }


    void saveMember() {
        // Member trader 생성
        Member trader = Member.builder()
                .roleCode("trader")
                .email("trader1@gmail.com")
                .password("1234")
                .name("홍길동")
                .nickname("김아무개")
                .phoneNumber("01012341234")
                .usingStatusCode("using status code")
                .totalFollow(0)
                .totalStrategyCount(0)
                .receiveInfoConsent("Yes")
                .infoConsentDate(LocalDateTime.now())
                .receiveMarketingConsent("NO")
                .marketingConsentDate(LocalDateTime.now())
                .createdBy(1L)
                .createdDate(LocalDateTime.now())
                .modifiedBy(1L)
                .modifiedDate(LocalDateTime.now())
                .build();
        // trader 저장
        memberRepository.save(trader);
    }



    void saveMember(String nickname) {
        // Member trader 생성
        Member trader = Member.builder()
                .roleCode("trader")
                .email("trader1@gmail.com")
                .password("1234")
                .name("홍길동")
                .nickname(nickname)
                .phoneNumber("01012341234")
                .usingStatusCode("using status code")
                .totalFollow(0)
                .totalStrategyCount(0)
                .receiveInfoConsent("Yes")
                .infoConsentDate(LocalDateTime.now())
                .receiveMarketingConsent("NO")
                .marketingConsentDate(LocalDateTime.now())
                .createdBy(1L)
                .createdDate(LocalDateTime.now())
                .modifiedBy(1L)
                .modifiedDate(LocalDateTime.now())
                .build();
        // trader 저장
        memberRepository.save(trader);
    }

    void saveMethod(String name){
        // Method 객체 생성
        Method method = Method.builder()
                // .name("Manual")
                .name(name)
                .statusCode("MS001")
                .methodCreatedDate(LocalDateTime.now())
                .createdBy(1L)
                .createdDate(LocalDateTime.now())
                .modifiedBy(1L)
                .modifiedDate(LocalDateTime.now())
                .build();
        // method 저장
        methodRepository.save(method);
    }

    Member getTrader() {
        return memberRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new NoSuchElementException("트레이더가 없습니다."));
    }

    Member getTrader(String nickname) {
        return memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new NoSuchElementException("해당 트레이더가 없습니다."));
        // .orElseGet(this::getTrader);
    }

    Method getMethod(){
        return methodRepository.findAll().stream().
                findFirst().
                orElseThrow(() -> new NoSuchElementException("매매방식이 없습니다."));
    }
}
