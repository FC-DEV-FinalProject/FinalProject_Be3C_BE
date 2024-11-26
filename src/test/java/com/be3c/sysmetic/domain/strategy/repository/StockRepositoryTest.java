package com.be3c.sysmetic.domain.strategy.repository;


import com.be3c.sysmetic.domain.strategy.dto.StockGetResponseDto;
import com.be3c.sysmetic.domain.strategy.entity.Stock;
import com.be3c.sysmetic.global.common.Code;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestPropertySource(locations = "/application-test.properties")
@Slf4j
@SpringBootTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StockRepositoryTest {
    @Autowired
    StockRepository stockRepository;

    @Autowired
    EntityManager entityManager;

    @BeforeEach
    public void setUp() {
        stockRepository.deleteAll();
        entityManager.createNativeQuery("ALTER TABLE stock AUTO_INCREMENT = 1")
                .executeUpdate();
    }

    @AfterEach
    public void setDown() {
        stockRepository.deleteAll();
    }

    @Test
    @DisplayName("아이디로_종목_찾기")
    @Rollback(true)
    @Order(1)
    public void findById() {
        // given
        ArrayList<Stock> stocks = new ArrayList<>();
        long create_rand = (long) (Math.random() * 1000);
        log.info("생성된 테스트 종목 수 : {}개", create_rand);

        for (int i = 0; i < create_rand; i++) {
            stocks.add(Stock.builder()
                    .name("테스트" + (i + 1))
                    .statusCode(Code.USING_STATE.getCode())
                    .build());

            stockRepository.save(stocks.get(i));

            Optional<Stock> stock = stockRepository
                    .findByIdAndStatusCode((long) i + 1, Code.USING_STATE.getCode());

            assertThat(stock).isPresent();
            assertThat(stock.get().getName()).isEqualTo("테스트" + (i + 1));
        }

        int test_count = 0;
        while (test_count < 10) {
            // when
            long find_rand = (long) (Math.random() * create_rand);
//            log.info("찾아본 테스트 종목 번호 : {}번", find_rand);

            // then
            Optional<Stock> stock = stockRepository.findByIdAndStatusCode(find_rand + 1, Code.USING_STATE.getCode());
            assertThat(stock).isPresent();
            assertThat(stock.get().getName()).isEqualTo("테스트" + (find_rand + 1));

            test_count++;
        }
    }

    @Test
    @DisplayName("아이디로_종목_찾기_실패")
    @Rollback(true)
    @Order(2)
    public void failToFindById() {
        // given
        ArrayList<Stock> stocks = new ArrayList<>();
        long create_rand = (long) (Math.random() * 1000);
//        log.info("생성된 테스트 종목 수 : {}개", create_rand);

        for (int i = 0; i < create_rand; i++) {
            stocks.add(Stock.builder()
                    .name("테스트" + (i + 1))
                    .statusCode(Code.USING_STATE.getCode())
                    .build());

            stockRepository.save(stocks.get(i));

            Optional<Stock> stock = stockRepository
                    .findByIdAndStatusCode((long) i + 1, Code.USING_STATE.getCode());

            assertThat(stock).isPresent();
            assertThat(stock.get().getName()).isEqualTo("테스트" + (i + 1));
        }

        // then
        Optional<Stock> stock = stockRepository.findByIdAndStatusCode(create_rand + 11, Code.USING_STATE.getCode());
        assertThrows(NoSuchElementException.class, stock::get);
    }

    @Test
    @DisplayName("중복_확인_실패")
    @Rollback(true)
    @Order(3)
    public void findByName() {
        // given
        ArrayList<Stock> stocks = new ArrayList<>();
        long create_rand = (long) (Math.random() * 1000);
//        log.info("생성된 테스트 종목 수 : {}개", create_rand);

        for (int i = 0; i < create_rand; i++) {
            stocks.add(Stock.builder()
                    .name("테스트" + (i + 1))
                    .statusCode(Code.USING_STATE.getCode())
                    .build());

            stockRepository.save(stocks.get(i));

            Optional<Stock> stock = stockRepository
                    .findByIdAndStatusCode((long) i + 1, Code.USING_STATE.getCode());

            assertThat(stock).isPresent();
            assertThat(stock.get().getName()).isEqualTo("테스트" + (i + 1));
        }

        int test_count = 0;
        while (test_count < 10) {
            // when
            long find_rand = (long) (Math.random() * create_rand);
//            log.info("찾아본 테스트 종목 번호 : {}번", find_rand);

            Optional<Stock> stock = stockRepository.findByNameAndStatusCode(
                    stocks.get((int) find_rand).getName(), Code.USING_STATE.getCode()
            );
            // then
            assertThat(stock).isPresent();

            test_count++;
        }
    }

    @Test
    @DisplayName("중복_확인_통과")
    @Rollback(true)
    @Order(4)
    public void failToFindByName() {
        // given
        ArrayList<Stock> stocks = new ArrayList<>();
        long create_rand = (long) (Math.random() * 1000);
//        log.info("생성된 테스트 종목 수 : {}개", create_rand);

        for (int i = 0; i < create_rand; i++) {
            stocks.add(Stock.builder()
                    .name("테스트" + (i + 1))
                    .statusCode(Code.USING_STATE.getCode())
                    .build());

            stockRepository.save(stocks.get(i));

            Optional<Stock> stock = stockRepository
                    .findByIdAndStatusCode((long) i + 1, Code.USING_STATE.getCode());

            assertThat(stock).isPresent();
            assertThat(stock.get().getName()).isEqualTo("테스트" + (i + 1));
        }

        // then
        Optional<Stock> stock = stockRepository.findByNameAndStatusCode("이상한_값", Code.USING_STATE.getCode());
        assertThrows(NoSuchElementException.class, stock::get);
    }

    @Test
    @DisplayName("페이징 찾기 테스트")
    @Rollback(true)
    @Order(5)
    public void findPage() {
        // given
        ArrayList<Stock> stocks = new ArrayList<>();
        long create_rand = (long) (Math.random() * 1000) + 10;
        log.info("생성된 테스트 종목 수 : {}개", create_rand);

        long total_page = (create_rand / 10) + (create_rand % 10 == 0 ? 0 : 1);
        log.info("총 페이지 수 : {}", total_page);

        for (int i = 0; i < create_rand; i++) {
            stocks.add(Stock.builder()
                    .name("테스트" + (i + 1))
                    .statusCode(Code.USING_STATE.getCode())
                    .build());

            stockRepository.save(stocks.get(i));

            Optional<Stock> stock = stockRepository
                    .findByIdAndStatusCode((long) i + 1, Code.USING_STATE.getCode());
            assertThat(stock).isPresent();
            assertThat(stock.get().getName()).isEqualTo("테스트" + (i + 1));
        }

        // when
        int find_page = (int) (Math.random() * total_page);
        log.info("찾는 페이지 수 : {}", find_page);
        Pageable pageable = PageRequest.of(
                find_page,
                10,
                Sort.by("createdAt"
                ).ascending());

        Page<StockGetResponseDto> stockPage = stockRepository
                .findAllByStatusCode(Code.USING_STATE.getCode(), pageable);

        // then
        assertThat(stockPage).isNotNull();
        assertThat(stockPage.getTotalPages()).isEqualTo(total_page);
        AtomicLong counter = new AtomicLong(1L);

        stockPage.getContent().forEach(stock -> {
            log.info("테스트 진행 : {}", stock.toString());
            assertThat(stock.getId())
                    .isEqualTo(find_page * 10L + counter.get());
            assertThat(stock.getName())
                    .isEqualTo("테스트" + (find_page * 10L + counter.getAndIncrement()));
        });
    }

    @Test
    @DisplayName("페이징 찾기 실패 테스트")
    @Rollback(true)
    @Order(6)
    public void findPageFail() {
        // given
        ArrayList<Stock> stocks = new ArrayList<>();
        long create_rand = (long) (Math.random() * 1000) + 10;
//        log.info("생성된 테스트 종목 수 : {}개", create_rand);

        long total_page = (create_rand / 10) + (create_rand % 10 == 0 ? 0 : 1);
//        log.info("총 페이지 수 : {}", total_page);

        for (int i = 0; i < create_rand; i++) {
            stocks.add(Stock.builder()
                    .name("테스트" + (i + 1))
                    .statusCode(Code.USING_STATE.getCode())
                    .build());

            stockRepository.save(stocks.get(i));

            Optional<Stock> stock = stockRepository.findByIdAndStatusCode
                    ((long) i + 1, Code.USING_STATE.getCode());
            assertThat(stock).isPresent();
            assertThat(stock.get().getName()).isEqualTo("테스트" + (i + 1));
        }

        // when
        log.info("찾는 페이지 수 : {}", total_page + 11);
        Pageable pageable = PageRequest.of(
                (int) total_page + 1,
                10,
                Sort.by("createdAt"
                ).descending());

        Page<StockGetResponseDto> stockPage = stockRepository
                .findAllByStatusCode(Code.USING_STATE.getCode(), pageable);

        // then
        assertThrows(NoSuchElementException.class, () -> {
            if(!stockPage.hasContent()) {
                throw new NoSuchElementException();
            }
        });
    }
}

